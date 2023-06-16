package com.app.cards.security;


import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;


import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;


import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurity {
    static Logger log=Logger.getLogger(WebSecurity.class.getName());
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthManager authManager;
    private final SecurityContextRepository securityContextRepository;

    public WebSecurity(UserDetailsServiceImpl userDetailsService, AuthManager authManager, SecurityContextRepository securityContextRepository) {
        this.userDetailsService = userDetailsService;
        this.authManager = authManager;
        this.securityContextRepository = securityContextRepository;
    }


    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception{
        return http.exceptionHandling(exception->exception
                .authenticationEntryPoint((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                ).accessDeniedHandler((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                ))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(authManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/login","/swagger-ui/**","/v3/**").permitAll()
                                .anyExchange().authenticated()
                        )
                .build();
    }


}
