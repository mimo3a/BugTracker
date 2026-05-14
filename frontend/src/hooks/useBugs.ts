import { useEffect, useState } from 'react'
import { api } from '../lib/api'
import { filterBugs } from '../lib/filterBugs'
import { MOCK_BUGS, MOCK_TAGS } from '../lib/mockData'
import { type Bug, type BugFilters, type Tag, type User } from '../types/bug'

interface BugsState {
  bugs: Bug[]
  total: number
  page: number
  pageSize: number
  loading: boolean
  error: string | null
  /** True wenn auf Mock-Daten zurückgefallen (Backend nicht erreichbar). */
  usingMock: boolean
}

const INITIAL: BugsState = {
  bugs: [],
  total: 0,
  page: 1,
  pageSize: 20,
  loading: true,
  error: null,
  usingMock: false,
}

/**
 * Fetches bugs matching `filters` from the backend, falling back to mock
 * data so the filter UI is verifiable even without a running Spring app.
 */
export function useBugs(filters: BugFilters, page = 1): BugsState {
  const [state, setState] = useState<BugsState>(INITIAL)
  const [prevKey, setPrevKey] = useState<string | null>(null)
  const key = JSON.stringify({ filters, page })

  if (prevKey !== key) {
    setPrevKey(key)
    if (!state.loading) setState((s) => ({ ...s, loading: true, error: null }))
  }

  useEffect(() => {
    let cancelled = false
    api
      .listBugs(filters, page)
      .then((res) => {
        if (cancelled) return
        setState({
          bugs: res.bugs,
          total: res.total,
          page: res.page,
          pageSize: res.pageSize,
          loading: false,
          error: null,
          usingMock: false,
        })
      })
      .catch(() => {
        if (cancelled) return
        const filtered = filterBugs(MOCK_BUGS, filters)
        const pageSize = 20
        const start = (page - 1) * pageSize
        const slice = filtered.slice(start, start + pageSize)
        setState({
          bugs: slice,
          total: filtered.length,
          page,
          pageSize,
          loading: false,
          error: null,
          usingMock: true,
        })
      })

    return () => {
      cancelled = true
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [key])

  return state
}

interface OptionsState {
  users: User[]
  tags: Tag[]
  usingMockTags: boolean
}

/**
 * Builds the assignee + tag dropdowns. Users come from `/api/users`
 * (jeder eingeloggte User, inkl. unzugewiesener Tester); tags from
 * `/api/tags` mit Mock-Fallback wenn der Tag-Endpoint noch fehlt.
 */
export function useFilterOptions(): OptionsState {
  const [state, setState] = useState<OptionsState>({
    users: [],
    tags: [],
    usingMockTags: false,
  })

  useEffect(() => {
    let cancelled = false
    Promise.all([
      api.listUsers().catch(() => [] as User[]),
      api
        .listTags()
        .then((tags) => ({ tags, usingMock: false }))
        .catch(() => ({ tags: MOCK_TAGS, usingMock: true })),
    ]).then(([users, tagResult]) => {
      if (cancelled) return
      const sortedUsers = [...users].sort((a, b) => a.username.localeCompare(b.username))
      setState({ users: sortedUsers, tags: tagResult.tags, usingMockTags: tagResult.usingMock })
    })
    return () => {
      cancelled = true
    }
  }, [])

  return state
}
