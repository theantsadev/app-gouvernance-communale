import { Link } from "react-router";

const Sidebar = () => {
    return (
        <aside>
            <h2>Menu</h2>
            <ul>
                <li><Link to="/">Voir le contenu 1</Link></li>
                <li><Link to="/content-2">Voir le contenu 2</Link></li>
            </ul>
        </aside>
    );
}

export default Sidebar;