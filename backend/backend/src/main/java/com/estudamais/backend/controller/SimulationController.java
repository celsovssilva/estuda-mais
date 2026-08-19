package com.estudamais.backend.controller;

import com.estudamais.backend.entity.DiaProva;
import com.estudamais.backend.entity.Question;
import com.estudamais.backend.entity.User;
import com.estudamais.backend.repository.QuestionRepository;
import com.estudamais.backend.request.EnviarSimuladoRequest;
import com.estudamais.backend.request.PausarSimuladoRequest;
import com.estudamais.backend.response.ResultadoSimuladoResponse;
import com.estudamais.backend.response.SimuladoPendenteResponse;
import com.estudamais.backend.service.EnemImportService;
import com.estudamais.backend.service.SimuladoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    @PostMapping("/pausar")
    public ResponseEntity<SimuladoPendenteResponse> pausar(@RequestBody PausarSimuladoRequest request){
        SimuladoPendenteResponse response = simuladoService.pausarSimulado(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pendentes")
    public ResponseEntity<SimuladoPendenteResponse> buscarPendentes(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        SimuladoPendenteResponse response = simuladoService.buscarSimuladoPausado(user.getId());
        return ResponseEntity.ok(response);

    }

    @GetMapping("/historico")
    public ResponseEntity<List<ResultadoSimuladoResponse>> simuladoHistorico(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        List<ResultadoSimuladoResponse> historico = simuladoService.historico(user.getId());
        return ResponseEntity.ok(historico);

    }


}
