package com.estudamais.backend.service;

import com.estudamais.backend.request.EnviarSimuladoRequest;
import com.estudamais.backend.response.ResultadoSimuladoResponse;

public interface SimuladoService {
    ResultadoSimuladoResponse processarSimulado(EnviarSimuladoRequest dto);
}
