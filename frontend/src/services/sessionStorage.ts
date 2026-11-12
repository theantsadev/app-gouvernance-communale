import type { LoginResponse } from "../types/auth";

export const CLE_SESSION = "commune.session";

export function obtenirSession(): LoginResponse | null {
    const session = localStorage.getItem(CLE_SESSION);
    if (!session) {
        return null;
    }

    try {
        return JSON.parse(session) as LoginResponse;
    } catch {
        localStorage.removeItem(CLE_SESSION);
        return null;
    }
}

export function supprimerSession(): void {
    localStorage.removeItem(CLE_SESSION);
}
