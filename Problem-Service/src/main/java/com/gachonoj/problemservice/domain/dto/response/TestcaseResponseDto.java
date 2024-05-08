package com.gachonoj.problemservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestcaseResponseDto {
    private String testcaseInput;
    private String testcaseOutput;
    private String testcaseStatus; // Enum 이름을 String으로 받습니다.

}