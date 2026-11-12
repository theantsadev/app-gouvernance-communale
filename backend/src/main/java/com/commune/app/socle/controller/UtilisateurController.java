package com.commune.app.socle.controller;

import com.commune.app.socle.entity.Utilisateur;
import com.commune.app.socle.lib.UtilisateurLib;
import com.commune.app.socle.repository.UtilisateurRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/socle/utilisateur")
public class UtilisateurController {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurController(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    // Intégration : retourne l'utilisateur provenant du SecurityContext.
    @GetMapping("/current")
    public ResponseEntity<UtilisateurLib> utilisateurConnecte(
            Authentication authentication) {
        Utilisateur utilisateur = utilisateurRepository.findById(authentication.getName())
                .orElseThrow();
        return ResponseEntity.ok(new UtilisateurLib(
                utilisateur.getIdUtilisateur(),
                utilisateur.getEmail(),
                utilisateur.getNom(),
                utilisateur.getPrenom()));
    }
}
