import type { UtilisateurResponse } from "../../../types/auth";


const obtenirInitiales = ({ prenom, nom }: UtilisateurResponse) =>
    `${prenom.charAt(0)}${nom.charAt(0)}`.toUpperCase();

const Bulle = ({ utilisateur }: { utilisateur: UtilisateurResponse }) => (
    <div className="bulle" title={`${utilisateur.prenom} ${utilisateur.nom}`}>
        {obtenirInitiales(utilisateur)}
    </div>
);
export default Bulle;