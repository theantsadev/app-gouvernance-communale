package com.commune.app.socle.service;

import com.commune.app.socle.entity.Utilisateur;
import com.commune.app.socle.repository.UtilisateurRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminInitializer implements CommandLineRunner {

    private static final String ROLE_ADMINISTRATEUR = "ADMINISTRATEUR";

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminInitializer(
            UtilisateurRepository utilisateurRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.email}") String adminEmail,
            @Value("${app.admin.password}") String adminPassword) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    // Métier : crée le premier administrateur uniquement si aucun n'existe.
    @Override
    @Transactional
    public void run(String... args) {
        if (utilisateurRepository.existsByIdRole(ROLE_ADMINISTRATEUR)) {
            return;
        }
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "ADMIN_EMAIL et ADMIN_PASSWORD sont obligatoires pour créer le premier administrateur.");
        }

        LocalDateTime maintenant = LocalDateTime.now();
        Utilisateur administrateur = new Utilisateur(
                utilisateurRepository.genererId(),
                adminEmail,
                passwordEncoder.encode(adminPassword),
                "Administrateur",
                "Système",
                maintenant,
                maintenant,
                true,
                ROLE_ADMINISTRATEUR);
        utilisateurRepository.save(administrateur);
    }
}
