package com.estudamais.backend.response;

import com.estudamais.backend.entity.RespostaAluno;
import com.estudamais.backend.entity.StudentSimulation;
import com.estudamais.backend.service.ScheduleService;

import java.util.List;

public record SimuladoPendenteResponse(
        Long id,
        Integer indiceAtual,
        Integer tempoDecorrido,
        List<RespostaItemResponse> respostas
) {
    public SimuladoPendenteResponse(StudentSimulation studentSimulation) {
        this(
                studentSimulation.getId(),
                studentSimulation.getIndiceAtual(),
                studentSimulation.getTempoDecorrido(),
                studentSimulation.getRespostas().stream()
                        .map(r -> new RespostaItemResponse(r.getQuestao(), r.getAlternativaEscolhida()))
                        .toList()
        );

    }


}
