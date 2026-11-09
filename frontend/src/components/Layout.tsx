import Footer from "./Footer";
import Header from "./Header";
import Sidebar from "./Sidebar";
import type { ReactNode } from "react";

const Layout = ({ children }: { children: ReactNode }) => {
    return (
        <div>
            <Header />
            <Sidebar />
            {children}
            <Footer />
        </div>
    );
}

export default Layout;