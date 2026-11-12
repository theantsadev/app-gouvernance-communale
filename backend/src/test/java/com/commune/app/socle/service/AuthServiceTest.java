package com.commune.app.socle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.commune.app.commun.AuthException;
import com.commune.app.socle.dto.LoginRequest;
import com.commune.app.socle.dto.LoginResponse;
import com.commune.app.socle.entity.Utilisateur;
import com.commune.app.socle.repository.UtilisateurRepository;
import com.commune.app.socle.security.JwtService;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest {

    private static final String EMAIL = "agent@example.org";
    private static final String MOT_DE_PASSE = "secret";

    @Test
    void refuseEmailObligatoire() {
        AuthException exception = assertThrows(
                AuthException.class,
                () -> new LoginRequest("", MOT_DE_PASSE));

        assertEquals("FORMULAIRE_INVALIDE", exception.getCode());
        assertEquals("email", exception.getChamp());
    }

    @Test
    void refuseMotDePasseObligatoire() {
        AuthException exception = assertThrows(
                AuthException.class,
                () -> new LoginRequest(EMAIL, ""));

        assertEquals("FORMULAIRE_INVALIDE", exception.getCode());
        assertEquals("motDePasse", exception.getChamp());
    }

    @Test
    void refuseEmailInconnuAvecMessageGenerique() {
        HistoriqueCapture historique = new HistoriqueCapture();
        AuthService service = service(new FakeUtilisateurService(null), historique);

        AuthException exception = assertThrows(
                AuthException.class,
                () -> service.login(new LoginRequest(EMAIL, MOT_DE_PASSE), "127.0.0.1"));

        assertEquals("AUTH_IDENTIFIANTS_INVALIDES", exception.getCode());
        assertEquals("E-mail ou mot de passe incorrect.", exception.getMessage());
        assertEquals("ECHEC", historique.type);
        assertEquals(EMAIL, historique.email);
    }

    @Test
    void refuseMotDePasseIncorrect() throws Exception {
        Utilisateur utilisateur = utilisateur(true);
        HistoriqueCapture historique = new HistoriqueCapture();
        AuthService service = service(
                new FakeUtilisateurService(utilisateur),
                historique,
                new FakePasswordEncoder(false));

        AuthException exception = assertThrows(
                AuthException.class,
                () -> service.login(new LoginRequest(EMAIL, MOT_DE_PASSE), "127.0.0.1"));

        assertEquals("AUTH_IDENTIFIANTS_INVALIDES", exception.getCode());
        assertEquals("USR-001", historique.idUtilisateur);
    }

    @Test
    void refuseCompteDesactive() throws Exception {
        Utilisateur utilisateur = utilisateur(false);
        HistoriqueCapture historique = new HistoriqueCapture();
        AuthService service = service(new FakeUtilisateurService(utilisateur), historique);

        AuthException exception = assertThrows(
                AuthException.class,
                () -> service.login(new LoginRequest(EMAIL, MOT_DE_PASSE), "127.0.0.1"));

        assertEquals("AUTH_COMPTE_DESACTIVE", exception.getCode());
        assertEquals("ECHEC", historique.type);
    }

    @Test
    void accepteConnexionEtRetourneUnToken() throws Exception {
        Utilisateur utilisateur = utilisateur(true);
        HistoriqueCapture historique = new HistoriqueCapture();
        AuthService service = service(new FakeUtilisateurService(utilisateur), historique);

        LoginResponse reponse = service.login(
                new LoginRequest(EMAIL, MOT_DE_PASSE),
                "127.0.0.1");

        assertEquals("USR-001", reponse.utilisateur().idUtilisateur());
        assertTrue(reponse.token().split("\\.").length == 3);
        assertEquals("CONNEXION", historique.type);
    }

    private AuthService service(
            UtilisateurService utilisateurService,
            HistoriqueCapture historique) {
        return service(utilisateurService, historique, new FakePasswordEncoder(true));
    }

    private AuthService service(
            UtilisateurService utilisateurService,
            HistoriqueCapture historique,
            PasswordEncoder passwordEncoder) {
        return new AuthService(
                utilisateurService,
                passwordEncoder,
                historique,
                new JwtService("development-secret-at-least-32-characters", 3600),
                new FakeSessionAuthentificationService());
    }

    private Utilisateur utilisateur(boolean actif) throws Exception {
        var constructeur = Utilisateur.class.getDeclaredConstructor();
        constructeur.setAccessible(true);
        Utilisateur utilisateur = constructeur.newInstance();
        champ(utilisateur, "idUtilisateur", "USR-001");
        champ(utilisateur, "email", EMAIL);
        champ(utilisateur, "motDePasseHash", "hash");
        champ(utilisateur, "nom", "Nom");
        champ(utilisateur, "prenom", "Prénom");
        champ(utilisateur, "actif", actif);
        champ(utilisateur, "dateCreation", LocalDateTime.now());
        champ(utilisateur, "dateModification", LocalDateTime.now());
        champ(utilisateur, "idRole", "AGENT_TECHNIQUE");
        return utilisateur;
    }

    private void champ(Utilisateur utilisateur, String nom, Object valeur) throws Exception {
        Field champ = Utilisateur.class.getDeclaredField(nom);
        champ.setAccessible(true);
        champ.set(utilisateur, valeur);
    }

    private static class FakeUtilisateurService extends UtilisateurService {
        private final Utilisateur utilisateur;

        FakeUtilisateurService(Utilisateur utilisateur) {
            super((UtilisateurRepository) null, null);
            this.utilisateur = utilisateur;
        }

        @Override
        public Utilisateur findByEmail(String email) {
            if (utilisateur == null) {
                throw AuthException.identifiantsInvalides();
            }
            return utilisateur;
        }
    }

    private record FakePasswordEncoder(boolean resultat) implements PasswordEncoder {
        @Override
        public String encode(CharSequence rawPassword) {
            return rawPassword.toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return resultat;
        }
    }

    private static class FakeSessionAuthentificationService
            extends SessionAuthentificationService {

        FakeSessionAuthentificationService() {
            super(null);
        }

        @Override
        public void creer(
                String token,
                String idUtilisateur,
                java.time.LocalDateTime dateExpiration) {
        }
    }

    private static class HistoriqueCapture extends HistoriqueConnexionService {
        private String email;
        private String type;
        private String idUtilisateur;

        HistoriqueCapture() {
            super(null);
        }

        @Override
        public void enregistrerConnexion(Utilisateur utilisateur, String adresseIp) {
            email = utilisateur.getEmail();
            type = "CONNEXION";
            idUtilisateur = utilisateur.getIdUtilisateur();
        }

        @Override
        public void enregistrerEchec(String email, String adresseIp, String idUtilisateur) {
            this.email = email;
            this.type = "ECHEC";
            this.idUtilisateur = idUtilisateur;
        }
    }
}
