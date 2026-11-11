package com.commune.app.socle.controller;

import com.commune.app.socle.dto.LoginRequest;
import com.commune.app.socle.lib.UtilisateurLib;
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
    public ResponseEntity<UtilisateurLib> login(@RequestBody LoginRequest requete) {
        return ResponseEntity.ok(authService.login(requete));
    }
}
