package com.gachonoj.problemservice.domain.dto.response;

import com.gachonoj.problemservice.domain.entity.Problem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private List<String> testcaseInputs;
    private List<String> testcaseOutputs;

    public ProblemDetailAdminResponseDto(Problem problem, List<String> testcaseInputs, List<String> testcaseOutputs) {
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
        this.testcaseInputs = testcaseInputs;
        this.testcaseOutputs = testcaseOutputs;
    }
}