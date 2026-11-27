package com.commune.app.socle.dto;

public record LoginResponse(
        String token,
        long expiresIn,
        UtilisateurResponse utilisateur) {
}
