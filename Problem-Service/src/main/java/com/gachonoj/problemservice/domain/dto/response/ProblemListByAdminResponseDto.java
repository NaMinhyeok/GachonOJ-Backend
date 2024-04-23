package com.gachonoj.problemservice.domain.dto.response;

import com.gachonoj.problemservice.domain.entity.Problem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemListByAdminResponseDto {
    private Long problemId;
    private String problemTitle;
    private Integer problemDiff;
    private String problemStatus;
    private Integer correctPeople;
    private Integer correctSubmit;
    private Integer submitCount;
    private String problemCreatedDate;

    public ProblemListByAdminResponseDto(Problem problem, Integer correctPeople, Integer correctSubmit, Integer submitCount, String problemCreatedDate, String problemStatus) {
        this.problemId = problem.getProblemId();
        this.problemTitle = problem.getProblemTitle();
        this.problemDiff = problem.getProblemDiff();
        this.problemStatus = problemStatus;
        this.correctPeople = correctPeople;
        this.correctSubmit = correctSubmit;
        this.submitCount = submitCount;
        this.problemCreatedDate = problemCreatedDate;
    }
}
