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
                    acertou
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
        StudentSimulation s = simuladoRepository.findById(dto.id())
                .orElseThrow(() -> new RuntimeException("Simulado não encontrado"));

        s.setStatus(StatusSimulado.PAUSADO);
        s.setIndiceAtual(dto.indiceAtual());
        s.setTempoDecorrido(dto.tempoDecorrido());
        s.getRespostas().clear();

        for (RespostaItemRequest item : dto.respostaAlunos()) {
            Question questao = questaoRepository.findById(item.questaoId())
                    .orElseThrow(() -> new RuntimeException("Questão não encontrada: " + item.questaoId()));

            RespostaAluno respostaAluno = new RespostaAluno();
            respostaAluno.setQuestao(questao.getId());
            respostaAluno.setAlternativaEscolhida(item.alternativaEscolhida());
            respostaAluno.setSimulation(s);

            s.getRespostas().add(respostaAluno);
        }

        simuladoRepository.save(s);

        return new SimuladoPendenteResponse(s);
    }

    @Override
    public SimuladoPendenteResponse buscarSimuladoPausado(Long userId) {

            return  simuladoRepository.findByUserIdAndStatus(userId,StatusSimulado.PAUSADO)
                    .map(SimuladoPendenteResponse::new).orElse(null);
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