package com.gachonoj.apigateway.auth;

import com.netflix.discovery.converters.Auto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
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
import java.util.Objects;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final JwtUtil jwtUtil;

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
            if(!request.getHeaders().containsKey("Authorization")){
                return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
            }
            String accessToken = getTokenFromHeader(request);
            try{
                jwtUtil.isExpired(accessToken);
                Long memberId = jwtUtil.getMemberId(accessToken);
                String role = jwtUtil.getRole(accessToken);
                if(isAdminPath(path) && !role.equals("ROLE_ADMIN")){
                    return onError(exchange, "Token is not admin", HttpStatus.UNAUTHORIZED);
                }
                ServerHttpRequest modifiedRequest = request.mutate().header("X-Authorization-Id", String.valueOf(memberId)).build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (ExpiredJwtException e){
                return onError(exchange, "Token is expired", HttpStatus.FORBIDDEN);
            } catch (IllegalArgumentException e){
                return onError(exchange, "Invalid token format", HttpStatus.UNAUTHORIZED);
            } catch (RuntimeException e){
                return onError(exchange, "An error occurred while checking the token", HttpStatus.UNAUTHORIZED);
            } catch(Exception e){
                System.out.println("Exception occurred while parsing JWT: " + e.getMessage());
                return chain.filter(exchange);
            }
        });
    }

    private String getTokenFromHeader(ServerHttpRequest request){
        List<String> headers = request.getHeaders().get("Authorization");
        return headers.get(0).replace("Bearer ", "");
    }

    private boolean isAdminPath(String path){
        return path.startsWith("/member/admin/") || path.startsWith("/problem/admin/") || path.startsWith("/ai/admin/") || path.startsWith("/submission/admin/") || path.startsWith("/board/admin/");
    }
    private Mono<Void> onError(ServerWebExchange exchange, String errorMsg, HttpStatus httpStatus) {
        log.error(errorMsg);

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        return response.setComplete();
    }
}
