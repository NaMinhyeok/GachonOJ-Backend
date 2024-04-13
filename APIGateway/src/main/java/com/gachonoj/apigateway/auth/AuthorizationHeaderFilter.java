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
            if(!request.getHeaders().containsKey("Authorization")){
                return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
            }
            List<String> headers = request.getHeaders().get("Authorization");
            String token = headers.get(0).replace("Bearer ", "");
            try{
                if(!jwtUtil.isExpired(token)){
                    Long memberId = jwtUtil.getMemberId(token);
                    String memberRole = jwtUtil.getRole(token);
                    if("ROLE_ADMIN".equals(memberRole)){
                        ServerHttpRequest modifiedRequest = request.mutate().header("X-Authorization-Id", String.valueOf(memberId)).header("X-Authorization-Role", memberRole).build();
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    } else if("ROLE_STUDENT".equals(memberRole)){
                        ServerHttpRequest modifiedRequest = request.mutate().header("X-Authorization-Id", String.valueOf(memberId)).build();
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    } else if("ROLE_PROFESSOR".equals(memberRole)){
                        ServerHttpRequest modifiedRequest = request.mutate().header("X-Authorization-Id", String.valueOf(memberId)).build();
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    } else {
                        return onError(exchange, "Permission denied", HttpStatus.FORBIDDEN);  // 권한이 없는 경우 에러 반환
                    }
                } else {
                    return onError(exchange, "Token is expired", HttpStatus.UNAUTHORIZED);
                }
            }catch(Exception e){
                System.out.println("Exception occurred while parsing JWT: " + e.getMessage());
                return chain.filter(exchange);
            }
        });
    }
    private Mono<Void> onError(ServerWebExchange exchange, String errorMsg, HttpStatus httpStatus) {
        log.error(errorMsg);

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        return response.setComplete();
    }
}
