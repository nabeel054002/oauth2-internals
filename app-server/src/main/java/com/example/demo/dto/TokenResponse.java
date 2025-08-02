package com.example.demo.dto;

import lombok.Data;

@Data
public class TokenResponse {
    
    private String access_token;
    private String token_type;
    private int expires_in;
    private String refresh_token;
    private String scope;

    // Getters and setters (or use Lombok's @Data)
    
}
