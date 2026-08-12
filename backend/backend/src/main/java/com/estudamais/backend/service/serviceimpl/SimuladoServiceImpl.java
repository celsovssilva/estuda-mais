package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.DiaProva;
import com.estudamais.backend.entity.Question;
import com.estudamais.backend.entity.RespostaAluno;
import com.estudamais.backend.entity.StudentSimulation;
import com.estudamais.backend.repository.QuestionRepository;
import com.estudamais.backend.repository.SimulationRepository;
import com.estudamais.backend.request.EnviarSimuladoRequest;
import com.estudamais.backend.request.RespostaItemRequest;
import com.estudamais.backend.response.GabaritoItemResponse;
import com.estudamais.backend.response.ResultadoSimuladoResponse;
import com.estudamais.backend.service.EnemImportService;
import com.estudamais.backend.service.SimuladoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SimuladoServiceImpl  implements SimuladoService {
    @Autowired
    private QuestionRepository questaoRepository;

    @Autowired
    private SimulationRepository simuladoRepository;

    @Autowired
    private EnemImportService enemImportService;

    @Override
    public ResultadoSimuladoResponse processarSimulado(EnviarSimuladoRequest dto) {
        StudentSimulation simulado = new StudentSimulation();
        simulado.setUsuarioId(dto.usuarioId());
        simulado.setDataInicio(LocalDateTime.now());


        int acertos = 0;
        double somaDificuldadeAcertos = 0.0;
        List<GabaritoItemResponse> detalhesGabarito = new ArrayList<>();

        for (RespostaItemRequest r : dto.respostas()) {
            Question questao = questaoRepository.findById((r.questaoId()))
                    .orElseThrow(() -> new RuntimeException("Questão não encontrada: " + r.questaoId()));

            boolean acertou = questao.getRespostaCorreta().equalsIgnoreCase(r.alternativaEscolhida());

            if (acertou) {
                acertos++;
                somaDificuldadeAcertos += questao.getParametroB();
            }


            RespostaAluno respostaAluno = new RespostaAluno(questao, r.alternativaEscolhida(), acertou);
            simulado.getRespostas().add(respostaAluno);

            detalhesGabarito.add(new GabaritoItemResponse(
                    questao.getId(),
                    questao.getRespostaCorreta(),
                    r.alternativaEscolhida(),
                    acertou
            ));
        }


        // Cálculo Simplificado da Escala ENEM (Aproximação de TRI para escala 300 - 1000)
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
    public List<Question> obterSimulados(Integer ano, DiaProva dia,String disciplina ,String idioma) {
        if (dia != null && dia.equals(DiaProva.DIA_2)) {
            idioma = null;
        } else if (dia != null && dia.equals(DiaProva.DIA_1)) {
                idioma = idioma.trim().toLowerCase();
            }

                List<Question> questionList = questaoRepository.buscarSimulado(ano, dia, disciplina, idioma);

        if(questionList.isEmpty()){
                enemImportService.importarProvas(ano);
                questionList = questaoRepository.buscarSimulado(ano, dia, disciplina, idioma);
        }
     return questionList;
    }

    private double calcularNotaAproximadaTRI(int acertos, int total, double somaDificuldade) {
        if (acertos == 0) return 300.0; // Nota base mínima aproximada do ENEM

        double proporcaoAcertos = (double) acertos / total;
        // Ajusta a nota de acordo com a taxa de acerto e a dificuldade média das que acertou
        double notaBase = 300.0 + (proporcaoAcertos * 500.0);
        double bonusDificuldade = (somaDificuldade / acertos) * 50.0;

        double notaFinal = notaBase + bonusDificuldade;
        return Math.min(Math.max(notaFinal, 300.0), 1000.0); // Limita entre 300 e 1000
    }
    }

