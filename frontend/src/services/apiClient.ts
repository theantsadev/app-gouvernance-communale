import type { ApiError } from "../types/api";

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
            ...options.headers,
        },
    });

    if (response.ok) {
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
