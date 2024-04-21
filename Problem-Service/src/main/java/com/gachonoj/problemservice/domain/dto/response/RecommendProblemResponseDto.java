package com.gachonoj.problemservice.domain.dto.response;

import com.gachonoj.problemservice.domain.entity.Problem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendProblemResponseDto {
    private Long problemId;
    private String problemTitle;
    private String problemDiff;
    private String problemClass;

    public RecommendProblemResponseDto(Problem problem) {
        this.problemId = problem.getProblemId();
        this.problemTitle = problem.getProblemTitle();
        this.problemDiff = problem.getProblemDiff().toString() + "단계";
        this.problemClass = problem.getProblemClass().getLabel();
    }
}
