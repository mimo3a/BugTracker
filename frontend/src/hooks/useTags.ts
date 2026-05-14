import { useCallback, useEffect, useState } from 'react'
import { api } from '../lib/api'
import { MOCK_TAGS } from '../lib/mockData'
import type { Tag } from '../types/bug'

interface State {
  tags: Tag[]
  loading: boolean
  error: string | null
  usingMock: boolean
}

/**
 * Lädt alle Tags via GET /api/tags. Bei API-Fehler werden MOCK_TAGS gezeigt,
 * damit Admin-/Filter-UI auch ohne Backend testbar ist.
 */
export function useTags() {
  const [state, setState] = useState<State>({
    tags: [],
    loading: true,
    error: null,
    usingMock: false,
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
        setState({ tags, loading: false, error: null, usingMock: false })
      })
      .catch((err: unknown) => {
        if (cancelled) return
        setState({
          tags: MOCK_TAGS,
          loading: false,
          error: err instanceof Error ? err.message : null,
          usingMock: true,
        })
      })

    return () => {
      cancelled = true
    }
  }, [reloadToken])

  return { ...state, refresh }
}
