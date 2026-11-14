package com.commune.app.socle.security;

import java.util.Collection;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.commune.app.socle.entity.Utilisateur;

public class CustomUserDetails implements UserDetails {
    private final Utilisateur utilisateur;

    public CustomUserDetails(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return utilisateur.getRole()
                .getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getIdPermission()))
                .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return utilisateur.getMotDePasseHash();
    }

    @Override
    public String getUsername() {
        return utilisateur.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return utilisateur.isActif();
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

}
