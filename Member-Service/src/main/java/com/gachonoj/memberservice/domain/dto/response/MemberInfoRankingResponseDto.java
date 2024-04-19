package com.gachonoj.memberservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoRankingResponseDto {
    private String memberNickname;
    private Integer rating;
    private Integer solvedProblemCount;
    private Integer tryProblemCount;
    private Integer bookmarkProblemCount;
}
