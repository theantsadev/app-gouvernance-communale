import type { UtilisateurLib } from "../../../types/auth";


const obtenirInitiales = ({ prenom, nom }: UtilisateurLib) =>
    `${prenom.charAt(0)}${nom.charAt(0)}`.toUpperCase();

const Bulle = ({ utilisateur }: { utilisateur: UtilisateurLib }) => (
    <div className="bulle" title={`${utilisateur.prenom} ${utilisateur.nom}`}>
        {obtenirInitiales(utilisateur)}
    </div>
);
export default Bulle;