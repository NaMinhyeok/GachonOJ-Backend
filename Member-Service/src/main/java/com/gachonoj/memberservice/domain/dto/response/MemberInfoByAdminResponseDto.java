package com.gachonoj.memberservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoByAdminResponseDto {
    private String memberEmail;
    private String memberName;
    private String memberNumber;
    private String memberNickname;
    private String memberRole;
}
