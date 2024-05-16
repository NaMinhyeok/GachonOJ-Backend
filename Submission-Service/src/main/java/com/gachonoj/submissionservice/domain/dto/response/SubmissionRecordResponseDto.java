package com.gachonoj.submissionservice.domain.dto.response;

import com.gachonoj.submissionservice.domain.constant.Language;
import com.gachonoj.submissionservice.domain.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRecordResponseDto {
    // 제출 번호
    // 제출 일시 (YYYY-MM-MM HH:MM:SS)
    // 정답 여부
    // 언어
    private Long submissionId;
    private String submissionStatus;
    private Language submissionLang;
    private String submissionDate;
}
