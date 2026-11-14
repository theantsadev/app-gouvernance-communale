package com.commune.app.socle.controller;

import com.commune.app.socle.lib.UtilisateurLib;
import com.commune.app.socle.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/socle/utilisateur")
public class UtilisateurController {

    // Intégration : retourne l'utilisateur provenant du SecurityContext.
    @GetMapping("/current")
    public ResponseEntity<UtilisateurLib> utilisateurConnecte(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(UtilisateurLib.fromEntity(customUserDetails.getUtilisateur()));
    }
}
