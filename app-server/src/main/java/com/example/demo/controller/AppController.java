package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.dto.TokenResponse;

import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Controller
public class AppController {

    String authUrl = "http://localhost:8080";
    String hostUrl = "http://localhost:9000";

    private final WebClient webClient;

    @Autowired
    public AppController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/")
    public String home() {
        return "home";  // Render home.html
    }

    @GetMapping("/login")
    public String login() {
        System.out.println("Entered login route");
        // Redirect user to the Auth Server with required params

        String redirectUrl = UriComponentsBuilder
        .fromUriString(authUrl)
        .path("/oauth2/authorize")
        .queryParam("response_type", "code")
        .queryParam("client_id", "app-client")
        .queryParam("redirect_uri", hostUrl + "/callback")
        .queryParam("scope", "read")
        .queryParam("state", "xyz123")
        .toUriString();

        System.out.println("redirectUrl: " + redirectUrl);

        return "redirect:" + redirectUrl;
    }

    @GetMapping("/callback")
    public String callback(@RequestParam String code,
                           @RequestParam String state,
                           Model model) {
        // This gets called after user authorizes on auth-server

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        System.out.println("code: " + code);
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", hostUrl + "/callback");
        formData.add("client_id", "app-client");
        formData.add("client_secret", "app-secret");

        TokenResponse tokenResponse = webClient.post()
        .uri(authUrl + "/oauth2/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .bodyValue(formData)
        .retrieve()
        .bodyToMono(TokenResponse.class)
        .block();
        System.out.println("tokenResponse " + tokenResponse);
        model.addAttribute("code", code);
        model.addAttribute("state", state);
        model.addAttribute("token", tokenResponse);
        return "callback";  // Render callback.html with code & state
    }
}
