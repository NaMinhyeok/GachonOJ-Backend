package com.gachonoj.memberservice.jwt;

import com.gachonoj.memberservice.domain.constant.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessTokenExpireTime;
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") long accessTokenExpireTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpireTime = accessTokenExpireTime;
    }
    // 액세스 토큰 생성
    public String generateAccessToken(Long memberId, Role role) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + accessTokenExpireTime);

        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("memberRole",role)
                .claim("tokenType", "access")
                .setIssuedAt(now)
                .setExpiration(expireTime)
                .signWith(key)
                .compact();
    }
    // 액세스 토큰에서 회원 아이디 추출
    public Long getMemberId(String token) {
        return Long.parseLong(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("memberId").toString());
    }
    // 리프레시 토큰 생성
    public String generateRefreshToken(Long memberId, Role role) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + accessTokenExpireTime * 2);

        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("memberRole",role)
                .claim("tokenType", "refresh")
                .setIssuedAt(now)
                .setExpiration(expireTime)
                .signWith(key)
                .compact();
    }
}
