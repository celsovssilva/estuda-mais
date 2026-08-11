package com.estudamais.backend.service;

import com.estudamais.backend.entity.DiaProva;
import com.estudamais.backend.entity.Question;
import com.estudamais.backend.request.EnviarSimuladoRequest;
import com.estudamais.backend.response.ResultadoSimuladoResponse;

import java.util.List;

public interface SimuladoService {
    ResultadoSimuladoResponse processarSimulado(EnviarSimuladoRequest dto);
    List<Question> obterSimulados(Integer ano, DiaProva dia,String disciplina,String idioma);
}
