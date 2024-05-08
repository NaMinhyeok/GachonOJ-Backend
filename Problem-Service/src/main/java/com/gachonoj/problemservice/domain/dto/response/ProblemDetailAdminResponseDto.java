package com.gachonoj.problemservice.domain.dto.response;

import com.gachonoj.problemservice.domain.entity.Problem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ProblemDetailAdminResponseDto {
    private Long problemId;
    private String problemTitle;
    private String problemContents;
    private String problemInputContents;
    private String problemOutputContents;
    private Integer problemDiff;
    private String problemClass; // Enum 이름을 String으로 받습니다.
    private Integer problemTimeLimit;
    private Integer problemMemoryLimit;
    private String problemStatus; // Enum 이름을 String으로 받습니다.
    private String problemPrompt;
    private List<TestcaseResponseDto> testcases;

    public ProblemDetailAdminResponseDto(Problem problem) {
        this.problemId = problem.getProblemId();
        this.problemTitle = problem.getProblemTitle();
        this.problemContents = problem.getProblemContents();
        this.problemInputContents = problem.getProblemInputContents();
        this.problemOutputContents = problem.getProblemOutputContents();
        this.problemDiff = problem.getProblemDiff();
        this.problemClass = problem.getProblemClass().name();
        this.problemTimeLimit = problem.getProblemTimeLimit();
        this.problemMemoryLimit = problem.getProblemMemoryLimit();
        this.problemStatus = problem.getProblemStatus().name();
        this.problemPrompt = problem.getProblemPrompt();
        this.testcases = problem.getTestcases().stream()
                .map(tc -> new TestcaseResponseDto(
                        tc.getTestcaseInput(),
                        tc.getTestcaseOutput(),
                        tc.getTestcaseStatus().name()))
                .collect(Collectors.toList());
    }
}