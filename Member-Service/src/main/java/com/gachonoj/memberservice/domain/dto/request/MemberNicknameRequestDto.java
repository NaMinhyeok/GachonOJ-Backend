package com.gachonoj.memberservice.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberNicknameRequestDto {
    @NotBlank
    private String memberNickname;
}
