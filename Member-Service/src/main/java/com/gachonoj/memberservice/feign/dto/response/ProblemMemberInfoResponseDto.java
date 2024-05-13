package com.gachonoj.memberservice.feign.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemMemberInfoResponseDto {
    private String memberNumber;
    private String memberName;
    private String memberEmail;
}
