package com.estudamais.backend.controller;

import com.estudamais.backend.entity.User;
import com.estudamais.backend.request.GoalRequest;
import com.estudamais.backend.request.StudySessionRequest;
import com.estudamais.backend.response.GoalResponse;
import com.estudamais.backend.response.StudySessionResponse;
import com.estudamais.backend.service.StudyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/study")
public class StudyController {

    @Autowired
    private StudyService studyService;

    @PostMapping("/goal/save")
    public ResponseEntity<GoalResponse> saveOrUpdateGoal(@RequestBody @Valid GoalRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        GoalResponse response = studyService.saveOrUpdateGoal(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/goal")
    public ResponseEntity<GoalResponse> getGoal(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        GoalResponse response = studyService.getGoal(user.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/session/register")
    public ResponseEntity<StudySessionResponse> registerSession(@RequestBody @Valid StudySessionRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        StudySessionResponse response = studyService.registerSession(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/session/history")
    public ResponseEntity<List<StudySessionResponse>> getUserHistory(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<StudySessionResponse> response = studyService.getUserHistory(user.getId());
        return ResponseEntity.ok(response);
    }
}