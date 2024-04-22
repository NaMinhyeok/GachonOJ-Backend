package com.gachonoj.memberservice.domain.dto.response;

import com.gachonoj.memberservice.domain.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String memberImg;
    private Role memberRole;

    public LoginResponseDto(String memberImg, String memberRole) {
        this.memberImg = memberImg;
        this.memberRole = Role.fromLabel(memberRole);
    }
}
