package com.estudamais.backend.response;

import java.util.List;

public record ResultadoSimuladoResponse(
        Long simuladoId,
        Double notaTRI,
        Integer totalAcertos,
        Integer totalQuestoes,
        List<GabaritoItemResponse> gabarito
) {
}
