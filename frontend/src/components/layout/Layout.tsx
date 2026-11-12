import { useEffect, useState } from "react";
import { Outlet, useNavigate } from "react-router";
import "../../assets/css/layout-style.css";
import Footer from "./Footer";
import Header from "./header/Header";
import PageContent from "./PageContent";
import Sidebar from "./sidebar/Sidebar";
import { logout, sessionValide, utilisateurConnecte } from "../../services/authApi";
import { obtenirSession } from "../../services/sessionStorage";
import type { UtilisateurLib } from "../../types/auth";

const Layout = () => {
    const navigate = useNavigate();
    const [menuOuvert, setMenuOuvert] = useState(false);
    const [utilisateur, setUtilisateur] = useState<UtilisateurLib | null>(null);
    const [verificationEnCours, setVerificationEnCours] = useState(true);
    const basculerMenu = () => setMenuOuvert((valeur) => !valeur);
    const fermerMenu = () => setMenuOuvert(false);

    useEffect(() => {
        let composantActif = true;

        const verifierSession = async () => {
            if (!obtenirSession()) {
                navigate("/connexion", { replace: true });
                return;
            }

            try {
                const validation = await sessionValide();
                if (!validation.valide) {
                    throw new Error("La session n'est plus valide.");
                }
                const utilisateurCourant = await utilisateurConnecte();
                if (composantActif) {
                    setUtilisateur(utilisateurCourant);
                    setVerificationEnCours(false);
                }
            } catch {
                if (composantActif) {
                    navigate("/connexion", { replace: true });
                }
            }
        };

        void verifierSession();
        return () => {
            composantActif = false;
        };
    }, [navigate]);

    const deconnecter = async () => {
        try {
            await logout();
        } finally {
            navigate("/connexion", { replace: true });
        }
    };

    if (verificationEnCours || !utilisateur) {
        return null;
    }

    return (
        <div className="layout">
            <Header
                utilisateur={utilisateur}
                deconnecter={deconnecter}
                menuOuvert={menuOuvert}
                basculerMenu={basculerMenu}
            />
            <div className="layout-corps">
                <Sidebar ouvert={menuOuvert} fermer={fermerMenu} />
                <main className="layout-contenu">
                    <PageContent>
                        <Outlet />
                    </PageContent>
                </main>
            </div>
            <Footer />
        </div>
    );
};

export default Layout;