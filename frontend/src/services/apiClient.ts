import type { ApiError } from "../types/api";
import { obtenirSession, supprimerSession } from "./sessionStorage";

const apiUrl = import.meta.env.VITE_API_URL;

if (!apiUrl) {
    throw new Error("La variable VITE_API_URL est obligatoire.");
}

export async function apiFetch<T>(
    chemin: string,
    options: RequestInit = {},
): Promise<T> {
    const response = await fetch(`${apiUrl}${chemin}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(obtenirSession()?.token
                ? { Authorization: `Bearer ${obtenirSession()?.token}` }
                : {}),
            ...options.headers,
        },
    });

    if (response.status === 401) {
        supprimerSession();
    }

    if (response.ok) {
        if (response.status === 204) {
            return undefined as T;
        }
        return response.json() as Promise<T>;
    }

    const erreur = await response.json().catch(() => null) as ApiError | null;
    if (erreur?.code && erreur.message) {
        throw erreur;
    }
    throw {
        code: "ERREUR_RESEAU",
        message: "La connexion au serveur est impossible.",
    } satisfies ApiError;
}
