package com.commune.app.socle.lib;

import java.util.Set;

import com.commune.app.socle.entity.Utilisateur;

// Métier : transporte uniquement les informations publiques de l'utilisateur connecté.
public record UtilisateurLib(
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

        public static UtilisateurLib fromEntity(Utilisateur utilisateur) {
                return new UtilisateurLib(
                                utilisateur.getIdUtilisateur(),
                                utilisateur.getEmail(),
                                utilisateur.getNom(),
                                utilisateur.getPrenom(), utilisateur.getRole().getIdRole(),
                                utilisateur.getPermissions());
        }
}
