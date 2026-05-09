import { useEffect, useState } from 'react'
import { api } from '../lib/api'
import { filterBugs } from '../lib/filterBugs'
import { MOCK_BUGS, MOCK_TAGS } from '../lib/mockData'
import { EMPTY_FILTERS, type Bug, type BugFilters, type Tag, type User } from '../types/bug'

interface BugsState {
  bugs: Bug[]
  total: number
  loading: boolean
  error: string | null
  /** True when we fell back to mock data (backend unreachable). */
  usingMock: boolean
}

const INITIAL: BugsState = {
  bugs: [],
  total: 0,
  loading: true,
  error: null,
  usingMock: false,
}

/**
 * Fetches bugs matching `filters` from the backend, falling back to mock
 * data so the filter UI is verifiable even without a running Spring app.
 *
 * Loading state is flipped via a render-phase compare-and-set against the
 * latest filter snapshot, avoiding `setState` inside the effect body.
 */
export function useBugs(filters: BugFilters): BugsState {
  const [state, setState] = useState<BugsState>(INITIAL)
  const [prevFilters, setPrevFilters] = useState<BugFilters | null>(null)

  if (prevFilters !== filters) {
    setPrevFilters(filters)
    if (!state.loading) setState((s) => ({ ...s, loading: true, error: null }))
  }

  useEffect(() => {
    let cancelled = false
    api
      .listBugs(filters)
      .then((res) => {
        if (cancelled) return
        setState({
          bugs: res.bugs,
          total: res.total,
          loading: false,
          error: null,
          usingMock: false,
        })
      })
      .catch(() => {
        if (cancelled) return
        const filtered = filterBugs(MOCK_BUGS, filters)
        setState({
          bugs: filtered,
          total: filtered.length,
          loading: false,
          error: null,
          usingMock: true,
        })
      })

    return () => {
      cancelled = true
    }
  }, [filters])

  return state
}

interface OptionsState {
  users: User[]
  tags: Tag[]
}

/**
 * Builds the assignee + tag dropdowns for the filter bar.
 *
 * Why not `GET /api/users`? Per openapi.yaml that endpoint is ADMIN-only
 * (FA-15) and returns 403 for TESTER/DEVELOPER, so we cannot use it to
 * populate the assignee dropdown for ordinary users. Instead we derive the
 * assignee list from an unfiltered bug fetch on mount: every assignee that
 * has at least one open bug shows up.
 *
 * Caveat: a user who has never been assigned a bug is not selectable as a
 * filter target. Acceptable for the MVP filter UI; a dedicated lightweight
 * endpoint (e.g. /api/users/assignable, all roles) is the long-term fix.
 */
export function useFilterOptions(): OptionsState {
  const [state, setState] = useState<OptionsState>({ users: [], tags: [] })

  useEffect(() => {
    let cancelled = false
    Promise.all([
      api
        .listBugs(EMPTY_FILTERS)
        .then((res) => res.bugs)
        .catch(() => MOCK_BUGS),
      api.listTags().catch(() => MOCK_TAGS),
    ]).then(([bugs, tags]) => {
      if (cancelled) return
      setState({ users: extractUsers(bugs), tags })
    })
    return () => {
      cancelled = true
    }
  }, [])

  return state
}

function extractUsers(bugs: Bug[]): User[] {
  const seen = new Map<number, string>()
  for (const b of bugs) {
    if (b.assigneeId !== null && b.assigneeName !== null) seen.set(b.assigneeId, b.assigneeName)
    if (!seen.has(b.reporterId)) seen.set(b.reporterId, b.reporterName)
  }
  return Array.from(seen.entries())
    .map(([id, username]) => ({ id, username }))
    .sort((a, b) => a.username.localeCompare(b.username))
}
