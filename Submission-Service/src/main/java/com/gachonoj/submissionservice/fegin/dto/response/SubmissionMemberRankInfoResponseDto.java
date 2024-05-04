package com.gachonoj.submissionservice.fegin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionMemberRankInfoResponseDto {
    private Integer memberRank;
    private Integer memberRating;
    private Integer needRank;
}
