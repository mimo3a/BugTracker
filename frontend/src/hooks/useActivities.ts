import { useCallback, useEffect, useState } from 'react'
import { api } from '../lib/api'
import type { Activity } from '../types/bug'

interface ActivitiesState {
  activities: Activity[]
  loading: boolean
  error: string | null
}

/**
 * Lädt die Activity-Historie eines Bugs (T059).
 * Backend-Endpoint: GET /api/bugs/{bugId}/activities.
 */
export function useActivities(bugId: number | null) {
  const [state, setState] = useState<ActivitiesState>({
    activities: [],
    loading: bugId !== null,
    error: null,
  })
  const [reloadToken, setReloadToken] = useState(0)

  const refresh = useCallback(() => setReloadToken((n) => n + 1), [])

  useEffect(() => {
    if (bugId === null) {
      setState({ activities: [], loading: false, error: null })
      return
    }
    let cancelled = false
    setState((s) => ({ ...s, loading: true, error: null }))

    api
      .listActivities(bugId)
      .then((activities) => {
        if (cancelled) return
        setState({ activities, loading: false, error: null })
      })
      .catch((err: unknown) => {
        if (cancelled) return
        setState({
          activities: [],
          loading: false,
          error: err instanceof Error ? err.message : 'Historie konnte nicht geladen werden',
        })
      })

    return () => {
      cancelled = true
    }
  }, [bugId, reloadToken])

  return { ...state, refresh }
}
