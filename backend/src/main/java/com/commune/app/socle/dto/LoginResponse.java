package com.commune.app.socle.dto;

import com.commune.app.socle.lib.UtilisateurLib;

public record LoginResponse(
        String token,
        long expiresIn,
        UtilisateurLib utilisateur) {
}
