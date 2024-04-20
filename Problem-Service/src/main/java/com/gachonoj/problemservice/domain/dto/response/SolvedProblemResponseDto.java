package com.gachonoj.problemservice.domain.dto.response;

import com.gachonoj.problemservice.domain.constant.ProblemClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SolvedProblemResponseDto {
    private Long problemId;
    private String problemTitle;
    private Integer problemDiff;
    private ProblemClass problemClass;
    private Integer correctPeople;
    private Double correctRate;
    private Boolean isBookmarked;
}
