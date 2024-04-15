package com.gachonoj.memberservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoResponseDto {
    private String memberEmail;
    private String memberName;
    private String memberNumber;
    private String memberIntroduce;
    private String memberNickname;
    private String memberImg;
    private Integer rating;
}
