import { useCallback, useMemo } from 'react'
import { useSearchParams } from 'react-router-dom'
import {
  BUG_PRIORITIES,
  BUG_STATUSES,
  type BugFilters,
  type BugPriority,
  type BugStatus,
} from '../types/bug'

/**
 * Two-way binding between filter state and URL search params, so filtered
 * views are deep-linkable (T053 acceptance criterion: "URL-Sync").
 */
export function useBugFilters() {
  const [searchParams, setSearchParams] = useSearchParams()

  const filters = useMemo<BugFilters>(() => {
    const status = searchParams
      .getAll('status')
      .filter((s): s is BugStatus => (BUG_STATUSES as readonly string[]).includes(s))

    const priorityRaw = searchParams.get('priority') ?? ''
    const priority: BugPriority | '' = (BUG_PRIORITIES as readonly string[]).includes(priorityRaw)
      ? (priorityRaw as BugPriority)
      : ''

    const tagIdRaw = searchParams.get('tagId')
    const tagId = tagIdRaw && /^\d+$/.test(tagIdRaw) ? Number(tagIdRaw) : null

    const assigneeRaw = searchParams.get('assigneeId')
    const assigneeId = assigneeRaw && /^\d+$/.test(assigneeRaw) ? Number(assigneeRaw) : null

    return {
      status,
      priority,
      tagId,
      assigneeId,
      search: searchParams.get('search') ?? '',
      archived: searchParams.get('archived') === 'true',
    }
  }, [searchParams])

  const setFilters = useCallback(
    (next: BugFilters) => {
      const params = new URLSearchParams()
      for (const s of next.status) params.append('status', s)
      if (next.priority) params.set('priority', next.priority)
      if (next.tagId !== null) params.set('tagId', String(next.tagId))
      if (next.assigneeId !== null) params.set('assigneeId', String(next.assigneeId))
      if (next.search.trim()) params.set('search', next.search.trim())
      if (next.archived) params.set('archived', 'true')
      setSearchParams(params, { replace: false })
    },
    [setSearchParams],
  )

  const reset = useCallback(() => {
    setSearchParams(new URLSearchParams(), { replace: false })
  }, [setSearchParams])

  const isDirty = useMemo(
    () =>
      filters.status.length > 0 ||
      filters.priority !== '' ||
      filters.tagId !== null ||
      filters.assigneeId !== null ||
      filters.search.trim().length > 0 ||
      filters.archived,
    [filters],
  )

  return { filters, setFilters, reset, isDirty }
}
