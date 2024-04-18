package com.gachonoj.memberservice.domain.dto.request;

import lombok.Getter;

@Getter
public class UpdatePasswordRequestDto {
    private String memberPassword;
    private String memberNewPassword;
    private String memberNewPasswordConfirm;
}
