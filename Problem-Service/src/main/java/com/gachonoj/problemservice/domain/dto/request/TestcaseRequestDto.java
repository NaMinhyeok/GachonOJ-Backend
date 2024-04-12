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

    // Lombok 어노테이션으로 인해 모든 getter, setter, 기본 생성자 및 모든 인자를 받는 생성자가 자동으로 생성됩니다.
}