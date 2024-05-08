package com.gachonoj.problemservice.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ExamCardInfoResponseDto {
    private Long examId;
    private String examTitle;
    private String memberNickname;
    private String examStartDate;
    private String examEndDate;
    private String examStatus;


    public ExamCardInfoResponseDto(Long examId, String examTitle, String examStartDate, String examEndDate, String examStatus) {
        this.examId = examId;
        this.examTitle = examTitle;
        this.examStartDate = examStartDate;
        this.examEndDate = examEndDate;
        this.examStatus = examStatus;
    }
}
