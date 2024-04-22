package com.gachonoj.memberservice.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDto {
    @NotBlank
    private String memberEmail;
    @NotBlank
    private String memberPassword;
}
