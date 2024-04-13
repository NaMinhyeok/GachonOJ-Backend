package com.gachonoj.apigateway.auth;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutes {

    private final AuthorizationHeaderFilter authorizationHeaderFilter;

    public GatewayRoutes(AuthorizationHeaderFilter authorizationHeaderFilter) {
        this.authorizationHeaderFilter = authorizationHeaderFilter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/member/members", "/member/email", "/member/email/verification", "/member/verification/**","/member/login")
                        .uri("lb://member-service"))
                .route(r -> r.path("/member/**")
                        .filters(f -> f.filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
                        .uri("lb://member-service"))
                .route(r -> r.path("/problem/**")
                        .filters(f -> f.filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
                        .uri("lb://problem-service"))
                .route(r -> r.path("/ai/**")
                        .filters(f -> f.filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
                        .uri("lb://ai-service"))
                .route(r -> r.path("/submission/**")
                        .filters(f -> f.filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
                        .uri("lb://submission-service"))
                .route(r -> r.path("/board/**")
                        .filters(f -> f.filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
                        .uri("lb://board-service"))
                .build();
    }
}
