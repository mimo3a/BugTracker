import { Navigate, useLocation } from 'react-router-dom'
import type { ReactNode } from 'react'
import { useAuth } from '../context/AuthContext'

/**
 * Wrapper für Routen, die nur ADMIN-Usern offenstehen (T053a, T053b).
 * Unauthentifiziert → /login. Authentifiziert, aber nicht ADMIN → /bugs.
 */
export function AdminRoute({ children }: { children: ReactNode }) {
  const { user, loading } = useAuth()
  const location = useLocation()

  if (loading) {
    return (
      <div className="min-h-screen bg-bg text-ink flex items-center justify-center">
        <span className="font-mono text-sm text-ink-soft">...</span>
      </div>
    )
  }

  if (!user) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />
  }

  if (user.role !== 'ADMIN') {
    return <Navigate to="/bugs" replace />
  }

  return <>{children}</>
}
