import type { Colonne } from "../../types/colonne";

type TrowProps<T> = {
    colonnes: Colonne<T>[];
    ligne: T;
};

const Trow = <T,>({ colonnes, ligne }: TrowProps<T>) => (
    <tr>
        {colonnes.map((colonne) => (
            <td key={colonne.cle}>
                {colonne.afficher ? colonne.afficher(ligne) : String(ligne[colonne.cle] ?? "")}
            </td>
        ))}
    </tr>
);

export default Trow;