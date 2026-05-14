// Bug-tracker domain types — mirrors docs/api/openapi.yaml.

export const BUG_STATUSES = [
  'NEU',
  'IN_BEARBEITUNG',
  'IM_REVIEW',
  'ERLEDIGT',
  'ABGELEHNT',
  'ARCHIVIERT',
] as const

export type BugStatus = (typeof BUG_STATUSES)[number]

export const BUG_PRIORITIES = ['NIEDRIG', 'MITTEL', 'HOCH', 'KRITISCH'] as const

export type BugPriority = (typeof BUG_PRIORITIES)[number]

export interface Bug {
  id: number
  title: string
  description?: string
  status: BugStatus
  priority: BugPriority
  reporterId: number
  reporterName: string
  assigneeId: number | null
  assigneeName: string | null
  tagIds: number[]
  tagNames: string[]
  archived: boolean
  createdAt: string
  updatedAt: string
}

export interface BugListResponse {
  bugs: Bug[]
  total: number
  page: number
  pageSize: number
}

export interface User {
  id: number
  username: string
}

export interface Tag {
  id: number
  name: string
  color?: string | null
}

export interface Activity {
  id: number
  bugId: number
  userId: number
  userName: string
  action: string
  field: string | null
  oldValue: string | null
  newValue: string | null
  createdAt: string
}

export interface Comment {
  id: number
  bugId: number
  userId: number
  userName: string
  content: string
  createdAt: string
}

export interface BugFilters {
  status: BugStatus[]
  priority: BugPriority | ''
  tagId: number | null
  assigneeId: number | null
  search: string
  archived: boolean
}

export const EMPTY_FILTERS: BugFilters = {
  status: [],
  priority: '',
  tagId: null,
  assigneeId: null,
  search: '',
  archived: false,
}

// Display-friendly labels (matches the legacy German UI conventions).
export const STATUS_LABELS: Record<BugStatus, string> = {
  NEU: 'neu',
  IN_BEARBEITUNG: 'in-bearbeitung',
  IM_REVIEW: 'im-review',
  ERLEDIGT: 'erledigt',
  ABGELEHNT: 'abgelehnt',
  ARCHIVIERT: 'archiviert',
}

export const PRIORITY_LABELS: Record<BugPriority, string> = {
  NIEDRIG: 'niedrig',
  MITTEL: 'mittel',
  HOCH: 'hoch',
  KRITISCH: 'kritisch',
}
