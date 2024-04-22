package com.gachonoj.apigateway.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    private final Long accessTokenExpireTime;
    private final Long refreshTokenExpireTime;
    private final SecretKey secretKey;
    public JwtUtil(@Value("${spring.jwt.secret}")String secret, @Value("${spring.jwt.access-token.expire-time}")Long accessTokenExpireTime, @Value("${spring.jwt.refresh-token.expire-time}")Long refreshTokenExpireTime){
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
    public Long getMemberId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("memberId", Long.class);
    }

    public void isExpired(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            throw new ExpiredJwtException(null, null, "Token is expired", e);
        } catch (MalformedJwtException e) {
            // 토큰이 잘못된 형식인 경우
            throw new IllegalArgumentException("Invalid token format");
        } catch (Exception e) {
            // 그 외의 예외
            throw new RuntimeException("An error occurred while checking the token", e);
        }
    }
    public Boolean isRefreshTokenExpired(String token) {
        log.info(token);
        log.info("리프레시토큰 만료됐는지 확인하는 곳 token : " + token);
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            return true;
        } catch (MalformedJwtException e) {
            // 토큰이 잘못된 형식인 경우
            throw new IllegalArgumentException("Invalid token format", e);
        } catch (Exception e) {
            // 그 외의 예외
            throw new RuntimeException("An error occurred while checking the token", e);
        }
    }

    public String createAccessJwt(String role, Long memberId) {

        return Jwts.builder()
                .setHeaderParam("type", "access")
                .claim("role", role)
                .claim("memberId",memberId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpireTime))
                .signWith(secretKey)
                .compact();
    }
    public String createRefreshJwt(String role, Long memberId) {

        return Jwts.builder()
                .setHeaderParam("type", "refresh")
                .claim("memberId",memberId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpireTime))
                .signWith(secretKey)
                .compact();
    }
}
