import type { Bug, BugFilters, BugListResponse, Tag, User } from '../types/bug'

const API_BASE = '/api'

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    credentials: 'include',
    headers: { Accept: 'application/json', ...(init?.headers ?? {}) },
    ...init,
  })
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`)
  return res.json() as Promise<T>
}

function buildBugQuery(filters: BugFilters): string {
  const params = new URLSearchParams()
  for (const s of filters.status) params.append('status', s)
  if (filters.priority) params.set('priority', filters.priority)
  if (filters.assigneeId !== null) params.set('assigneeId', String(filters.assigneeId))
  if (filters.tagId !== null) params.set('tagId', String(filters.tagId))
  if (filters.search.trim()) params.set('search', filters.search.trim())
  if (filters.archived) params.set('archived', 'true')
  const qs = params.toString()
  return qs ? `?${qs}` : ''
}

export const api = {
  listBugs: (filters: BugFilters) => request<BugListResponse>(`/bugs${buildBugQuery(filters)}`),
  listUsers: () => request<User[]>('/users'),
  listTags: () => request<Tag[]>('/tags'),
}

export type { Bug, BugFilters, BugListResponse, Tag, User }
