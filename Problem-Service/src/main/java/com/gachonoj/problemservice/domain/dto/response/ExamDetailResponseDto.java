package com.gachonoj.problemservice.domain.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamDetailResponseDto {
    private Long examId;
    private String examTitle;
    private String examContents;
    private String examStartDate;
    private String examEndDate;
    private String examStatus;
    private String examType;
    private String examMemo;
    private String examNotice;
    private List<Long> candidateList;
    private List<ProblemDetailAdminResponseDto> problems;
}