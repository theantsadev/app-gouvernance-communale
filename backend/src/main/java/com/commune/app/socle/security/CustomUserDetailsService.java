package com.commune.app.socle.security;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.commune.app.socle.entity.Utilisateur;
import com.commune.app.socle.service.UtilisateurService;

public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurService utilisateurService;

    public CustomUserDetailsService(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String email) {
        Utilisateur utilisateur = utilisateurService.findByEmail(email);
        return new CustomUserDetails(utilisateur);
    }

    public CustomUserDetails loadUserById(String idUtilisateur) {
        Utilisateur utilisateur = utilisateurService.findById(idUtilisateur);
        return new CustomUserDetails(utilisateur);
    }
    
}
