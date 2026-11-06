package com.estudamais.backend.controller;

import com.estudamais.backend.request.VideoConsumoRequest;
import com.estudamais.backend.response.VideoConsumoRespose;
import com.estudamais.backend.service.VideoConsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/resumo")
public class VideoConsumoController {
    @Autowired
    private VideoConsumoService videoConsumoService;

    @PostMapping("/resumir")
    public ResponseEntity<VideoConsumoRespose> resumir(@RequestBody VideoConsumoRequest request){
        String resumoGerado = videoConsumoService.gerarResumo(request.url());
        return ResponseEntity.ok(new VideoConsumoRespose(resumoGerado));
    }
}
