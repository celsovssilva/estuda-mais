package com.estudamais.backend.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewImportRequest {
    @NotBlank private String title;
    @NotBlank private String rawContent;
}
