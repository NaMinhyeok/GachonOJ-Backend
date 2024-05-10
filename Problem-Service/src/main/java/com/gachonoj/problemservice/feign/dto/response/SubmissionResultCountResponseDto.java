package com.gachonoj.problemservice.feign.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResultCountResponseDto {
    private Long problemId;
    private Integer submitCount;
    private Integer incorrectCount;

}
