package com.gachonoj.submissionservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TodaySubmissionCountResponseDto {
    private Integer totalSubmissionCount;
    private Integer correctSubmissionCount;
    private Integer incorrectSubmissionCount;
}
