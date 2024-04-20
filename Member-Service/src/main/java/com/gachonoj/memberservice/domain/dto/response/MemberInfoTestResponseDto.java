package com.gachonoj.memberservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoTestResponseDto {
    private Long memberId;
    private String memberImg;
    private String memberName;
    private String memberNumber;
    private String memberEmail;
}
