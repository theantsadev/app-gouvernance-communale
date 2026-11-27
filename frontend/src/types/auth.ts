export type UtilisateurResponse = {
    idUtilisateur: string;
    email: string;
    nom: string;
    prenom: string;
};

export type LoginResponse = {
    token: string;
    expiresIn: number;
    utilisateur: UtilisateurResponse;
};

export type LoginRequest = {
    email: string;
    motDePasse: string;
};
