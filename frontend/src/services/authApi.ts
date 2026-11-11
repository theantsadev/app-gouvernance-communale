export type UtilisateurLib = {
    idUtilisateur: string;
    email: string;
    nom: string;
    prenom: string;
};

export type ApiError = {
    code: string;
    message: string;
    champ?: string;
};

const apiUrl = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

// Intégration : appelle l'API de connexion et transforme ses erreurs.
export async function login(
    email: string,
    motDePasse: string,
): Promise<UtilisateurLib> {
    const response = await fetch(`${apiUrl}/api/socle/auth/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, motDePasse }),
    });

    if (response.ok) {
        return response.json() as Promise<UtilisateurLib>;
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
