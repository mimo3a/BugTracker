import type {
  Activity,
  Bug,
  BugFilters,
  BugListResponse,
  BugStatus,
  Comment,
  Tag,
  User,
} from '../types/bug'
import type { AdminUser, Me, RegisterInput, UserRole } from '../types/auth'

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
      if (typeof body?.message === 'string') message = body.message
      else if (typeof body?.error === 'string') message = body.error
      else if (body && typeof body === 'object') {
        // Spring Bean-Validation: { fieldName: "msg", ... }
        const fieldMessages = Object.values(body).filter(
          (v): v is string => typeof v === 'string',
        )
        if (fieldMessages.length > 0) message = fieldMessages.join(', ')
      }
    } catch {
      // body was not JSON — keep statusText fallback
    }
    throw new ApiError(res.status, message)
  }
  if (res.status === 204) return undefined as T
  return res.json() as Promise<T>
}

function buildBugQuery(filters: BugFilters, page = 1): string {
  const params = new URLSearchParams()
  for (const s of filters.status) params.append('status', s)
  if (filters.priority) params.set('priority', filters.priority)
  if (filters.assigneeId !== null) params.set('assigneeId', String(filters.assigneeId))
  if (filters.tagId !== null) params.append('tagIds', String(filters.tagId))
  if (filters.search.trim()) params.set('search', filters.search.trim())
  if (filters.archived) params.set('archived', 'true')
  // Backend ist 0-indexed (Spring-Pageable-Konvention), UI 1-indexed.
  if (page > 1) params.set('page', String(page - 1))
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

export interface TagInput {
  name: string
  color?: string | null
}

export interface UpdateUserInput {
  role?: UserRole
  active?: boolean
}

export interface CreateCommentInput {
  content: string
}

const json = (body: unknown): RequestInit => ({
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(body),
})

export const api = {
  // bugs — listing + CRUD
  listBugs: (filters: BugFilters, page = 1) =>
    request<BugListResponse>(`/bugs${buildBugQuery(filters, page)}`),
  getBug: (id: number) => request<Bug>(`/bugs/${id}`),
  createBug: (input: CreateBugInput) => request<Bug>('/bugs', { method: 'POST', ...json(input) }),
  updateBug: (id: number, input: UpdateBugInput) =>
    request<Bug>(`/bugs/${id}`, { method: 'PUT', ...json(input) }),

  // bugs — inline edits (T050)
  updateBugStatus: (id: number, status: BugStatus) =>
    request<Bug>(`/bugs/${id}/status`, { method: 'PATCH', ...json({ status }) }),
  updateBugAssignee: (id: number, assigneeId: number | null) =>
    request<Bug>(`/bugs/${id}/assignee`, { method: 'PATCH', ...json({ assigneeId }) }),

  // bugs — archive / reactivate (T051) → backend T035 (PATCH /archive + /restore)
  archiveBug: (id: number) => request<Bug>(`/bugs/${id}/archive`, { method: 'PATCH' }),
  reactivateBug: (id: number) => request<Bug>(`/bugs/${id}/restore`, { method: 'PATCH' }),

  // bugs — history (T059)
  listActivities: (bugId: number) => request<Activity[]>(`/bugs/${bugId}/activities`),

  // bugs — comments (T062)
  listComments: (bugId: number) => request<Comment[]>(`/bugs/${bugId}/comments`),
  createComment: (bugId: number, input: CreateCommentInput) =>
    request<Comment>(`/bugs/${bugId}/comments`, { method: 'POST', ...json(input) }),

  // tags — CRUD (T053a)
  listTags: () => request<Tag[]>('/tags'),
  createTag: (input: TagInput) => request<Tag>('/tags', { method: 'POST', ...json(input) }),
  updateTag: (id: number, input: TagInput) =>
    request<Tag>(`/tags/${id}`, { method: 'PATCH', ...json(input) }),
  deleteTag: (id: number) => request<void>(`/tags/${id}`, { method: 'DELETE' }),

  // users (T053b) — `listUsers` legacy (id+username only); `listAdminUsers` für Admin-Tabelle
  listUsers: () => request<User[]>('/users'),
  listAdminUsers: () => request<AdminUser[]>('/users'),
  updateUser: (id: number, input: UpdateUserInput) =>
    request<AdminUser>(`/users/${id}`, { method: 'PATCH', ...json(input) }),

  // auth
  auth: {
    login: (body: LoginBody) => request<Me>('/auth/login', { method: 'POST', ...json(body) }),
    logout: () => request<void>('/auth/logout', { method: 'POST' }),
    me: () => request<Me>('/auth/me'),
    register: (body: RegisterInput) =>
      request<Me>('/auth/register', { method: 'POST', ...json(body) }),
  },
}

export type { Bug, BugFilters, BugListResponse, Tag, User }
