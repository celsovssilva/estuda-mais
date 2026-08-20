package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.*;
import com.estudamais.backend.repository.QuestionRepository;
import com.estudamais.backend.repository.SimulationRepository;
import com.estudamais.backend.request.EnviarSimuladoRequest;
import com.estudamais.backend.request.PausarSimuladoRequest;
import com.estudamais.backend.request.RespostaItemRequest;
import com.estudamais.backend.response.GabaritoItemResponse;
import com.estudamais.backend.response.ResultadoSimuladoResponse;
import com.estudamais.backend.response.SimuladoPendenteResponse;
import com.estudamais.backend.service.EnemImportService;
import com.estudamais.backend.service.SimuladoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SimuladoServiceImpl implements SimuladoService {

    @Autowired
    private QuestionRepository questaoRepository;

    @Autowired
    private SimulationRepository simuladoRepository;

    @Autowired
    private EnemImportService enemImportService;
    @Override
    public ResultadoSimuladoResponse processarSimulado(EnviarSimuladoRequest dto) {
        StudentSimulation simulado = new StudentSimulation();
        simulado.setUserId(dto.usuarioId());
        simulado.setDataInicio(LocalDateTime.now());
        simulado.setStatus(StatusSimulado.FINALIZADO);

        int acertos = 0;
        double somaDificuldadeAcertos = 0.0;
        List<GabaritoItemResponse> detalhesGabarito = new ArrayList<>();

        for (RespostaItemRequest r : dto.respostas()) {
            Question questao = questaoRepository.findById(r.questaoId())
                    .orElseThrow(() -> new RuntimeException("Questão não encontrada: " + r.questaoId()));

            boolean acertou = questao.getRespostaCorreta() != null
                    && questao.getRespostaCorreta().equalsIgnoreCase(r.alternativaEscolhida());

            if (acertou) {
                acertos++;
                if (questao.getParametroB() != null) {
                    somaDificuldadeAcertos += questao.getParametroB();
                }
            }

            RespostaAluno respostaAluno = new RespostaAluno(questao, r.alternativaEscolhida(), acertou);
            respostaAluno.setSimulation(simulado);
            simulado.getRespostas().add(respostaAluno);

            detalhesGabarito.add(new GabaritoItemResponse(
                    questao.getId(),
                    questao.getRespostaCorreta(),
                    r.alternativaEscolhida(),
                    acertou,
                    questao.getDisciplina()
            ));
        }

        double notaTRI = calcularNotaAproximadaTRI(acertos, dto.respostas().size(), somaDificuldadeAcertos);

        simulado.setNotaCalculadaTRI(notaTRI);
        simulado.setDataFim(LocalDateTime.now());

        StudentSimulation salvo = simuladoRepository.save(simulado);

        return new ResultadoSimuladoResponse(
                salvo.getId(),
                notaTRI,
                acertos,
                dto.respostas().size(),
                detalhesGabarito
        );
    }

    @Override
    public List<Question> obterSimulados(Integer ano, DiaProva dia, String disciplina, String idioma) {
        if (dia != null && dia.equals(DiaProva.DIA_2)) {
            idioma = null;
        } else if (dia != null && dia.equals(DiaProva.DIA_1)) {
            if (idioma != null && !idioma.isBlank()) {
                idioma = idioma.trim().toLowerCase();
            } else {
                idioma = null;
            }
        }

        List<Question> questionList = questaoRepository.buscarSimulado(ano, dia, disciplina, idioma);

        if (questionList.isEmpty()) {
            enemImportService.importarProvas(ano);
            questionList = questaoRepository.buscarSimulado(ano, dia, disciplina, idioma);
        }

        return questionList;
    }

    @Override
    public SimuladoPendenteResponse pausarSimulado(PausarSimuladoRequest dto) {
        StudentSimulation simulado = simuladoRepository.findByUserIdAndStatus(dto.userId(), StatusSimulado.PAUSADO)
                .orElseGet(() -> {
                    StudentSimulation s = new StudentSimulation();
                    s.setUserId(dto.userId());
                    s.setDataInicio(LocalDateTime.now());
                    return s;
                });


        simulado.setStatus(StatusSimulado.FINALIZADO);
        simulado.setDataFim(LocalDateTime.now());

        int acertos = 0;
        double somaDificuldadeAcertos = 0.0;
        List<GabaritoItemResponse> detalhesGabarito = new ArrayList<>();

        simulado.getRespostas().clear();

        for (RespostaItemRequest r : dto.respostaAlunos()) {
            Question questao = questaoRepository.findById(r.questaoId())
                    .orElseThrow(() -> new RuntimeException("Questão não encontrada: " + r.questaoId()));

            RespostaAluno respostaAluno = new RespostaAluno();
            respostaAluno.setQuestao(questao.getId());
            respostaAluno.setAlternativaEscolhida(r.alternativaEscolhida());
            respostaAluno.setSimulation(simulado);

            simulado.getRespostas().add(respostaAluno);
        }

        simuladoRepository.save(simulado);

        return new SimuladoPendenteResponse(simulado);
    }

    @Override
    public SimuladoPendenteResponse buscarSimuladoPausado(Long userId) {

            return  simuladoRepository.findByUserIdAndStatus(userId,StatusSimulado.PAUSADO)
                    .map(SimuladoPendenteResponse::new).orElse(null);
    }

    @Override
    public List<ResultadoSimuladoResponse> historico(Long userId) {
        List<StudentSimulation> simulations = simuladoRepository.findAllByUserIdAndStatus(userId, StatusSimulado.FINALIZADO);
        List<ResultadoSimuladoResponse> historico = new ArrayList<>();
        for (StudentSimulation p : simulations) {
            List<RespostaAluno> respostas = p.getRespostas();
            List<GabaritoItemResponse> gabaritoDaProva = new ArrayList<>();
            int acertos = 0;
            for (RespostaAluno r : respostas) {
                if (r.getCorreta()) {
                    acertos++;
                }
                gabaritoDaProva.add(new GabaritoItemResponse(
                        r.getQuestao().getId(),
                        r.getQuestao().getEnunciado(),
                        r.getAlternativaEscolhida(),
                        r.getCorreta(),
                        r.getQuestao().getDisciplina()
                        ));

            }
            ResultadoSimuladoResponse resultado = new ResultadoSimuladoResponse(p.getId(), p.getNotaCalculadaTRI(), acertos, respostas.size(), gabaritoDaProva);
            historico.add(resultado);
        }

            return historico;

    }


    private double calcularNotaAproximadaTRI(int acertos, int total, double somaDificuldade) {

        if (total == 0 || acertos == 0) {
            return 300.0;
        }

        double proporcaoAcertos = (double) acertos / total;
        double notaBase = 300.0 + (proporcaoAcertos * 500.0);
        double bonusDificuldade = (somaDificuldade / acertos) * 50.0;

        double notaFinal = notaBase + bonusDificuldade;
        return Math.min(Math.max(notaFinal, 300.0), 1000.0);
    }
}