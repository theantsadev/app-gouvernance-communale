package com.commune.app.socle.dto;

import com.commune.app.commun.AuthException;
import java.util.regex.Pattern;

// Métier : champs reçus par la connexion ; le contrôle unitaire se fait dans valider().
public record LoginRequest(String email, String motDePasse) {

    private static final Pattern FORMAT_EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    // Normalisation seulement : aucun contrôle ici, le null passe
    public LoginRequest {
        email = (email == null) ? null : email.trim();
    }

    public void validerEmail() {
        if (email == null || email.isBlank()) {
            throw AuthException.formulaire("L'e-mail est obligatoire.", "email");
        }
        if (!FORMAT_EMAIL.matcher(email).matches()) {
            throw AuthException.formulaire("Le format de l'e-mail est invalide.", "email");
        }
    }

    public void validerMotDePasse() {
        if (motDePasse == null || motDePasse.isBlank()) {
            throw AuthException.formulaire("Le mot de passe est obligatoire.", "motDePasse");
        }
    }

    public void valider() {
        validerEmail();
        validerMotDePasse();
    }

    // Le mot de passe ne doit jamais apparaître dans un log
    @Override
    public String toString() {
        return "LoginRequest[email=" + email + ", motDePasse=***]";
    }
}