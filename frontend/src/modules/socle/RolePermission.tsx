
import Table from "../../components/table/Table";
import type { Colonne } from "../../types/colonne";
import { useEffect, useState } from "react";
import type { PermissionResponse } from "../../types/permission";
import { getPermissionsByRoleId } from "../../services/roleApi";
import { useParams } from "react-router";

const colonnes: Colonne<PermissionResponse>[] = [
    { cle: "action", titre: "Action" },
    { cle: "ressource", titre: "Ressource" },
    { cle: "description", titre: "Description" },
    { cle: "idService", titre: "Service", afficher: (permission) => permission.idService ?? "Tous" },
];

const RolePermission = () => {
    const { idRole } = useParams<{ idRole: string }>();
    const [chargement, setChargement] = useState(true);

    const [rolePermissions, setRolePermissions] = useState<PermissionResponse[]>([]);
    useEffect(() => {
        if (!idRole) return;
        const chargerRolePermissions = async () => {
            setChargement(true);
            try {
                setRolePermissions(await getPermissionsByRoleId(idRole));
            } catch (erreur) {
                console.error("Erreur lors de la récupération des permissions du rôle :" + idRole, erreur);
            } finally {
                setChargement(false);
            }
        };
        void chargerRolePermissions();
    }, [idRole]);


    return (
        <section>
            <h1>Liste des permissions rattachées au role: {idRole}</h1>
            <Table
                colonnes={colonnes}
                lignes={rolePermissions}
                obtenirCle={(permission) => permission.idPermission}
                messageVide={chargement ? "Chargement..." : "Aucune permission."}
            />
        </section>
    );

}

export default RolePermission;

