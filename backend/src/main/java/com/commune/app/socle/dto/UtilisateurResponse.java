package com.commune.app.socle.dto;

import java.util.Set;

import com.commune.app.socle.entity.Utilisateur;

// Métier : transporte uniquement les informations publiques de l'utilisateur connecté.
public record UtilisateurResponse(
                String idUtilisateur,
                String email,
                String nom,
                String prenom, String idRole, Set<String> permissions) {
        @Override
        public final String toString() {
                return "UtilisateurLib [idUtilisateur=" + idUtilisateur + ", email=" + email
                                + ", nom=" + nom + ", prenom=" + prenom + ", idRole=" + idRole
                                + ", permissions=" + permissions + "]";
        }

        public static UtilisateurResponse fromEntity(Utilisateur utilisateur) {
                return new UtilisateurResponse(
                                utilisateur.getIdUtilisateur(),
                                utilisateur.getEmail(),
                                utilisateur.getNom(),
                                utilisateur.getPrenom(), utilisateur.getRole().getIdRole(),
                                utilisateur.getPermissions());
        }
}
