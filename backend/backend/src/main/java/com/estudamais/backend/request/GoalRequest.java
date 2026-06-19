package com.estudamais.backend.request;

import jakarta.validation.constraints.*;
import lombok.Data;

public record GoalRequest(
        @NotBlank String category,
        @NotNull @Min(1) Integer targetMinutesPerDay
) {}