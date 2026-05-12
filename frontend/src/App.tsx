import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { ProtectedRoute } from './components/ProtectedRoute'
import { LoginPage } from './pages/LoginPage'
import { BugListPage } from './pages/BugListPage'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/bugs"
            element={
              <ProtectedRoute>
                <BugListPage />
              </ProtectedRoute>
            }
          />
          <Route path="/" element={<Navigate to="/bugs" replace />} />
          <Route path="*" element={<Navigate to="/bugs" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
