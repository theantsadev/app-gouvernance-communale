package com.commune.app.socle.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

// Métier : représente exactement la table SESSION_AUTHENTIFICATION.
@Entity
@Table(name = "SESSION_AUTHENTIFICATION")
public class SessionAuthentification {

    @Id
    @Column(name = "id_session", length = 20)
    private String idSession;

    @Column(name = "empreinte_token", nullable = false, unique = true, length = 128)
    private String empreinteToken;

    @Column(name = "id_utilisateur", nullable = false, length = 20)
    private String idUtilisateur;

    @Column(name = "date_expiration", nullable = false)
    private LocalDateTime dateExpiration;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_revoquee")
    private LocalDateTime dateRevoquee;

    protected SessionAuthentification() {
    }

    public SessionAuthentification(
            String idSession,
            String empreinteToken,
            String idUtilisateur,
            LocalDateTime dateExpiration,
            LocalDateTime dateCreation) {
        this.idSession = idSession;
        this.empreinteToken = empreinteToken;
        this.idUtilisateur = idUtilisateur;
        this.dateExpiration = dateExpiration;
        this.dateCreation = dateCreation;
    }

    public String getIdSession() {
        return idSession;
    }

    public String getEmpreinteToken() {
        return empreinteToken;
    }

    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public LocalDateTime getDateRevoquee() {
        return dateRevoquee;
    }

    public void revoquer(LocalDateTime date) {
        this.dateRevoquee = date;
    }
}
