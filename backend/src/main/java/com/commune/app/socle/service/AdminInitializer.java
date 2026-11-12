package com.commune.app.socle.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UtilisateurService utilisateurService;
    private final String adminEmail;
    private final String adminPassword;

    public AdminInitializer(
            UtilisateurService utilisateurService,
            @Value("${app.admin.email}") String adminEmail,
            @Value("${app.admin.password}") String adminPassword) {
        this.utilisateurService = utilisateurService;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    // Intégration : déclenche l'initialisation de l'administrateur au démarrage.
    @Override
    public void run(String... args) {
        utilisateurService.initialiserAdministrateur(adminEmail, adminPassword);
    }
}
