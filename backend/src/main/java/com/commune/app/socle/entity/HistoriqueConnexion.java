package com.commune.app.socle.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

// Métier : représente exactement la table HISTORIQUE_CONNEXION.
@Entity
@Table(name = "HISTORIQUE_CONNEXION")
public class HistoriqueConnexion {

    @Id
    @Column(name = "id_connexion", length = 20)
    private String idConnexion;

    @Column(name = "email_saisi", nullable = false, length = 150)
    private String emailSaisi;

    @Column(name = "type", nullable = false, length = 15)
    private String type;

    @Column(name = "date_connexion", nullable = false)
    private LocalDateTime dateConnexion;

    @Column(name = "adresse_ip", length = 45)
    private String adresseIp;

    @Column(name = "id_utilisateur", length = 20)
    private String idUtilisateur;

    protected HistoriqueConnexion() {
    }

    public HistoriqueConnexion(
            String idConnexion,
            String emailSaisi,
            String type,
            LocalDateTime dateConnexion,
            String adresseIp,
            String idUtilisateur) {
        this.idConnexion = idConnexion;
        this.emailSaisi = emailSaisi;
        this.type = type;
        this.dateConnexion = dateConnexion;
        this.adresseIp = adresseIp;
        this.idUtilisateur = idUtilisateur;
    }

    public String getIdConnexion() {
        return idConnexion;
    }

    public String getEmailSaisi() {
        return emailSaisi;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getDateConnexion() {
        return dateConnexion;
    }

    public String getAdresseIp() {
        return adresseIp;
    }

    public String getIdUtilisateur() {
        return idUtilisateur;
    }
}
