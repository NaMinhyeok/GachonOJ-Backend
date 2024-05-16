package com.gachonoj.problemservice.domain.dto.response;

import com.gachonoj.problemservice.domain.entity.Exam;
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
    private String memberNickname;
    private String examContents;
    private String examStartDate;
    private String examEndDate;
    private String examType;
    private String examNotice;
}