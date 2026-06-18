package com.estudamais.backend.response;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudySessionResponse {
    private Long id;
    private String subject;
    private Integer durationMinutes;
    private LocalDate studyDate;
    private String feedbackMessage; // Alerta de descanso ou validação de progresso
}