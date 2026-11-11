package com.commune.app.commun;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErreurHandler {

    // Intégration : transforme les erreurs métier dans le format de l'API.
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErreurResponse> gererAuthException(AuthException exception) {
        int statut = switch (exception.getCode()) {
            case "FORMULAIRE_INVALIDE" -> 400;
            case "AUTH_COMPTE_DESACTIVE" -> 403;
            default -> 401;
        };
        return ResponseEntity.status(statut)
                .body(new ErreurResponse(
                        exception.getCode(),
                        exception.getMessage(),
                        exception.getChamp()));
    }
}
