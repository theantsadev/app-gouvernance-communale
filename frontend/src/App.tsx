import { Route, Routes } from 'react-router'
import Layout from './components/layout/Layout'
import Content1 from './pages/Content1'
import Content2 from './pages/Content2'
import Error from './pages/Error'

export default function App() {
  return (
    <Routes>

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