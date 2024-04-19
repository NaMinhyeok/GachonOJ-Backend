package com.gachonoj.problemservice.domain.dto.response;

import com.gachonoj.problemservice.domain.constant.ProblemClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkProblemResponseDto {
    private String problemTitle;
    private Integer problemDiff;
    private ProblemClass problemClass;
    private Integer correctPeople;
    private Integer correctRate;
    private Boolean isBookmarked;
}
