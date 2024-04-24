package com.gachonoj.submissionservice.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteTestRequestDto {
    @NotBlank
    private String code;
    @NotBlank
    private String language;
}
