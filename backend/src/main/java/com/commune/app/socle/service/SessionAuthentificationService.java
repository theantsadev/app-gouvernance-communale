package com.commune.app.socle.service;

import com.commune.app.socle.entity.SessionAuthentification;
import com.commune.app.socle.repository.SessionAuthentificationRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionAuthentificationService {

    private final SessionAuthentificationRepository sessionRepository;

    public SessionAuthentificationService(
            SessionAuthentificationRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    // Métier : crée une session persistée sans stocker le token en clair.
    @Transactional
    public void creer(String token, String idUtilisateur, LocalDateTime dateExpiration) {
        LocalDateTime maintenant = LocalDateTime.now();
        SessionAuthentification session = new SessionAuthentification(
                sessionRepository.genererId(),
                empreinte(token),
                idUtilisateur,
                dateExpiration,
                maintenant);
        sessionRepository.save(session);
    }

    // Métier : vérifie que la session existe, n'est pas révoquée et n'est pas expirée.
    @Transactional(readOnly = true)
    public boolean estValide(String token) {
        return sessionRepository.trouverValide(
                empreinte(token),
                LocalDateTime.now()).isPresent();
    }

    // Métier : révoque la session courante.
    @Transactional
    public void revoquer(String token) {
        sessionRepository.revoquer(empreinte(token), LocalDateTime.now());
    }

    public String empreinte(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 est indisponible.", exception);
        }
    }
}
