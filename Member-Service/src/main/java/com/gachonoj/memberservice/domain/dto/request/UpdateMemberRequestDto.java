package com.gachonoj.memberservice.domain.dto.request;

import lombok.Getter;

@Getter
public class UpdateMemberRequestDto {
    private String memberNickname;
    private String memberName;
    private String memberNumber;
}
