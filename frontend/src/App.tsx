import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { BugListPage } from './pages/BugListPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/bugs" replace />} />
        <Route path="/bugs" element={<BugListPage />} />
        <Route path="*" element={<Navigate to="/bugs" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
