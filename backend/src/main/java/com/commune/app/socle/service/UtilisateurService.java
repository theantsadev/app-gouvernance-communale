package com.commune.app.socle.service;

import com.commune.app.commun.AuthException;
import com.commune.app.socle.entity.Utilisateur;
import com.commune.app.socle.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    // Métier : ne révèle pas si l'e-mail existe dans la base.
    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(AuthException::identifiantsInvalides);
    }
}
