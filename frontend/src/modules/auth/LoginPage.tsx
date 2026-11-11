import { useState } from "react";
import type { FormEvent } from "react";
import "../../assets/css/login-style.css";

type ChampsConnexion = {
    email: string;
    motDePasse: string;
};

const LoginPage = () => {
    const [champs, setChamps] = useState<ChampsConnexion>({
        email: "",
        motDePasse: "",
    });

    // Affichage : conserve les valeurs saisies par l'utilisateur.
    const modifierChamp = (champ: keyof ChampsConnexion, valeur: string) => {
        setChamps((valeurs) => ({ ...valeurs, [champ]: valeur }));
    };

    // Affichage : prépare le formulaire pour le branchement de LOGIN006.
    const soumettre = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
    };

    return (
        <main className="login-page">
            <section className="login-card" aria-labelledby="login-titre">
                <h1 id="login-titre">Connexion</h1>
                <p className="login-introduction">
                    Accédez à votre espace de gouvernance communale.
                </p>

                <form onSubmit={soumettre}>
                    <div className="login-champ">
                        <label htmlFor="email">E-mail</label>
                        <input
                            id="email"
                            name="email"
                            type="email"
                            autoComplete="username"
                            required
                            value={champs.email}
                            onChange={(event) => modifierChamp("email", event.target.value)}
                        />
                    </div>

                    <div className="login-champ">
                        <label htmlFor="motDePasse">Mot de passe</label>
                        <input
                            id="motDePasse"
                            name="motDePasse"
                            type="password"
                            autoComplete="current-password"
                            required
                            value={champs.motDePasse}
                            onChange={(event) => modifierChamp("motDePasse", event.target.value)}
                        />
                    </div>

                    <button className="login-bouton" type="submit">
                        Se connecter
                    </button>
                </form>
            </section>
        </main>
    );
};

export default LoginPage;
