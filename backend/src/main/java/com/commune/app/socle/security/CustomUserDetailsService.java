package com.commune.app.socle.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.commune.app.socle.entity.Utilisateur;
import com.commune.app.socle.service.UtilisateurService;

import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurService utilisateurService;

    public CustomUserDetailsService(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @Override @Transactional(readOnly = true)
    public CustomUserDetails loadUserByUsername(String email) {
        Utilisateur utilisateur = utilisateurService.findByEmail(email);
        return new CustomUserDetails(utilisateur);
    }
    
    @Transactional(readOnly = true)
    public CustomUserDetails loadUserById(String idUtilisateur) {
        Utilisateur utilisateur = utilisateurService.findById(idUtilisateur);
        return new CustomUserDetails(utilisateur);
    }
    
}
