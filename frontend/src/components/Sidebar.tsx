import { NavLink } from "react-router";
import '../assets/css/sidebar-style.css';

// NavLink : composant pour créer un lien de navigation et ajoute automatiquement la classe "active" lorsque le lien correspond à l'URL actuelle
const Sidebar = () => {
    return (
        <aside>
            <h2>Menu</h2>
            <ul>
                <li><NavLink to="/">Voir le contenu 1</NavLink></li>
                <li><NavLink to="/content-2">Voir le contenu 2</NavLink></li>
            </ul>
        </aside>
    );
}

export default Sidebar;