import { useCallback, useEffect, useState } from 'react'
import { api } from '../lib/api'
import type { Tag } from '../types/bug'

interface State {
  tags: Tag[]
  loading: boolean
  error: string | null
}

/**
 * Lädt alle Tags via GET /api/tags (T038a, live). Bei Fehler wird der
 * Error durchgereicht — kein Mock-Fallback mehr.
 */
export function useTags() {
  const [state, setState] = useState<State>({
    tags: [],
    loading: true,
    error: null,
  })
  const [reloadToken, setReloadToken] = useState(0)

  const refresh = useCallback(() => setReloadToken((n) => n + 1), [])

  useEffect(() => {
    let cancelled = false
    setState((s) => ({ ...s, loading: true, error: null }))

    api
      .listTags()
      .then((tags) => {
        if (cancelled) return
        setState({ tags, loading: false, error: null })
      })
      .catch((err: unknown) => {
        if (cancelled) return
        setState({
          tags: [],
          loading: false,
          error: err instanceof Error ? err.message : 'Tags konnten nicht geladen werden',
        })
      })

    return () => {
      cancelled = true
    }
  }, [reloadToken])

  return { ...state, refresh }
}
