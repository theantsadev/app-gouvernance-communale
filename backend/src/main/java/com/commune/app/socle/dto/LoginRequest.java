package com.commune.app.socle.dto;

import com.commune.app.commun.AuthException;

// Métier : valide les champs reçus par la connexion.
public class LoginRequest {

    private String email;
    private String motDePasse;

    public LoginRequest() {
    }

    public LoginRequest(String email, String motDePasse) {
        setEmail(email);
        setMotDePasse(motDePasse);
    }

    public void valider() {
        setEmail(email);
        setMotDePasse(motDePasse);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw AuthException.formulaire("L'e-mail est obligatoire.", "email");
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw AuthException.formulaire(
                    "Le format de l'e-mail est invalide.",
                    "email");
        }
        this.email = email.trim();
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        if (motDePasse == null || motDePasse.isBlank()) {
            throw AuthException.formulaire(
                    "Le mot de passe est obligatoire.",
                    "motDePasse");
        }
        this.motDePasse = motDePasse;
    }
}
