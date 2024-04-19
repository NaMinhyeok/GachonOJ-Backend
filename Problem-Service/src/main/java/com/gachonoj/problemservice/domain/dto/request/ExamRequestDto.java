package com.gachonoj.problemservice.domain.dto.request;

import com.gachonoj.problemservice.domain.constant.ExamStatus;
import com.gachonoj.problemservice.domain.constant.ExamType;
import lombok.Data;

import java.util.List;
@Data
public class ExamRequestDto {
    private String examTitle;
    private String examMemo;
    private String examContents;
    private String examNotice;
    private String examStartDate;
    private String examEndDate;
    private Integer examDueTime;
    private List<Long> candidateList;
    private ExamStatus examStatus;
    private ExamType examType;
    private List<ProblemRequestDto> tests;
    public ExamStatus getExamStatus() {
        return examStatus != null ? examStatus : ExamStatus.WRITING; // 기본값으로 WRITING 설정
    }
    public ExamType getExamType() {
        return examType != null ? examType : ExamType.EXAM; // 기본값으로 WRITING 설정
    }

}
