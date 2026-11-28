
import { Link } from "react-router";
import Table from "../../components/table/Table";
import type { Colonne } from "../../types/colonne";
import type { RoleResponse } from "../../types/role";
import { useEffect, useState } from "react";
import { getAllRoles } from "../../services/roleApi";

const colonnes: Colonne<RoleResponse>[] = [
    { cle: "nom", titre: "Rôle", afficher: (role) => <Link to={`/roles/${role.idRole}/permissions`}>{role.nom}</Link> },
    { cle: "description", titre: "Description" },
];
const RoleList = () => {
    const [chargement, setChargement] = useState(true);

    const [roles, setRoles] = useState<RoleResponse[]>([]);
    useEffect(() => {
        const chargerRoles = async () => {
            try {
                setRoles(await getAllRoles());
            } catch (erreur) {
                console.error("Erreur lors de la récupération des rôles :", erreur);
            } finally {
                setChargement(false);
            }
        };
        void chargerRoles();
    }, []);


    return (
        <section>
            <h1>Liste des rôles</h1>
            <Table
                colonnes={colonnes}
                lignes={roles}
                obtenirCle={(role) => role.idRole}
                messageVide={chargement ? "Chargement..." : "Aucun rôle."}
            />
        </section>
    );

}

export default RoleList;