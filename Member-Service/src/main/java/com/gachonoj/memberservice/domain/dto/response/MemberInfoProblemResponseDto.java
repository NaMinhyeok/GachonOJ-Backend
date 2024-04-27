package com.gachonoj.memberservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoProblemResponseDto {
    private String memberNickname;
    private String memberIntroduce;
    private String memberImg;
    private Integer rating;
    private Integer memberRank;
    private Integer needRank;
}
