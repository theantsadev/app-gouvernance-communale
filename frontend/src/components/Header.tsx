import logoCommune from "../assets/img/hero.png";
import '../assets/css/header-style.css';

type HeaderProps = {
    utilisateur: { prenom: string; nom: string };
    deconnecter: () => void;
};

const obtenirInitiales = (prenom: string, nom: string) =>
    `${prenom.charAt(0)}${nom.charAt(0)}`.toUpperCase();

const Header = ({ utilisateur, deconnecter }: HeaderProps) => {
    const nomComplet = `${utilisateur.prenom} ${utilisateur.nom}`;

    return (
        <header className="header">
            <img className="header-logo" src={logoCommune} alt="Logo de la commune" />

            <div className="header-utilisateur">
                <div className="header-bulle" title={nomComplet}>
                    {obtenirInitiales(utilisateur.prenom, utilisateur.nom)}
                </div>
                <span className="header-nom">{nomComplet}</span>
                <button type="button" className="header-deconnexion" onClick={deconnecter}>
                    Se déconnecter
                </button>
            </div>
        </header>
    );
}

export default Header;