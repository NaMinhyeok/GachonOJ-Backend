package com.gachonoj.submissionservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MySubmissionResultResponseDto {
    private String memberNickname;
    private String problemTitle;
    private String submissionCode;
}
