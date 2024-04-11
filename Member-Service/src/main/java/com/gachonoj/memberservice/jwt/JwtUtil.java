package com.gachonoj.memberservice.jwt;

import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Getter
@Component
public class JwtUtil {
    private final Long accessTokenExpireTime;
    private final Long refreshTokenExpireTime;
    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}")String secret, @Value("${spring.jwt.access-token.expire-time}")Long accessTokenExpireTime, @Value("${spring.jwt.refresh-token.expire-time}")Long refreshTokenExpireTime) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }
    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }
    public Boolean isRefreshTokenExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(String username, String role, Long memberId) {

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .claim("memberId",memberId)
                .claim("tokenType", "access")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpireTime))
                .signWith(secretKey)
                .compact();
    }
    public String createRefreshJwt(Long memberId) {

        return Jwts.builder()
                .claim("memberId",memberId)
                .claim("tokenType", "refresh")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpireTime))
                .signWith(secretKey)
                .compact();
    }
}
