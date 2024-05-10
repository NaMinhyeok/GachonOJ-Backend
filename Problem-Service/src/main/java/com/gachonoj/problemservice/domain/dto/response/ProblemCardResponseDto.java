package com.gachonoj.problemservice.domain.dto.response;

import com.gachonoj.problemservice.domain.entity.Problem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemCardResponseDto {
    private Long problemId;
    private String problemTitle;
    private String problemDiff;
    private String problemClass;
    private String correctRate;

    public ProblemCardResponseDto(Problem problem,String problemDiff, String problemClass, String correctRate) {
        this.problemId = problem.getProblemId();
        this.problemTitle = problem.getProblemTitle();
        this.problemDiff = problemDiff;
        this.problemClass = problemClass;
        this.correctRate = correctRate;
    }
}
