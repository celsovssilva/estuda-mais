package com.estudamais.backend.controller;

import com.estudamais.backend.entity.DiaProva;
import com.estudamais.backend.entity.Question;
import com.estudamais.backend.repository.QuestionRepository;
import com.estudamais.backend.request.EnviarSimuladoRequest;
import com.estudamais.backend.response.ResultadoSimuladoResponse;
import com.estudamais.backend.service.EnemImportService;
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
    @Autowired
    private EnemImportService enemImportService;

    @GetMapping("/questoes")
    public ResponseEntity<List<Question>> obterQuestao(@RequestParam(required = false) String disciplina, @RequestParam(required = false) Integer ano,@RequestParam(required = false) DiaProva dia,@RequestParam(required = false) String idioma){

      List<Question> questionList =  simuladoService.obterSimulados(ano, dia,disciplina,idioma);

        return ResponseEntity.ok(questionList);

    }

    @PostMapping("/finalizar")
    public ResponseEntity<ResultadoSimuladoResponse> resultado(@RequestBody EnviarSimuladoRequest request){
        ResultadoSimuladoResponse resultadoSimuladoResponse = simuladoService.processarSimulado(request);
        return  ResponseEntity.ok(resultadoSimuladoResponse);

    }
    private String normalizarTexto(String texto) {
        return texto.toLowerCase()
                .replace("-", " ")
                .replace("ç", "c")
                .replace("ã", "a")
                .replace("á", "a")
                .replace("é", "e")
                .replace("ê", "e")
                .trim();
    }
}
