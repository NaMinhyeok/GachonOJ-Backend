package com.gachonoj.apigateway.auth;

import com.netflix.discovery.converters.Auto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private JwtUtil jwtUtil;

    public AuthorizationHeaderFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }
    public static class Config {
    }
    @Override
    public GatewayFilter apply(Config config){
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            log.info("path: " + path);
            if(!request.getHeaders().containsKey("Authorization")){
                return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
            }
            List<String> headers = request.getHeaders().get("Authorization");
            String token = headers.get(0).replace("Bearer ", "");
            if(path.startsWith("/member/admin/") || path.startsWith("/problem/admin/") || path.startsWith("/ai/admin/") || path.startsWith("/submission/admin/") || path.startsWith("/board/admin/")){
                try{
                    if(!jwtUtil.isExpired(token)){
                        log.info("token: " + token);
                        log.info("Role: " + jwtUtil.getRole(token));
                        if(jwtUtil.getRole(token).equals("ROLE_ADMIN")){
                            Long memberId = jwtUtil.getMemberId(token);
                            ServerHttpRequest modifiedRequest = request.mutate().header("X-Authorization-Id", String.valueOf(memberId)).build();
                            return chain.filter(exchange.mutate().request(modifiedRequest).build());
                        } else {
                            return onError(exchange, "Token is not admin", HttpStatus.UNAUTHORIZED);
                        }
                    } else {
                        return onError(exchange, "Token is expired", HttpStatus.UNAUTHORIZED);
                    }
                }catch(Exception e){
                    System.out.println("Exception occurred while parsing JWT: " + e.getMessage());
                    return chain.filter(exchange);
                }
            } else {
                try{
                    if(!jwtUtil.isExpired(token)){
                        Long memberId = jwtUtil.getMemberId(token);
                        String role = jwtUtil.getRole(token);
                        ServerHttpRequest modifiedRequest = request.mutate().header("X-Authorization-Id", String.valueOf(memberId)).build();
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    } else {
                        return onError(exchange, "Token is expired", HttpStatus.UNAUTHORIZED);
                    }
                }catch(Exception e){
                    System.out.println("Exception occurred while parsing JWT: " + e.getMessage());
                    return chain.filter(exchange);
                }
            }
        });
    }
//    @Override
//    public GatewayFilter apply(Config config){
//        return ((exchange, chain) -> {
//            ServerHttpRequest request = exchange.getRequest();
//            String path = request.getURI().getPath();
//
//            // 특정 경로에 대해서만 토큰을 검사합니다.
//            if (path.startsWith("/api/member/admin/") || path.startsWith("/api/problem/admin/") || path.startsWith("/api/ai/admin/") || path.startsWith("/api/submission/admin/") || path.startsWith("/api/board/admin/")) {
//                if(!request.getHeaders().containsKey("Authorization")){
//                    return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
//                }
//                List<String> headers = request.getHeaders().get("Authorization");
//                String token = headers.get(0).replace("Bearer ", "");
//                try{
//                    if(!jwtUtil.isExpired(token)){
//                        if(jwtUtil.getRole(token).equals("ADMIN")){
//                            Long memberId = jwtUtil.getMemberId(token);
//                            ServerHttpRequest modifiedRequest = request.mutate().header("X-Authorization-Id", String.valueOf(memberId)).build();
//                            return chain.filter(exchange.mutate().request(modifiedRequest).build());
//                        } else {
//                            return onError(exchange, "Token is not admin", HttpStatus.UNAUTHORIZED);
//                        }
//                    } else {
//                        return onError(exchange, "Token is expired", HttpStatus.UNAUTHORIZED);
//                    }
//                }catch(Exception e){
//                    System.out.println("Exception occurred while parsing JWT: " + e.getMessage());
//                    return chain.filter(exchange);
//                }
//            } else {
//                // 특정 경로가 아닌 경우, 필터의 로직을 실행하지 않고 다음 필터로 넘어갑니다.
//                return chain.filter(exchange);
//            }
//        });
//    }
    private Mono<Void> onError(ServerWebExchange exchange, String errorMsg, HttpStatus httpStatus) {
        log.error(errorMsg);

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        return response.setComplete();
    }
}
