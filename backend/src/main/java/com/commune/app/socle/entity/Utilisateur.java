package com.commune.app.socle.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

// Métier : représente exactement la table UTILISATEUR avec les noms camelCase.
@Entity
@Table(name = "UTILISATEUR")
public class Utilisateur {

    @Id
    @Column(name = "id_utilisateur", length = 20)
    private String idUtilisateur;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "mot_de_passe_hash", nullable = false, length = 255)
    private String motDePasseHash;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "telephone", length = 30)
    private String telephone;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    @Column(name = "actif", nullable = false)
    private boolean actif;

    @Column(name = "id_service", length = 20)
    private String idService;

    @Column(name = "id_role", nullable = false, length = 20)
    private String idRole;

    protected Utilisateur() {
    }

    public Utilisateur(
            String idUtilisateur,
            String email,
            String motDePasseHash,
            String nom,
            String prenom,
            LocalDateTime dateCreation,
            LocalDateTime dateModification,
            boolean actif,
            String idRole) {
        this.idUtilisateur = idUtilisateur;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
        this.nom = nom;
        this.prenom = prenom;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.actif = actif;
        this.idRole = idRole;
    }

    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    public String getEmail() {
        return email;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public boolean isActif() {
        return actif;
    }

    public String getIdService() {
        return idService;
    }

    public String getIdRole() {
        return idRole;
    }
}
