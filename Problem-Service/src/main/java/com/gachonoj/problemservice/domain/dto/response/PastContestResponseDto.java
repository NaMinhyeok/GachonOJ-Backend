package com.gachonoj.problemservice.domain.dto.response;

import com.gachonoj.problemservice.domain.constant.ExamStatus;
import com.gachonoj.problemservice.domain.entity.Exam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PastContestResponseDto {
    private Long examId;
    private String examTitle;
    private String memberNickname;
    private String examStartDate;
    private String examEndDate;
    private String examStatus;

    public PastContestResponseDto(Exam exam, String memberNickname) {
        this.examId = exam.getExamId();
        this.examTitle = exam.getExamTitle();
        this.memberNickname = memberNickname;
        this.examStartDate = DateFormat(exam.getExamStartDate());
        this.examEndDate = DateFormat(exam.getExamEndDate());
        this.examStatus = translateExamStatus(exam.getExamStatus());
    }

    public String translateExamStatus(ExamStatus examStatus) {
        return switch (examStatus) {
            case WRITING -> "작성 중";
            case RESERVATION -> "예약";
            case ONGOING -> "진행 중";
            case TERMINATED -> "종료됨";
        };
    }

    public String DateFormat(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH시");
        return date.format(formatter);
    }
}
