package com.gachonoj.submissionservice.feign.dto.response;

import com.gachonoj.submissionservice.domain.constant.Status;

public class SubmissionExamResultResponseDto {
    private Long memberId;
    private Long problemId;
    private String submissionCode;
    private Status submissionStatus;
}
