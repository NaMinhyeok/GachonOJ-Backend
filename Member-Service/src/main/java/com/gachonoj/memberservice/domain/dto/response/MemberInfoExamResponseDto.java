package com.gachonoj.memberservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoExamResponseDto {
    private String memberNickname;
    private Integer rating;
    private String memberName;
    private String memberNumber;
}
