package com.gachonoj.submissionservice.feign.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionProblemTestCaseResponseDto {
    private String input;
    private String output;
}