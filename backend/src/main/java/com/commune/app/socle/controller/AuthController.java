package com.commune.app.socle.controller;

import com.commune.app.socle.dto.LoginRequest;
import com.commune.app.socle.dto.LoginResponse;
import com.commune.app.socle.dto.SessionValidResponse;
import com.commune.app.socle.entity.Utilisateur;
import com.commune.app.socle.repository.UtilisateurRepository;
import com.commune.app.socle.service.HistoriqueConnexionService;
import com.commune.app.socle.service.AuthService;
import com.commune.app.socle.service.SessionAuthentificationService;
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
    private final SessionAuthentificationService sessionService;
    private final HistoriqueConnexionService historiqueConnexionService;
    private final UtilisateurRepository utilisateurRepository;

    public AuthController(
            AuthService authService,
            SessionAuthentificationService sessionService,
            HistoriqueConnexionService historiqueConnexionService,
            UtilisateurRepository utilisateurRepository) {
        this.authService = authService;
        this.sessionService = sessionService;
        this.historiqueConnexionService = historiqueConnexionService;
        this.utilisateurRepository = utilisateurRepository;
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
        Utilisateur utilisateur = utilisateurRepository.findById(authentication.getName())
                .orElseThrow();
        sessionService.revoquer(token);
        historiqueConnexionService.enregistrerDeconnexion(
                utilisateur,
                request.getRemoteAddr());
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
