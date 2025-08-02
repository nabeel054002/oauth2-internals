package com.example.demo.repository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

import org.springframework.stereotype.Component;

@Component
public class InMemoryAuthorizationCodeStore {

    // Inner class to represent auth code data
    public static class AuthCodeData {
        private final String clientId;
        private final String username;
        private final Instant createdAt;

        public AuthCodeData(String clientId, String username, Instant createdAt) {
            this.clientId = clientId;
            this.username = username;
            this.createdAt = createdAt;
        }

        public String getClientId() {
            return clientId;
        }

        public String getUsername() {
            return username;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }
    }

    // Map to store auth codes
    private final Map<String, AuthCodeData> codeStore = new ConcurrentHashMap<>();
    private final long ttlSeconds = 300; // 5 minutes TTL

    // Store a new code
    public void store(String code, AuthCodeData data) {
        codeStore.put(code, data);
    }

    // Retrieve and consume (remove) the code
    public Optional<AuthCodeData> consume(String code) {
        AuthCodeData data = codeStore.remove(code);
        if (data == null) return Optional.empty();

        // Check expiration
        if (Instant.now().isAfter(data.getCreatedAt().plusSeconds(ttlSeconds))) {
            return Optional.empty(); // expired
        }

        return Optional.of(data);
    }

    // Optional: clear expired codes (not mandatory for small-scale)
    public void cleanup() {
        Instant now = Instant.now();
        codeStore.entrySet().removeIf(entry ->
                now.isAfter(entry.getValue().getCreatedAt().plusSeconds(ttlSeconds)));
    }
}
