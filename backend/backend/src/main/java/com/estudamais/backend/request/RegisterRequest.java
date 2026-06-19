package com.estudamais.backend.request;

import jakarta.validation.constraints.*;
import lombok.Data;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String password
) {}