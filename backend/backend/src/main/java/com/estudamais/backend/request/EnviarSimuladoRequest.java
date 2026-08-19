package com.estudamais.backend.request;

import java.util.List;

public record EnviarSimuladoRequest(
        Long simuladoId,
        Long usuarioId,
        List<RespostaItemRequest> respostas
) {
}
