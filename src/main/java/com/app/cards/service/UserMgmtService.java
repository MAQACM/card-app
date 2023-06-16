package com.app.cards.service;

import java.util.Map;

import com.app.cards.models.payloads.LoginRequest;

import com.app.cards.models.payloads.TokenResp;
import reactor.core.publisher.Mono;

public interface UserMgmtService {
    Mono<TokenResp> signIn(LoginRequest loginRequest);
}
