package com.example.demo.controller;

import com.example.demo.service.AuthCodeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final AuthCodeService authCodeService;

    public AuthController(AuthCodeService authCodeService) {
        this.authCodeService = authCodeService;
    }

    @GetMapping("/oauth2/authorize")
    public String authorize(
            @RequestParam String response_type,
            @RequestParam String client_id,
            @RequestParam String redirect_uri,
            @RequestParam String scope,
            @RequestParam String state,
            Model model
    ) {
        System.out.println("Authorize request:");
        System.out.println("client_id: " + client_id);
        System.out.println("redirect_uri: " + redirect_uri);
        System.out.println("scope: " + scope);
        System.out.println("state: " + state);

        model.addAttribute("redirectUri", redirect_uri);
        model.addAttribute("clientId", client_id); // add for tracking
        model.addAttribute("state", state);
        return "login"; // Thymeleaf or similar template
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("state") String state,
            @RequestParam("client_id") String clientId
    )   
    {
        System.out.println("Login attempt:");
        System.out.println("username: " + username);
        System.out.println("password: " + password);

        // ✅ Generate real authorization code
        String authorizationCode = authCodeService.generateAuthorizationCode(clientId, username);
        System.out.println("authorizationCode " + authorizationCode);
        // String authorizationCode = "abc123";

        // 🔁 Redirect back with code & state
        return "redirect:" + redirectUri +
                "?code=" + authorizationCode +
                "&state=" + state;
    }
}
