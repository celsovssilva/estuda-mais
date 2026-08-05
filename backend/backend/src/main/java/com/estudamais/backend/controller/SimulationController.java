package com.estudamais.backend.controller;

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
    public ResponseEntity<List<Question>> obterQuestao(@RequestParam(required = false) String disciplina,@RequestParam(required = false) Integer ano){
        List<Question> questions = questionRepository.findByAno(ano);

        if(questions.isEmpty()){
            enemImportService.importarProvas(ano);
            questions = questionRepository.findByAno(ano);
        }

        if(disciplina != null && !disciplina.isBlank()){
            List<Question> filtradas = questions.stream().filter(q-> disciplina.equalsIgnoreCase(q.getDisciplina()))
                    .toList();
            return ResponseEntity.ok(filtradas);
        }
        return ResponseEntity.ok(questions);

    }

    @PostMapping("/finalizar")
    public ResponseEntity<ResultadoSimuladoResponse> resultado(@RequestBody EnviarSimuladoRequest request){
        ResultadoSimuladoResponse resultadoSimuladoResponse = simuladoService.processarSimulado(request);
        return  ResponseEntity.ok(resultadoSimuladoResponse);

    }
}
