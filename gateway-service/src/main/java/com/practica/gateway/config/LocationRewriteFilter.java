package com.practica.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class LocationRewriteFilter {

    @Bean
    public GlobalFilter rewriteLocationFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                HttpHeaders headers = exchange.getResponse().getHeaders();
                String location = headers.getFirst(HttpHeaders.LOCATION);

                if (location != null && location.contains("oauth-server:9000")) {
                    // Solo reescribir si la URL contiene oauth-server:9000
                    String newLocation = location.replace("http://oauth-server:9000", "http://localhost:8080");
                    headers.set(HttpHeaders.LOCATION, newLocation);
                }
            }));
        };
    }
}
