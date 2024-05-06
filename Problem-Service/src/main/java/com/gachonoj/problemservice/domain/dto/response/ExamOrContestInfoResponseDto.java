package com.gachonoj.problemservice.domain.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamOrContestInfoResponseDto {
    private Long examId;
    private String examTitle;
    private String examContents;
    private LocalDateTime examStartDate;
    private LocalDateTime examEndDate;
    private String examType;
    private String examNotice;
}