package com.estudamais.backend.response;

public record RespostaItemResponse (

        com.estudamais.backend.entity.Question questaoId,
        String alternativaEscolhida
) {}

