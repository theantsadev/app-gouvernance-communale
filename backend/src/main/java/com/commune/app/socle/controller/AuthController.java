package com.commune.app.socle.controller;

import com.commune.app.socle.dto.LoginRequest;
import com.commune.app.socle.dto.LoginResponse;
import com.commune.app.socle.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/socle/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Intégration : expose la route publique de connexion.
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest requete,
            jakarta.servlet.http.HttpServletRequest request) {
        return ResponseEntity.ok(authService.login(requete, request.getRemoteAddr()));
    }
}
