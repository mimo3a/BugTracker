import { useState, type FormEvent } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { useAuth } from '../context/AuthContext'
import { ApiError } from '../lib/api'

export function LoginPage() {
  const { user, login, loading } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  if (loading) return null
  if (user) return <Navigate to="/bugs" replace />

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      await login(username, password)
      toast.success('login erfolgreich')
      const redirectTo = (location.state as { from?: string } | null)?.from ?? '/bugs'
      navigate(redirectTo, { replace: true })
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) {
        setError('Falscher Benutzername oder Passwort')
      } else {
        setError(err instanceof Error ? err.message : 'Login fehlgeschlagen')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="min-h-screen bg-bg text-ink flex items-center justify-center px-4">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-sm border border-border rounded p-6 bg-surface"
      >
        <h1 className="font-mono text-2xl font-bold mb-1">login</h1>
        <p className="font-mono text-xs text-ink-soft mb-5">/api/auth/login</p>

        <label htmlFor="username" className="block font-mono text-xs text-ink-soft mb-1">
          username
        </label>
        <input
          id="username"
          type="text"
          autoComplete="username"
          autoFocus
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          className="w-full mb-3 px-3 py-2 bg-bg border border-border rounded font-mono text-sm focus:outline-none focus:border-ink"
          required
        />

        <label htmlFor="password" className="block font-mono text-xs text-ink-soft mb-1">
          password
        </label>
        <input
          id="password"
          type="password"
          autoComplete="current-password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="w-full mb-4 px-3 py-2 bg-bg border border-border rounded font-mono text-sm focus:outline-none focus:border-ink"
          required
        />

        {error && <p className="font-mono text-xs text-red-fg mb-3">{error}</p>}

        <button
          type="submit"
          disabled={submitting || !username || !password}
          className="w-full px-3 py-2 bg-ink text-bg font-mono text-sm rounded hover:opacity-90 disabled:opacity-50"
        >
          {submitting ? '...' : 'login'}
        </button>

        <p className="font-mono text-xs text-ink-soft mt-4 text-center">
          noch kein account?{' '}
          <Link to="/register" className="text-ink hover:underline underline-offset-2">
            register
          </Link>
        </p>
      </form>
    </div>
  )
}
