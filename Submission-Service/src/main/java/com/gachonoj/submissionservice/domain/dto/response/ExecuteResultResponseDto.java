package com.gachonoj.submissionservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteResultResponseDto {
    private String output;
    private String result;
}
