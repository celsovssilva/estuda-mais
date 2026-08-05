package com.estudamais.backend.response;

public record GabaritoItemResponse(
        Long questaoId,
        String enunciado,
        String alternativaEscolhida,
        Boolean acertou
) {
}
