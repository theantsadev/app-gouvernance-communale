package com.commune.app.socle.controller;

import com.commune.app.socle.lib.UtilisateurLib;
import com.commune.app.socle.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/socle/utilisateur")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    // Intégration : retourne l'utilisateur provenant du SecurityContext.
    @GetMapping("/current")
    public ResponseEntity<UtilisateurLib> utilisateurConnecte(
            Authentication authentication) {
        return ResponseEntity.ok(utilisateurService.findPublicById(authentication.getName()));
    }
}
