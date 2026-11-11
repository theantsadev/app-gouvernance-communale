import logoCommune from "../../../assets/img/hero.png";
import "../../../assets/css/header-style.css";
import Bulle from "./Bulle";
import Hamburger from "./Hamburger";
import type { UtilisateurLib } from "../../../types/auth";

type HeaderProps = {
    utilisateur: UtilisateurLib;
    deconnecter: () => void;
    menuOuvert: boolean;
    basculerMenu: () => void;
};


const Header = ({ utilisateur, deconnecter, menuOuvert, basculerMenu }: HeaderProps) => (
    <header className="header">
        <div className="header-gauche">
            <Hamburger ouvert={menuOuvert} basculer={basculerMenu} />
            <img className="header-logo" src={logoCommune} alt="Logo de la commune" />
        </div>
        <div className="header-utilisateur">
            <Bulle utilisateur={utilisateur} />
            <span className="header-nom">{utilisateur.prenom} {utilisateur.nom}</span>
            <button type="button" className="header-deconnexion" onClick={deconnecter}>
                Se déconnecter
            </button>
        </div>
    </header>
);
export default Header;