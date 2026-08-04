package com.estudamais.backend.controller;

import com.estudamais.backend.entity.Question;
import com.estudamais.backend.repository.QuestionRepository;
import com.estudamais.backend.request.EnviarSimuladoRequest;
import com.estudamais.backend.response.ResultadoSimuladoResponse;
import com.estudamais.backend.service.SimuladoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/simulados")
public class SimulationController {
    @Autowired
    private SimuladoService simuladoService;
    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/questoes")
    public ResponseEntity<List<Question>> obterQuestao(@RequestParam(required = false) String disciplina){
        if(disciplina != null && !disciplina.isBlank()){
            return ResponseEntity.ok(questionRepository.findByDisciplina(disciplina));
        }
        return ResponseEntity.ok(questionRepository.findAll());

    }

    @PostMapping("/finalizar")
    public ResponseEntity<ResultadoSimuladoResponse> resultado(@RequestBody EnviarSimuladoRequest request){
        ResultadoSimuladoResponse resultadoSimuladoResponse = simuladoService.processarSimulado(request);
        return  ResponseEntity.ok(resultadoSimuladoResponse);

    }
}
