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
    private Status submissionStatus;
    private Language submissionLang;
    private LocalDateTime submissionDate;
}
