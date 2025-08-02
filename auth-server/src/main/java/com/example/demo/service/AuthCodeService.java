package com.example.demo.service;

import com.example.demo.repository.InMemoryAuthorizationCodeStore;
import com.example.demo.repository.InMemoryAuthorizationCodeStore.AuthCodeData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthCodeService {

    private final InMemoryAuthorizationCodeStore codeStore;

    @Autowired
    public AuthCodeService(InMemoryAuthorizationCodeStore codeStore) {
        this.codeStore = codeStore;
    }

    // Generate and store a new authorization code
    public String generateAuthorizationCode(String clientId, String username) {
        String code = UUID.randomUUID().toString(); // Generate secure random code
        AuthCodeData data = new InMemoryAuthorizationCodeStore.AuthCodeData(clientId, username, Instant.now());
        codeStore.store(code, data);
        return code;
    }

    // Consume (validate and remove) a code
    public Optional<AuthCodeData> consumeAuthorizationCode(String code) {
        return codeStore.consume(code);
    }
}
