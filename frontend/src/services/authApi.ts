import { apiFetch } from "./apiClient";

export type UtilisateurLib = {
    idUtilisateur: string;
    email: string;
    nom: string;
    prenom: string;
};

export type LoginResponse = {
    token: string;
    expiresIn: number;
    utilisateur: UtilisateurLib;
};

// Intégration : appelle l'API de connexion avec le client HTTP partagé.
export async function login(
    email: string,
    motDePasse: string,
): Promise<LoginResponse> {
    return apiFetch<LoginResponse>("/api/socle/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, motDePasse }),
    });
}
