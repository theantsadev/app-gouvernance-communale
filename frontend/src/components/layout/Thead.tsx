import type { Colonne } from "../../types/colonne";

type TheadProps<T> = {
    colonnes: Colonne<T>[];
};

const Thead = <T,>({ colonnes }: TheadProps<T>) => (
    <thead>
        <tr>
            {colonnes.map((colonne) => (
                <th key={colonne.cle} scope="col">{colonne.titre}</th>
            ))}
        </tr>
    </thead>
);

export default Thead;