package com.gachonoj.apigateway.auth;

import lombok.Getter;

@Getter
public class RefreshRequestDto {
    private String refreshToken;
}
