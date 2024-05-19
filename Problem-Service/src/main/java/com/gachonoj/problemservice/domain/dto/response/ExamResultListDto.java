package com.gachonoj.problemservice.domain.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ExamResultListDto {
    private Long testId;
    private Long memberId;
    private String memberName;
    private String memberNumber;
    private String memberEmail;
    private Integer totalScore;
    private String examDueTime;
    private String submissionDate;
}