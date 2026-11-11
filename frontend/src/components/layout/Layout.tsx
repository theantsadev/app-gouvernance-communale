import { useState } from "react";
import { Outlet, useNavigate } from "react-router";
import "../../assets/css/layout-style.css";
import Footer from "./Footer";
import Header from "./header/Header";
import PageContent from "./PageContent";
import Sidebar from "./sidebar/Sidebar";

const Layout = () => {
    const navigate = useNavigate();
    const [menuOuvert, setMenuOuvert] = useState(false);
    const basculerMenu = () => setMenuOuvert((valeur) => !valeur);
    const fermerMenu = () => setMenuOuvert(false);

    const deconnecter = () => {
        localStorage.removeItem("commune.session");
        navigate("/connexion", { replace: true });
    };

    return (
        <div className="layout">
            <Header
                utilisateur={{ prenom: "Test", nom: "Utilisateur", email: "test@exemple.com", idUtilisateur: "1" }}
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