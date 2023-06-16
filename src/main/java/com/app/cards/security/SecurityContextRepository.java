package com.app.cards.security;

import com.app.cards.utils.TokenUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.app.cards.utils.TokenUtils.getClaim;

@AllArgsConstructor
@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {
    private final TokenUtils tokenUtils;
    private final AuthManager authManager;
    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .flatMap(auth -> {
                    String authToken = auth.substring(7);
                    Authentication authentication= new UsernamePasswordAuthenticationToken(authToken, authToken);
                    return this.authManager.authenticate(authentication).map(SecurityContextImpl::new);
                });
    }

}
