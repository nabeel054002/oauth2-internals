package com.example.demo.controller;

import com.example.demo.repository.InMemoryAuthorizationCodeStore;
import com.example.demo.repository.InMemoryAuthorizationCodeStore.AuthCodeData;
import com.example.demo.service.AuthCodeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/oauth2")
public class TokenController {

    private final AuthCodeService authCodeService;
    private final InMemoryAuthorizationCodeStore inMemoryAuthorizationCodeStore;

    @Autowired
    public TokenController(AuthCodeService authCodeService, InMemoryAuthorizationCodeStore inMemoryAuthorizationCodeStore) {
        this.authCodeService = authCodeService;
        this.inMemoryAuthorizationCodeStore = inMemoryAuthorizationCodeStore;
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> token(
            @RequestParam("grant_type") String grantType,
            @RequestParam("code") String code,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("client_id") String clientId
    ) {
        System.out.println("Entered url req!");
        if (!"authorization_code".equals(grantType)) {
            return ResponseEntity.badRequest().body("Unsupported grant_type");
        }

        Optional<AuthCodeData> codeDataOpt = authCodeService.consumeAuthorizationCode(code);
        // if (codeDataOpt.isEmpty()) {
        //     return ResponseEntity.badRequest().body("Invalid or expired authorization code");
        // }

        System.out.println("abt to do code data styff " + code);

        AuthCodeData codeData = codeDataOpt.get();
        System.out.println(codeData);
        this.inMemoryAuthorizationCodeStore.consume(code);

        // Optional: validate clientId and redirectUri match what's stored

        String accessToken = UUID.randomUUID().toString();

        Map<String, Object> response = new HashMap<>();
        response.put("access_token", accessToken);
        response.put("token_type", "Bearer");
        response.put("expires_in", 3600);
        response.put("scope", "basic");

        return ResponseEntity.ok(response);
    }
}
