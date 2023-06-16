package com.app.cards.service.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.naming.AuthenticationException;

import com.app.cards.models.payloads.TokenResp;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.app.cards.models.payloads.LoginRequest;
import com.app.cards.service.UserMgmtService;
import com.app.cards.utils.TokenUtils;
import com.app.cards.security.*;

import reactor.core.publisher.Mono;

import static com.app.cards.utils.TokenUtils.createUserFromAuthentication;

@Service
public class UserMgmtServiceImpl implements UserMgmtService {
    private final AuthManager authManager;
    private final TokenUtils tokenUtils;

    public UserMgmtServiceImpl( AuthManager authManager, TokenUtils tokenUtils) {
        this.authManager = authManager;
        this.tokenUtils = tokenUtils;
    }

    @Override
    public Mono<TokenResp> signIn(LoginRequest loginRequest) {
        return authManager.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword()).flatMap(resp->{
            String jwt=tokenUtils.doGenerateToken(resp);
            return Mono.just(new TokenResp(jwt));
        }).onErrorResume(error->Mono.error(new AuthenticationException("Could not authenticate user")));
    }

}
