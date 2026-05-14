import { useCallback, useEffect, useState } from 'react'
import { api } from '../lib/api'
import type { AdminUser } from '../types/auth'

interface State {
  users: AdminUser[]
  loading: boolean
  error: string | null
}

/**
 * Lädt die volle User-Liste (UserWithoutHash) für die Admin-Tabelle (T053b).
 */
export function useAdminUsers() {
  const [state, setState] = useState<State>({ users: [], loading: true, error: null })
  const [reloadToken, setReloadToken] = useState(0)

  const refresh = useCallback(() => setReloadToken((n) => n + 1), [])

  useEffect(() => {
    let cancelled = false
    setState((s) => ({ ...s, loading: true, error: null }))

    api
      .listAdminUsers()
      .then((users) => {
        if (cancelled) return
        setState({ users, loading: false, error: null })
      })
      .catch((err: unknown) => {
        if (cancelled) return
        setState({
          users: [],
          loading: false,
          error: err instanceof Error ? err.message : 'User konnten nicht geladen werden',
        })
      })

    return () => {
      cancelled = true
    }
  }, [reloadToken])

  return { ...state, refresh }
}
