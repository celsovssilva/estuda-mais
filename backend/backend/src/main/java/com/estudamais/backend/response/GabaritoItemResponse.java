package com.estudamais.backend.response;

public record GabaritoItemResponse(
        String questaoId,
        String enunciado,
        String alternativaEscolhida,
        Boolean acertou
) {
}
