package com.commune.app.socle.service;

import com.commune.app.commun.AuthException;
import com.commune.app.socle.dto.LoginRequest;
import com.commune.app.socle.dto.LoginResponse;
import com.commune.app.socle.entity.Utilisateur;
import com.commune.app.socle.lib.UtilisateurLib;
import com.commune.app.socle.security.JwtService;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;
    private final HistoriqueConnexionService historiqueConnexionService;
    private final JwtService jwtService;
    private final SessionAuthentificationService sessionService;

    public AuthService(
            UtilisateurService utilisateurService,
            PasswordEncoder passwordEncoder,
            HistoriqueConnexionService historiqueConnexionService,
            JwtService jwtService,
            SessionAuthentificationService sessionService) {
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
        this.historiqueConnexionService = historiqueConnexionService;
        this.jwtService = jwtService;
        this.sessionService = sessionService;
    }

    // Métier : contrôle la requête, l'utilisateur, le mot de passe et le compte actif.
    @Transactional
    public LoginResponse login(LoginRequest requete, String adresseIp) {
        // Métier : contrôle unitaire des champs.
        requete.valider();

        Utilisateur utilisateur = null;
        try {
            // Métier : contrôle complexe de l'utilisateur et du mot de passe.
            utilisateur = utilisateurService.findByEmail(requete.getEmail());
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

            // Base : conserve la connexion réussie.
            historiqueConnexionService.enregistrerConnexion(utilisateur, adresseIp);

            // Métier : crée le token et retourne les informations publiques.
            String token = jwtService.generer(utilisateur.getIdUtilisateur());
            sessionService.creer(
                    token,
                    utilisateur.getIdUtilisateur(),
                    LocalDateTime.ofInstant(
                            jwtService.dateExpiration(token),
                            java.time.ZoneId.systemDefault()));
            UtilisateurLib utilisateurLib = new UtilisateurLib(
                    utilisateur.getIdUtilisateur(),
                    utilisateur.getEmail(),
                    utilisateur.getNom(),
                    utilisateur.getPrenom());
            return new LoginResponse(token, jwtService.getDureeSecondes(), utilisateurLib);
        } catch (AuthException exception) {
            // Base : conserve l'échec dans une transaction séparée.
            String idUtilisateur = utilisateur == null
                    ? null
                    : utilisateur.getIdUtilisateur();
            historiqueConnexionService.enregistrerEchec(
                    requete.getEmail(),
                    adresseIp,
                    idUtilisateur);
            throw exception;
        }
    }

    // Métier : révoque la session et historise la déconnexion.
    @Transactional
    public void logout(String token, Utilisateur utilisateur, String adresseIp) {
        sessionService.revoquer(token);
        historiqueConnexionService.enregistrerDeconnexion(utilisateur, adresseIp);
    }
}
