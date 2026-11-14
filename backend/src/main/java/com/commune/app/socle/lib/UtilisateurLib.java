package com.commune.app.socle.lib;

import com.commune.app.socle.entity.Utilisateur;

// Métier : transporte uniquement les informations publiques de l'utilisateur connecté.
public record UtilisateurLib(
                String idUtilisateur,
                String email,
                String nom,
                String prenom) {
        public static UtilisateurLib fromEntity(Utilisateur utilisateur) {
                return new UtilisateurLib(
                                utilisateur.getIdUtilisateur(),
                                utilisateur.getEmail(),
                                utilisateur.getNom(),
                                utilisateur.getPrenom());
        }
}
