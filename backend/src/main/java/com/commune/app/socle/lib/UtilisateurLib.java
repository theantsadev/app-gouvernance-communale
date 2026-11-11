package com.commune.app.socle.lib;

// Métier : transporte uniquement les informations publiques de l'utilisateur connecté.
public record UtilisateurLib(
        String idUtilisateur,
        String email,
        String nom,
        String prenom) {
}
