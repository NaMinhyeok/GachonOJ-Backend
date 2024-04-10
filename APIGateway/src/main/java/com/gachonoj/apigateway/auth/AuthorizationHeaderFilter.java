package com.gachonoj.apigateway.auth;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    @Override
    public GatewayFilter apply(AuthorizationHeaderFilter.Config config) {
        return (exchange, chain) -> {
            return chain.filter(exchange);
        };
    }
    public static class Config {

    }
}
