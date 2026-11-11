import { useState } from "react";
import type { FormEvent } from "react";
import { useNavigate } from "react-router";
import "../../assets/css/login-style.css";
import { login } from "../../services/authApi";
import type { ApiError } from "../../types/api";

type ChampsConnexion = {
    email: string;
    motDePasse: string;
};

const LoginPage = () => {
    const navigate = useNavigate();
    const [champs, setChamps] = useState<ChampsConnexion>({
        email: "",
        motDePasse: "",
    });
    const [erreur, setErreur] = useState<ApiError | null>(null);
    const [envoiEnCours, setEnvoiEnCours] = useState(false);

    // Affichage : conserve les valeurs saisies par l'utilisateur.
    const modifierChamp = (champ: keyof ChampsConnexion, valeur: string) => {
        setChamps((valeurs) => ({ ...valeurs, [champ]: valeur }));
        setErreur(null);
    };

    // Intégration : appelle l'API, conserve la session et redirige.
    const soumettre = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setErreur(null);
        setEnvoiEnCours(true);

        try {
            const session = await login(champs.email, champs.motDePasse);
            localStorage.setItem("commune.session", JSON.stringify(session));
            navigate("/", { replace: true });
        } catch (cause) {
            setErreur(cause as ApiError);
        } finally {
            setEnvoiEnCours(false);
        }
    };

    return (
        <main className="login-page">
            <section className="login-card" aria-labelledby="login-titre">
                <h1 id="login-titre">Connexion</h1>
                <p className="login-introduction">
                    Accédez à votre espace de gouvernance communale.
                </p>

                {erreur && !erreur.champ && (
                    <p className="login-erreur" role="alert">{erreur.message}</p>
                )}

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
                            aria-invalid={erreur?.champ === "email"}
                        />
                        {erreur?.champ === "email" && (
                            <span className="login-erreur-champ">{erreur.message}</span>
                        )}
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
                            aria-invalid={erreur?.champ === "motDePasse"}
                        />
                        {erreur?.champ === "motDePasse" && (
                            <span className="login-erreur-champ">{erreur.message}</span>
                        )}
                    </div>

                    <button className="login-bouton" type="submit" disabled={envoiEnCours}>
                        {envoiEnCours ? "Connexion..." : "Se connecter"}
                    </button>
                </form>
            </section>
        </main>
    );
};

export default LoginPage;
