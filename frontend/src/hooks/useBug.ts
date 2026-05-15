import { useEffect, useState } from 'react'
import { api } from '../lib/api'
import { EMPTY_FILTERS, type Bug } from '../types/bug'

interface BugState {
  bug: Bug | null
  loading: boolean
  notFound: boolean
  error: string | null
}

/**
 * Lädt einen einzelnen Bug per ID.
 *
 * Workaround solange T033 (GET /api/bugs/{id}) noch nicht implementiert
 * ist: holt die Liste (auch archivierte) und sucht clientseitig. Wenn
 * T033 da ist, ersetzen durch `api.getBug(id)` — sonst alles wie es ist.
 */
export function useBug(id: number | null): BugState {
  const [state, setState] = useState<BugState>({
    bug: null,
    loading: id !== null,
    notFound: false,
    error: null,
  })

  useEffect(() => {
    if (id === null) {
      setState({ bug: null, loading: false, notFound: false, error: null })
      return
    }
    let cancelled = false
    setState((s) => ({ ...s, loading: true, error: null, notFound: false }))

    // Auch archivierte holen — Edit-Page muss zumindest sagen können
    // "ist archiviert" statt 404.
    api
      .listBugs({ ...EMPTY_FILTERS, archived: true })
      .then((res) => {
        if (cancelled) return
        const found = res.bugs.find((b) => b.id === id) ?? null
        setState({
          bug: found,
          loading: false,
          notFound: found === null,
          error: null,
        })
      })
      .catch((err) => {
        if (cancelled) return
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
  }, [id])

  return state
}
