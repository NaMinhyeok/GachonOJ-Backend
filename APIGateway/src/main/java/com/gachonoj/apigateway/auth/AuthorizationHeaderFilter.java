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
import java.util.Objects;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    public AuthorizationHeaderFilter(JwtUtil jwtUtil, RedisService redisService) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
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
            String accessToken = getTokenFromHeader(request);
            try{
                // 액세스 토큰이 만료 되었는지 확인
                if(jwtUtil.isExpired(accessToken)){
                    // 만료 되었을 경우 리프레시 토큰을 통해서 액세스 토큰 재발급
                    // 리프레시 토큰을 가져오기 위해 액세스토큰의 멤버아이디를 추출 후 redis에서 리프레시 토큰을 가져옴 왜냐하면 key값이 멤버아이디 이기 때문에
                    String refreshToken = redisService.getData(jwtUtil.getMemberId(accessToken).toString());
                    // 가져온 리프레시토큰이랑 액세스토큰의 멤버아이디가 같은지 확인 후 같으면 액세스토큰 재발급
                    if(Objects.isNull(refreshToken)){
                        return onError(exchange, "RefreshToken is expired, So please 로그인 다시", HttpStatus.UNAUTHORIZED);
                    } else if(Objects.equals(jwtUtil.getMemberId(refreshToken), jwtUtil.getMemberId(accessToken))){
                        accessToken = jwtUtil.createAccessJwt(jwtUtil.getRole(accessToken), jwtUtil.getMemberId(accessToken));
                    } else if(!Objects.equals(jwtUtil.getMemberId(refreshToken), jwtUtil.getMemberId(accessToken))){
                        return onError(exchange, "토큰 정보가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
                    } else{
                        return onError(exchange, "Token is expired", HttpStatus.UNAUTHORIZED);
                    }
                }
                Long memberId = jwtUtil.getMemberId(accessToken);
                String role = jwtUtil.getRole(accessToken);
                if(isAdminPath(path) && !role.equals("ROLE_ADMIN")){
                    return onError(exchange, "Token is not admin", HttpStatus.UNAUTHORIZED);
                }
                ServerHttpRequest modifiedRequest = request.mutate().header("X-Authorization-Id", String.valueOf(memberId)).build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }catch(Exception e){
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
