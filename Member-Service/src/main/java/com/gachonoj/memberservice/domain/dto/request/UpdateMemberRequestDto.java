package com.gachonoj.memberservice.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateMemberRequestDto {
    @NotBlank
    private String memberNickname;
    @NotBlank
    private String memberName;
    private String memberNumber;
    @NotBlank
    private String memberRole;
}
