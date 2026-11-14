package com.commune.app.socle.service;

import com.commune.app.commun.AuthException;
import com.commune.app.socle.entity.Role;
import com.commune.app.socle.entity.Utilisateur;
import com.commune.app.socle.lib.UtilisateurLib;
import com.commune.app.socle.repository.UtilisateurRepository;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UtilisateurService(
            UtilisateurRepository utilisateurRepository,
            PasswordEncoder passwordEncoder,
            RoleService roleService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    // Métier : ne révèle pas si l'e-mail existe dans la base.
    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(AuthException::identifiantsInvalides);
    }

    // Métier : centralise la recherche d'un utilisateur par son identifiant.
    public Utilisateur findById(String idUtilisateur) {
        return utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new IllegalStateException(
                        "L'utilisateur de la session est introuvable."));
    }

    // Métier : retourne uniquement les informations publiques de l'utilisateur.
    public UtilisateurLib findPublicById(String idUtilisateur) {
        Utilisateur utilisateur = findById(idUtilisateur);
        return UtilisateurLib.fromEntity(utilisateur);
    }

    // Métier : crée le premier administrateur uniquement si aucun n'existe.
    @Transactional
    public void initialiserAdministrateur(String email, String motDePasse) {
        String idRoleAdministrateur = "ADMINISTRATEUR";

        if (utilisateurRepository.existsByRole_IdRole(idRoleAdministrateur)) {
            return;
        }
        if (email.isBlank() || motDePasse.isBlank()) {
            throw new IllegalStateException(
                    "ADMIN_EMAIL et ADMIN_PASSWORD sont obligatoires pour créer le premier administrateur.");
        }

        Role roleAdministrateur = roleService.findByNom(idRoleAdministrateur);

        LocalDateTime maintenant = LocalDateTime.now();
        Utilisateur administrateur = new Utilisateur(
                utilisateurRepository.genererId(),
                email,
                passwordEncoder.encode(motDePasse),
                "Administrateur",
                "Système",
                maintenant,
                maintenant,
                true,
                roleAdministrateur);
        utilisateurRepository.save(administrateur);
    }
}
