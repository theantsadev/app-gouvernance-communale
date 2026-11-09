import { Outlet } from "react-router";
import Footer from "./Footer";
import Header from "./Header";
import Sidebar from "./Sidebar";


const Layout = () => {
    return (
        <div>
            <Header utilisateur={{ prenom: "Jean", nom: "Dupont" }} deconnecter={() => {}} />
            <Sidebar />
            {/* Outlet : variable désignée pour le contenu remplacable */}
            <Outlet />
            <Footer />
        </div>
    );
}

export default Layout;


