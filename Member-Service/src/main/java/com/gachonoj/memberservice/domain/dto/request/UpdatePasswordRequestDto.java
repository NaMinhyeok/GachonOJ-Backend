package com.gachonoj.memberservice.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdatePasswordRequestDto {
    @NotBlank
    private String memberPassword;
    @NotBlank
    private String memberNewPassword;
    @NotBlank
    private String memberNewPasswordConfirm;
}
