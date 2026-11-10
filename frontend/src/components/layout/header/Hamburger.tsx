type HamburgerProps = { ouvert: boolean; basculer: () => void };

const Hamburger = ({ ouvert, basculer }: HamburgerProps) => (
    <button type="button" className="hamburger" onClick={basculer}
        aria-label={ouvert ? "Fermer le menu" : "Ouvrir le menu"}
        aria-expanded={ouvert} aria-controls="menu-lateral">
        <span /><span /><span />
    </button>
);
export default Hamburger;