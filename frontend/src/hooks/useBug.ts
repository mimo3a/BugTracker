import { useCallback, useEffect, useState } from 'react'
import { ApiError, api } from '../lib/api'
import type { Bug } from '../types/bug'

interface BugState {
  bug: Bug | null
  loading: boolean
  notFound: boolean
  error: string | null
}

export function useBug(id: number | null) {
  const [state, setState] = useState<BugState>({
    bug: null,
    loading: id !== null,
    notFound: false,
    error: null,
  })
  const [reloadToken, setReloadToken] = useState(0)

  const refresh = useCallback(() => setReloadToken((n) => n + 1), [])
  const setBug = useCallback(
    (bug: Bug) => setState({ bug, loading: false, notFound: false, error: null }),
    [],
  )

  useEffect(() => {
    if (id === null) {
      setState({ bug: null, loading: false, notFound: false, error: null })
      return
    }
    let cancelled = false
    setState((s) => ({ ...s, loading: true, error: null, notFound: false }))

    api
      .getBug(id)
      .then((bug) => {
        if (cancelled) return
        setState({ bug, loading: false, notFound: false, error: null })
      })
      .catch((err: unknown) => {
        if (cancelled) return
        if (err instanceof ApiError && err.status === 404) {
          setState({ bug: null, loading: false, notFound: true, error: null })
          return
        }
        setState({
          bug: null,
          loading: false,
          notFound: false,
          error: err instanceof Error ? err.message : 'Bug konnte nicht geladen werden',
        })
      })

    return () => {
      cancelled = true
    }
  }, [id, reloadToken])

  return { ...state, refresh, setBug }
}
