import type { ReactNode } from "react";

export type Colonne<T> = {
    cle: keyof T & string;               // attribut de l'objet à afficher
    titre: string;                       // texte de l'en-tête
    afficher?: (ligne: T) => ReactNode;  // rendu personnalisé (sinon valeur brute)
};