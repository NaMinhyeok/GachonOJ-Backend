package com.gachonoj.problemservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultDetailsResponseDto {
    private String examTitle;
    private String examMemo;
    private int submissionTotal;
    private String memberName;
    private String memberNumber;
    private String memberEmail;
    private int testTotalScore;
    private String examDueTime;
    private String submissionDate;
    private List<QuestionResultDetailsResponseDto> examQuestions;
}

