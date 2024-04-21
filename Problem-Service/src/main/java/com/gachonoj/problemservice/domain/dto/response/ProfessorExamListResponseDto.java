package com.gachonoj.problemservice.domain.dto.response;

import com.gachonoj.problemservice.domain.entity.Exam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfessorExamListResponseDto {
    private Long examId;
    private String examTitle;
    private String examMemo;
    private String examUpdateDate;
    private String examCreatedDate;
    private String examStatus;

    public ProfessorExamListResponseDto(Exam exam,String examUpdateDate,String examCreatedDate,String examStartDate) {
        this.examId = exam.getExamId();
        this.examTitle = exam.getExamTitle();
        this.examMemo = exam.getExamMemo();
        this.examUpdateDate = examUpdateDate;
        this.examCreatedDate = examCreatedDate;
        this.examStatus = examStartDate + exam.getExamStatus().getLabel();
    }
}
