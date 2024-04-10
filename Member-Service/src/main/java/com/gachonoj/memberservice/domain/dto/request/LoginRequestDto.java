package com.gachonoj.memberservice.domain.dto.request;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String memberEmail;
    private String memberPassword;
}
