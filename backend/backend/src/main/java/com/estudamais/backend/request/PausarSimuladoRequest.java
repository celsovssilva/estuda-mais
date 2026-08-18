package com.estudamais.backend.request;

import com.estudamais.backend.entity.RespostaAluno;
import com.estudamais.backend.entity.StatusSimulado;

import java.util.List;

public record PausarSimuladoRequest(
        Long id,
        Long userId,
        StatusSimulado status,
        Integer indiceAtual,
        Integer tempoDecorrido,
        List<RespostaItemRequest> respostaAlunos
) {
}
