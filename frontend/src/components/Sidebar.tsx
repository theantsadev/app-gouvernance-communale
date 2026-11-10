
import "../assets/css/sidebar-style.css";
import { menuPrincipal } from "../config/menu";
import SidebarItem from "./SidebarItem";

type SidebarProps = {
    ouvert: boolean;
    fermer: () => void;
};

const Sidebar = ({ ouvert, fermer }: SidebarProps) => {
    const fermerSurNavigationMobile = () => {
        if (window.matchMedia("(max-width: 767px)").matches) {
            fermer();
        }
    };

    return (
        <>
            <div className={`sidebar-fond${ouvert ? " visible" : ""}`} onClick={fermer} aria-hidden="true" />
            <aside id="menu-lateral" className={`sidebar${ouvert ? " ouvert" : ""}`} aria-label="Menu principal">
                <div className="sidebar-entete">
                    <h2>Menu</h2>
                    <button type="button" className="sidebar-fermer" onClick={fermer} aria-label="Fermer le menu">×</button>
                </div>
                <nav>
                    <ul className="sidebar-liste">
                        {menuPrincipal.map((element) => (
                            <SidebarItem key={element.chemin ?? element.libelle} element={element}
                                niveau={0} fermerMenu={fermerSurNavigationMobile} />
                        ))}
                    </ul>
                </nav>
            </aside>
        </>
    );
};

export default Sidebar;