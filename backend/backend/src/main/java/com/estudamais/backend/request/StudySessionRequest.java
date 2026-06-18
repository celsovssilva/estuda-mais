package com.estudamais.backend.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class StudySessionRequest {
    @NotBlank private String subject;
    @NotNull @Min(1) private Integer durationMinutes;
}
