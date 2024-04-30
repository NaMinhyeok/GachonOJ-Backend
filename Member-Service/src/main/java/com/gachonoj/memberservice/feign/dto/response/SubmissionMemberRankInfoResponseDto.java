package com.gachonoj.memberservice.feign.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionMemberRankInfoResponseDto {
    private Integer memberRank;
    private Integer memberRating;
    private Integer needRank;
}
