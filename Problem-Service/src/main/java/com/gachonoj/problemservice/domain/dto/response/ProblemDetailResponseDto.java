package com.gachonoj.problemservice.domain.dto.response;

import com.gachonoj.problemservice.domain.entity.Problem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProblemDetailResponseDto {
    private String problemTitle;
    private String problemContents;
    private String problemDiff;
    private String problemInputContents;
    private String problemOutputContents;
    private List<String> testcaseInputs;
    private List<String> testcaseOutputs;

    public ProblemDetailResponseDto(Problem problem, List<String> testcaseInputs, List<String> testcaseOutputs) {
        this.problemTitle = problem.getProblemTitle();
        this.problemContents = problem.getProblemContents();
        this.problemDiff = problem.getProblemDiff() + "단계";
        this.problemInputContents = problem.getProblemInputContents();
        this.problemOutputContents = problem.getProblemOutputContents();
        this.testcaseInputs = testcaseInputs;
        this.testcaseOutputs = testcaseOutputs;
    }
}
