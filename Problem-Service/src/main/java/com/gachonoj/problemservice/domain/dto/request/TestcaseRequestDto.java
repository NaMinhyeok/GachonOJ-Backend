package com.gachonoj.problemservice.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestcaseRequestDto {
    private String testcaseInput;
    private String testcaseOutput;
    private String testcaseStatus; // Enum 이름을 String으로 받습니다.

}