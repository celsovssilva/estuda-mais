package com.estudamais.backend.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GoalRequest {
    @NotBlank private String category;
    @NotNull @Min(1) private Integer targetMinutesPerDay;
}