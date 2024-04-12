package com.gachonoj.apigateway.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("Request path: {}", request.getPath());
        log.info("Request method: {}", request.getMethod());
        log.info("Request URI: {}", request.getURI());
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // order in which filters are applied. -1 means it will be applied first.
    }
}