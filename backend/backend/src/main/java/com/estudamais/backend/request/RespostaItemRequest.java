package com.estudamais.backend.request;

public record RespostaItemRequest(
        String questaoId,
        String alternativaEscolhida
) {
}
