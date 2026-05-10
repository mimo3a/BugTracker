import type { Bug, BugFilters, BugListResponse, BugStatus, Tag, User } from '../types/bug'

const API_BASE = '/api'

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    credentials: 'include',
    headers: { Accept: 'application/json', ...(init?.headers ?? {}) },
    ...init,
  })
  if (!res.ok) {
    let message = `${res.status} ${res.statusText}`
    try {
      const body = (await res.json()) as { error?: string; message?: string }
      message = body.error ?? body.message ?? message
    } catch {
      // Keep the HTTP fallback when the response body is not JSON.
    }
    throw new Error(message)
  }
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
  getBug: (id: number) => request<Bug>(`/bugs/${id}`),
  updateBugStatus: (id: number, status: BugStatus) =>
    request<Bug>(`/bugs/${id}/status`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status }),
    }),
  listUsers: () => request<User[]>('/users'),
  listTags: () => request<Tag[]>('/tags'),
}

export type { Bug, BugFilters, BugListResponse, BugStatus, Tag, User }
