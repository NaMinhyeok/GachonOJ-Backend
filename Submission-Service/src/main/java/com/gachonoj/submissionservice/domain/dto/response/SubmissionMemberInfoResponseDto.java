package com.gachonoj.submissionservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionMemberInfoResponseDto {
    private Integer solvedProblemCount;
    private Integer tryProblemCount;
}
