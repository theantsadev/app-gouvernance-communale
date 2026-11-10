import type { UtilisateurAffiche } from "../../../types/UtilisateurAffiche";


const obtenirInitiales = ({ prenom, nom }: UtilisateurAffiche) =>
    `${prenom.charAt(0)}${nom.charAt(0)}`.toUpperCase();

const Bulle = ({ utilisateur }: { utilisateur: UtilisateurAffiche }) => (
    <div className="bulle" title={`${utilisateur.prenom} ${utilisateur.nom}`}>
        {obtenirInitiales(utilisateur)}
    </div>
);
export default Bulle;