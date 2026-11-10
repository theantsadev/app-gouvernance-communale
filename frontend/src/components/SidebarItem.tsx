import { useId, useState } from "react";
import type { CSSProperties } from "react";
import { NavLink, useLocation } from "react-router";
import { contientCheminActif } from "../config/menu";
import type { ElementMenu } from "../config/menu";

type SidebarItemProps = {
    element: ElementMenu;
    niveau: number;
    fermerMenu: () => void;
};

const SidebarItem = ({ element, niveau, fermerMenu }: SidebarItemProps) => {
    const { pathname } = useLocation();
    const idSousListe = useId();
    const aEnfants = (element.enfants?.length ?? 0) > 0;

    // Déplié au départ si une page enfant est affichée
    const [ouvert, setOuvert] = useState(() => aEnfants && contientCheminActif(element, pathname));
    const basculer = () => setOuvert((valeur) => !valeur);

    return (
        <li style={{ "--niveau": niveau } as CSSProperties}>
            <div className="sidebar-ligne">
                {element.chemin ? (
                    <NavLink to={element.chemin} end className="sidebar-lien" onClick={fermerMenu}>
                        {element.libelle}
                    </NavLink>
                ) : (
                    <button type="button" className="sidebar-lien" onClick={basculer}
                        aria-expanded={ouvert} aria-controls={idSousListe}>
                        {element.libelle}
                    </button>
                )}
                {aEnfants && (
                    <button type="button" className={`sidebar-chevron${ouvert ? " ouvert" : ""}`}
                        onClick={basculer} aria-expanded={ouvert} aria-controls={idSousListe}
                        aria-label={`${ouvert ? "Replier" : "Déplier"} ${element.libelle}`}>
                        ›
                    </button>
                )}
            </div>
            {aEnfants && ouvert && (
                <ul id={idSousListe} className="sidebar-liste">
                    {element.enfants?.map((enfant) => (
                        <SidebarItem key={enfant.chemin ?? enfant.libelle} element={enfant}
                            niveau={niveau + 1} fermerMenu={fermerMenu} />
                    ))}
                </ul>
            )}
        </li>
    );
};

export default SidebarItem;