import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { Toaster } from 'sonner'
import { AuthProvider } from './context/AuthContext'
import { ProtectedRoute } from './components/ProtectedRoute'
import { AdminRoute } from './components/AdminRoute'
import { LoginPage } from './pages/LoginPage'
import { RegisterPage } from './pages/RegisterPage'
import { BugListPage } from './pages/BugListPage'
import { BugCreatePage } from './pages/BugCreatePage'
import { BugDetailPage } from './pages/BugDetailPage'
import { BugEditPage } from './pages/BugEditPage'
import { AdminUsersPage } from './pages/AdminUsersPage'
import { AdminTagsPage } from './pages/AdminTagsPage'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Toaster
          position="top-right"
          toastOptions={{
            classNames: {
              toast: 'font-mono text-xs',
            },
          }}
        />
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route
            path="/bugs"
            element={
              <ProtectedRoute>
                <BugListPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/bugs/new"
            element={
              <ProtectedRoute>
                <BugCreatePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/bugs/:id"
            element={
              <ProtectedRoute>
                <BugDetailPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/bugs/:id/edit"
            element={
              <ProtectedRoute>
                <BugEditPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/admin/users"
            element={
              <AdminRoute>
                <AdminUsersPage />
              </AdminRoute>
            }
          />
          <Route
            path="/admin/tags"
            element={
              <AdminRoute>
                <AdminTagsPage />
              </AdminRoute>
            }
          />

          <Route path="/" element={<Navigate to="/bugs" replace />} />
          <Route path="*" element={<Navigate to="/bugs" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
