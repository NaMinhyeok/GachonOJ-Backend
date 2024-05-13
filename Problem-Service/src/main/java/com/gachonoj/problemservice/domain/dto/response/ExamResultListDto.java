package com.gachonoj.problemservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultListDto {
    private String examTitle;
    private String examMemo;
    private Integer submissionTotal;
    private Long memberId;
    private String memberName;
    private String memberNumber;
    private String memberEmail;
    private Integer totalScore;
    private String examDueTime;
    private String submissionDate;
}
