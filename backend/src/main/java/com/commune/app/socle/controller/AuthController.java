package com.commune.app.socle.controller;

import com.commune.app.socle.dto.LoginRequest;
import com.commune.app.socle.dto.LoginResponse;
import com.commune.app.socle.dto.SessionValidResponse;
import com.commune.app.socle.service.AuthService;
import org.springframework.security.core.Authentication;
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

    // Intégration : révoque la session courante et historise la déconnexion.
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            jakarta.servlet.http.HttpServletRequest request,
            Authentication authentication) {
        String token = extraireToken(request);
            authService.logout(token, authentication.getName(), request.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }

    // Intégration : confirme que le filtre a validé la session courante.
    @PostMapping("/session/valid")
    public ResponseEntity<SessionValidResponse> sessionValide() {
        return ResponseEntity.ok(new SessionValidResponse(true));
    }

    private String extraireToken(jakarta.servlet.http.HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalStateException("Le token de session est obligatoire.");
        }
        return authorization.substring("Bearer ".length()).trim();
    }
}
