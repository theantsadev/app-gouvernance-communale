import { apiFetch } from "./apiClient";
import type { LoginResponse } from "../types/auth";
import { supprimerSession } from "./sessionStorage";
import type { UtilisateurLib } from "../types/auth";

export type SessionValidResponse = {
    valide: boolean;
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

// Intégration : révoque la session courante puis la supprime du navigateur.
export async function logout(): Promise<void> {
    try {
        await apiFetch<void>("/api/socle/auth/logout", { method: "POST" });
    } finally {
        supprimerSession();
    }
}

// Intégration : vérifie la session courante auprès du backend.
export async function sessionValide(): Promise<SessionValidResponse> {
    return apiFetch<SessionValidResponse>("/api/socle/auth/session/valid", {
        method: "POST",
    });
}

// Intégration : récupère l'utilisateur associé à la session courante.
export async function utilisateurConnecte(): Promise<UtilisateurLib> {
    return apiFetch<UtilisateurLib>("/api/socle/utilisateur/current");
}
