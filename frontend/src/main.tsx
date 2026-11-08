import { StrictMode} from 'react'
import { createRoot } from 'react-dom/client'



function App() {
  return (
    <div>
      <h1>Hello, React!</h1>
    </div>
  );
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
