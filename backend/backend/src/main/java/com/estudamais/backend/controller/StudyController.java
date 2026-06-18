package com.estudamais.backend.controller;

import com.estudamais.backend.entity.User;
import com.estudamais.backend.request.GoalRequest;
import com.estudamais.backend.request.StudySessionRequest;
import com.estudamais.backend.response.GoalResponse;
import com.estudamais.backend.response.StudySessionResponse;
import com.estudamais.backend.service.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudyController {

    private final StudyService studyService;

    @PostMapping("/session")
    public ResponseEntity<StudySessionResponse> createSession(
            @Valid @RequestBody StudySessionRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(studyService.registerSession(user.getId(), request));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<StudySessionResponse>> getSessions(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(studyService.getUserHistory(user.getId()));
    }

    @PostMapping("/goal")
    public ResponseEntity<GoalResponse> defineGoal(
            @Valid @RequestBody GoalRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(studyService.saveOrUpdateGoal(user.getId(), request));
    }

    @GetMapping("/goal")
    public ResponseEntity<GoalResponse> getGoal(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(studyService.getGoal(user.getId()));
    }
}