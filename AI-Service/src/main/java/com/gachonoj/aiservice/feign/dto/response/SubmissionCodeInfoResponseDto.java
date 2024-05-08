package com.gachonoj.aiservice.feign.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SubmissionCodeInfoResponseDto {
    private Long problemId;
    private String code;
}
