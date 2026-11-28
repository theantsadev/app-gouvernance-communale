import type { Colonne } from "../../types/colonne";
import Trow from "./Trow";


type TbodyProps<T> = {
    colonnes: Colonne<T>[];
    lignes: T[];
    obtenirCle: (ligne: T) => string;
    messageVide: string;
};

const Tbody = <T,>({ colonnes, lignes, obtenirCle, messageVide }: TbodyProps<T>) => {
    // Aucune ligne : un message sur toute la largeur
    if (lignes.length === 0) {
        return (
            <tbody>
                <tr>
                    <td colSpan={colonnes.length} className="table-vide">{messageVide}</td>
                </tr>
            </tbody>
        );
    }

    return (
        <tbody>
            {lignes.map((ligne) => (
                <Trow key={obtenirCle(ligne)} colonnes={colonnes} ligne={ligne} />
            ))}
        </tbody>
    );
};

export default Tbody;