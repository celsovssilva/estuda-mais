package com.estudamais.backend.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private String title;
    private String aiSummary;
    private String aiGeneratedQuiz;
    private LocalDateTime createdAt;
}
