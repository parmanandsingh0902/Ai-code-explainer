package com.aicodeexplainer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CodeAnalysisRequest {

    @NotBlank(message = "Source code is required")
    @Size(max = 50000, message = "Source code must not exceed 50000 characters")
    private String sourceCode;

    @NotBlank(message = "Language is required")
    @Pattern(regexp = "^(Java|Python|JavaScript|C\\+\\+|C)$", message = "Language must be Java, Python, JavaScript, C++, or C")
    private String language;
}
