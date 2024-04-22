package com.gachonoj.apigateway.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    public AuthService(JwtUtil jwtUtil, RedisService redisService) {
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
    }

    // 리프레시 토큰을 통해 액세스 토큰 발급 메소드
    public ResponseEntity<?> refresh(String refreshToken){
        if(jwtUtil.isRefreshTokenExpired(refreshToken)){
            String value = redisService.getData(jwtUtil.getMemberId(refreshToken).toString());
            if(value == null || !value.equals(refreshToken)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is not valid");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is expired");
        }
        String role = jwtUtil.getRole(refreshToken);
        Long memberId = jwtUtil.getMemberId(refreshToken);
        String newAccessToken = jwtUtil.createAccessJwt(role, memberId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + newAccessToken);

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
