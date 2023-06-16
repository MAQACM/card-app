package com.app.cards.entry;

import java.util.Map;

import com.app.cards.models.payloads.TokenResp;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.cards.models.payloads.LoginRequest;
import com.app.cards.service.UserMgmtService;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class UsersMgmtController {
    private final UserMgmtService userMgmtService;

    public UsersMgmtController(UserMgmtService userMgmtService) {
        this.userMgmtService = userMgmtService;
    }
    @Operation(summary = "Login ")
    @PostMapping("login")
    public Mono<TokenResp> signIn(@Valid @RequestBody LoginRequest reqBody){
        return this.userMgmtService.signIn(reqBody);
    }
}
