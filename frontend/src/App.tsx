import { Route, Routes } from 'react-router'
import Layout from './components/layout/Layout'
import Content1 from './pages/Content1'
import Content2 from './pages/Content2'
import Error from './pages/Error'
import LoginPage from './modules/auth/LoginPage'

export default function App() {
  return (
    <Routes>
      {/* Affichage : la page de connexion reste hors du layout authentifié. */}
      <Route path="connexion" element={<LoginPage />} />

      <Route element={<Layout />}>
        {/* index pour indiquer une route par défaut */}
        <Route index element={<Content1 />} />
        <Route path="content-2" element={<Content2 />} />
      </Route>

      {/* "*" pour indiquer une route inconnue */}
      <Route path="*" element={<Error />} />

    </Routes>
  )
}