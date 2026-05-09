import type { Bug, BugFilters } from '../types/bug'

/**
 * Client-side filter applied to mock data when the API is unreachable.
 * The real backend does this server-side (see openapi.yaml `/api/bugs`).
 */
export function filterBugs(bugs: Bug[], filters: BugFilters): Bug[] {
  return bugs.filter((b) => {
    if (filters.status.length > 0 && !filters.status.includes(b.status)) return false
    if (filters.priority && b.priority !== filters.priority) return false
    if (filters.tagId !== null && b.tagId !== filters.tagId) return false
    if (filters.assigneeId !== null && b.assigneeId !== filters.assigneeId) return false
    if (filters.search.trim()) {
      const q = filters.search.trim().toLowerCase()
      if (!b.title.toLowerCase().includes(q)) return false
    }
    if (!filters.archived && b.archived) return false
    return true
  })
}
