package com.gachonoj.submissionservice.feign.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionExamResultInfoResponseDto {
    private List<SubmissionDetailDto> submissions;
}
