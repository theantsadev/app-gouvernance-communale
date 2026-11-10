import type { ReactNode } from "react";
import "../assets/css/page-content-style.css";

type PageContentProps = {
    children: ReactNode;
};

const PageContent = ({ children }: PageContentProps) => (
    <div className="page-content">
        {children}
    </div>
);

export default PageContent;
