package com.gachonoj.problemservice.domain.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ExamResultListDto {
    private Long memberId;
    private String memberName;
    private String memberNumber;
    private String memberEmail;
    private int totalScore;
    private String examDueTime; // "HH:mm:ss" 포맷
    private String submissionDate;
}