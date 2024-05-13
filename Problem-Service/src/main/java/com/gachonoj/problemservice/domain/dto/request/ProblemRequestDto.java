package com.gachonoj.problemservice.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProblemRequestDto {
    private Long problemId;
    private String problemTitle;
    private String problemContents;
    private String problemInputContents;
    private String problemOutputContents;
    private Integer problemDiff;
    private String problemClass; // Enum 이름을 String으로 받습니다.
    private Integer problemTimeLimit;
    private Integer problemMemoryLimit;
    private String problemPrompt;
    private List<TestcaseRequestDto> testcases;
    private Integer questionScore;
    private Integer questionSequence; // 문제 순서

    // 필요한 경우, 추가적인 생성자, 메서드 또는 로직을 여기에 구현할 수 있습니다.
    // problemId 필드에 대한 getter 메서드

}