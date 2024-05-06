package com.gachonoj.problemservice.domain.dto.response;

import java.util.List;

public class ExamResultResponseDto {
    private String examTitle;
    private String examMemo;
    private Long submissionTotal;
    private String memberName;
    private Long memberNumber;
    private String memberEmail;
    private Long testTotalScore;
    private String examDueTime;
    private String submissionDate;
    private List<QuestionResultDto> examQuestions;
}
