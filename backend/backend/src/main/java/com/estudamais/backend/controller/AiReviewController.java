package com.estudamais.backend.controller;

import com.estudamais.backend.entity.User;
import com.estudamais.backend.request.ReviewImportRequest;
import com.estudamais.backend.response.ReviewResponse;
import com.estudamais.backend.service.AiIntegrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiReviewController {

    private final AiIntegrationService aiService;

    @PostMapping("/import")
    public ResponseEntity<ReviewResponse> importMaterial(
            @Valid @RequestBody ReviewImportRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(aiService.importAndProcessMaterial(user.getId(), request));
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(aiService.getUserReviews(user.getId()));
    }

    @GetMapping("/export/{id}")
    public ResponseEntity<String> exportReview(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(aiService.exportReviewText(user.getId(), id));
    }
}