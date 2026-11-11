package com.commune.app.commun;

// Métier : transporte le code et le champ d'une erreur d'authentification.
public class AuthException extends RuntimeException {

    private final String code;
    private final String champ;

    public AuthException(String code, String message) {
        this(code, message, null);
    }

    public AuthException(String code, String message, String champ) {
        super(message);
        this.code = code;
        this.champ = champ;
    }

    public static AuthException formulaire(String message, String champ) {
        return new AuthException("FORMULAIRE_INVALIDE", message, champ);
    }

    public static AuthException identifiantsInvalides() {
        return new AuthException(
                "AUTH_IDENTIFIANTS_INVALIDES",
                "E-mail ou mot de passe incorrect.");
    }

    public String getCode() {
        return code;
    }

    public String getChamp() {
        return champ;
    }
}
