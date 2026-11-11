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

export type ChampsConnexion = {
    email: string;
    motDePasse: string;
};
