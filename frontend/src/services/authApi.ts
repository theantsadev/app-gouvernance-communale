import { apiFetch } from "./apiClient";
import type { LoginResponse } from "../types/auth";

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
