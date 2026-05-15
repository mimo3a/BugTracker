import { useCallback, useEffect, useState } from 'react'
import { api } from '../lib/api'
import type { Comment } from '../types/bug'

interface CommentsState {
  comments: Comment[]
  loading: boolean
  error: string | null
}

/**
 * Lädt die Kommentar-Liste zu einem Bug (T061 Backend live).
 * Bei Fehler: leere Liste + notierter Error, die UI zeigt eine
 * Fehlermeldung statt einer harten Wand.
 */
export function useComments(bugId: number | null) {
  const [state, setState] = useState<CommentsState>({
    comments: [],
    loading: bugId !== null,
    error: null,
  })
  const [reloadToken, setReloadToken] = useState(0)

  const refresh = useCallback(() => setReloadToken((n) => n + 1), [])

  useEffect(() => {
    if (bugId === null) {
      setState({ comments: [], loading: false, error: null })
      return
    }
    let cancelled = false
    setState((s) => ({ ...s, loading: true, error: null }))

    api
      .listComments(bugId)
      .then((comments) => {
        if (cancelled) return
        setState({ comments, loading: false, error: null })
      })
      .catch((err: unknown) => {
        if (cancelled) return
        setState({
          comments: [],
          loading: false,
          error: err instanceof Error ? err.message : 'Kommentare konnten nicht geladen werden',
        })
      })

    return () => {
      cancelled = true
    }
  }, [bugId, reloadToken])

  return { ...state, refresh }
}
