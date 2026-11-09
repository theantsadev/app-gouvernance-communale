import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import Layout from './components/Layout';
import Content1 from './pages/Content1';



function App() {
  return (
    <Layout children={<Content1 />} />
  );
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
