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
    public ResponseEntity<?> refresh(RefreshRequestDto refreshRequestDto){
        if(jwtUtil.isRefreshTokenExpired(refreshRequestDto.getRefreshToken())){
            String value = redisService.getData(jwtUtil.getMemberId(refreshRequestDto.getRefreshToken()).toString());
            if(value == null || !value.equals(refreshRequestDto.getRefreshToken())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is not valid");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is expired");
        }
        String role = jwtUtil.getRole(refreshRequestDto.getRefreshToken());
        Long memberId = jwtUtil.getMemberId(refreshRequestDto.getRefreshToken());
        String newAccessToken = jwtUtil.createAccessJwt(role, memberId);
        String newRefreshToken = jwtUtil.createRefreshJwt(role, memberId);

        redisService.deleteData(memberId.toString());
        redisService.setDataExpire(memberId.toString(), newRefreshToken, jwtUtil.getRefreshTokenExpireTime());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + newAccessToken);
        headers.add("Refresh-Token", "Bearer " + newRefreshToken);

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
