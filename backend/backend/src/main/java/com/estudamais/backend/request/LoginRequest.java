package com.estudamais.backend.request;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {}