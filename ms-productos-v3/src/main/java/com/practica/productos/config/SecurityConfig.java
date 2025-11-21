package com.practica.productos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Configuración de seguridad para perfil K8s (sin autenticación)
     * Permite todo el tráfico sin restricciones
     */
    @Bean
    @Profile("k8s")
    public SecurityWebFilterChain securityWebFilterChainK8s(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .build();
    }

    /**
     * Configuración de seguridad para otros perfiles (con OAuth2)
     * Requiere autenticación JWT para todos los endpoints excepto actuator
     */
    @Bean
    @Profile("!k8s")
    public SecurityWebFilterChain securityWebFilterChainOAuth(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {})
                )
                .build();
    }
}
