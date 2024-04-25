package com.gachonoj.problemservice.feign.dto.response;

import com.gachonoj.problemservice.domain.entity.Testcase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionProblemTestCaseResponseDto {
    private String input;
    private String output;
}
