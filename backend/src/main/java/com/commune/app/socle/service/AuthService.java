package com.commune.app.socle.service;

import com.commune.app.commun.AuthException;
import com.commune.app.socle.dto.LoginRequest;
import com.commune.app.socle.entity.Utilisateur;
import com.commune.app.socle.lib.UtilisateurLib;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UtilisateurService utilisateurService,
            PasswordEncoder passwordEncoder) {
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
    }

    // Métier : contrôle la requête, l'utilisateur, le mot de passe et le compte actif.
    @Transactional(readOnly = true)
    public UtilisateurLib login(LoginRequest requete) {
        // Métier : contrôle unitaire des champs.
        requete.valider();

        // Métier : contrôle complexe de l'utilisateur et du mot de passe.
        Utilisateur utilisateur = utilisateurService.findByEmail(requete.getEmail());
        if (!passwordEncoder.matches(
                requete.getMotDePasse(),
                utilisateur.getMotDePasseHash())) {
            throw AuthException.identifiantsInvalides();
        }

        // Métier : contrôle complexe du compte actif.
        if (!utilisateur.isActif()) {
            throw new AuthException(
                    "AUTH_COMPTE_DESACTIVE",
                    "Ce compte est désactivé.");
        }

        // Métier : retourne uniquement les informations publiques.
        return new UtilisateurLib(
                utilisateur.getIdUtilisateur(),
                utilisateur.getEmail(),
                utilisateur.getNom(),
                utilisateur.getPrenom());
    }
}
