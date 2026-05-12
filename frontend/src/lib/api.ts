import type { Bug, BugFilters, BugListResponse, Tag, User } from '../types/bug'
import type { Me } from '../types/auth'

const API_BASE = '/api'

export class ApiError extends Error {
  status: number
  constructor(status: number, message: string) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    credentials: 'include',
    headers: { Accept: 'application/json', ...(init?.headers ?? {}) },
    ...init,
  })
  if (!res.ok) {
    let message = `${res.status} ${res.statusText}`
    try {
      const body = await res.json()
      if (body?.message) message = body.message
      else if (body?.error) message = body.error
    } catch {
      // body was not JSON — keep statusText fallback
    }
    throw new ApiError(res.status, message)
  }
  if (res.status === 204) return undefined as T
  return res.json() as Promise<T>
}

function buildBugQuery(filters: BugFilters): string {
  const params = new URLSearchParams()
  for (const s of filters.status) params.append('status', s)
  if (filters.priority) params.set('priority', filters.priority)
  if (filters.assigneeId !== null) params.set('assigneeId', String(filters.assigneeId))
  if (filters.tagId !== null) params.append('tagIds', String(filters.tagId))
  if (filters.search.trim()) params.set('search', filters.search.trim())
  if (filters.archived) params.set('archived', 'true')
  const qs = params.toString()
  return qs ? `?${qs}` : ''
}

interface LoginBody {
  username: string
  password: string
}

export interface CreateBugInput {
  title: string
  description: string
  priority: Bug['priority']
  tagIds: number[]
}

export interface UpdateBugInput {
  title: string
  description: string
  tagIds: number[]
}

export const api = {
  // bugs
  listBugs: (filters: BugFilters) => request<BugListResponse>(`/bugs${buildBugQuery(filters)}`),
  createBug: (input: CreateBugInput) =>
    request<Bug>('/bugs', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(input),
    }),
  updateBug: (id: number, input: UpdateBugInput) =>
    request<Bug>(`/bugs/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(input),
    }),
  listUsers: () => request<User[]>('/users'),
  listTags: () => request<Tag[]>('/tags'),

  // auth
  auth: {
    login: (body: LoginBody) =>
      request<Me>('/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      }),
    logout: () => request<void>('/auth/logout', { method: 'POST' }),
    me: () => request<Me>('/auth/me'),
  },
}

export type { Bug, BugFilters, BugListResponse, Tag, User }
