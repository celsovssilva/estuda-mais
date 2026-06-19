package com.estudamais.backend.request;

import jakarta.validation.constraints.*;
import lombok.Data;

public record StudySessionRequest(
        @NotBlank String subject,
        @NotNull @Min(1) Integer durationMinutes
) {}