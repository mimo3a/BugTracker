import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'
import { api } from '../lib/api'
import type { Me, RegisterInput } from '../types/auth'

interface AuthContextValue {
  user: Me | null
  loading: boolean
  login: (username: string, password: string) => Promise<void>
  logout: () => Promise<void>
  register: (input: RegisterInput) => Promise<{ autoLoggedIn: boolean }>
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<Me | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.auth
      .me()
      .then(setUser)
      .catch(() => setUser(null))
      .finally(() => setLoading(false))
  }, [])

  async function login(username: string, password: string) {
    const me = await api.auth.login({ username, password })
    setUser(me)
  }

  async function logout() {
    try {
      await api.auth.logout()
    } finally {
      setUser(null)
    }
  }

  async function register(input: RegisterInput): Promise<{ autoLoggedIn: boolean }> {
    const me = await api.auth.register(input)
    setUser(me)
    return { autoLoggedIn: true }
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, register }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
