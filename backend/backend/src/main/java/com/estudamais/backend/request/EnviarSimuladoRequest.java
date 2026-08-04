package com.estudamais.backend.request;

import java.util.List;

public record EnviarSimuladoRequest(
        Long usuarioId,
        List<RespostaItemRequest> respostas
) {
}
