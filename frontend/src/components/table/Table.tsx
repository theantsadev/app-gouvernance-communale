import "../../assets/css/table-style.css";
import type { Colonne } from "../../types/colonne";
import Tbody from "./Tbody";
import Thead from "./Thead";

type TableProps<T> = {
    colonnes: Colonne<T>[];
    lignes: T[];
    obtenirCle: (ligne: T) => string;
    messageVide?: string;
};

const Table = <T,>({
    colonnes,
    lignes,
    obtenirCle,
    messageVide = "Aucune donnée à afficher.",
}: TableProps<T>) => (
    <div className="table-conteneur">
        <table className="table">
            <Thead colonnes={colonnes} />
            <Tbody colonnes={colonnes} lignes={lignes} obtenirCle={obtenirCle} messageVide={messageVide} />
        </table>
    </div>
);

export default Table;