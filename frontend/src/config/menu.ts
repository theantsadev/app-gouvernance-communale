export type ElementMenu = {
    libelle: string;
    chemin?: string;          // absent = simple groupe qui se déplie
    enfants?: ElementMenu[];  // peut contenir d'autres enfants, sans limite
};

export const menuPrincipal: ElementMenu[] = [
    { libelle: "Voir le contenu 1", chemin: "/" },
    {
        libelle: "Contenus",
        enfants: [
            { libelle: "Voir le contenu 2", chemin: "/content-2" },
            // exemple de 3e niveau : à retirer tant que la route n'existe pas
            { libelle: "Autres", enfants: [{ libelle: "Contenu 3", chemin: "/content-3" }] },
        ],
    },
];

// Vrai si l'élément ou l'un de ses descendants correspond à l'adresse courante
export const contientCheminActif = (element: ElementMenu, pathname: string): boolean =>
    element.chemin === pathname ||
    (element.enfants?.some((enfant) => contientCheminActif(enfant, pathname)) ?? false);