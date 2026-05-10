import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { BugDetailPage } from './pages/BugDetailPage'
import { BugListPage } from './pages/BugListPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/bugs" replace />} />
        <Route path="/bugs" element={<BugListPage />} />
        <Route path="/bugs/:id" element={<BugDetailPage />} />
        <Route path="*" element={<Navigate to="/bugs" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
