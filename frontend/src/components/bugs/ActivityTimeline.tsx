import type { Activity, Tag, User } from '../../types/bug'
import { formatLocalDateTime } from '../../lib/dates'
import { Skeleton } from '../Skeleton'

interface Props {
  activities: Activity[]
  loading: boolean
  error: string | null
  reporterName?: string
  createdAt?: string
  /** Für ID→Name-Resolution bei assignee-Änderungen. */
  users?: User[]
  /** Für ID→Name-Resolution bei tag-Änderungen. */
  tags?: Tag[]
}

// Mapping von Backend-Field-Namen auf User-freundliche Labels.
const FIELD_LABELS: Record<string, string> = {
  title: 'titel',
  description: 'beschreibung',
  status: 'status',
  priority: 'priorität',
  assigneeid: 'bearbeiter',
  tagids: 'tags',
  archived: 'archiv-status',
}

export function ActivityTimeline({
  activities,
  loading,
  error,
  reporterName,
  createdAt,
  users = [],
  tags = [],
}: Props) {
  if (loading) {
    return (
      <div className="space-y-3" role="status" aria-label="verlauf wird geladen">
        <Skeleton className="h-4 w-2/3" />
        <Skeleton className="h-4 w-1/2" />
        <Skeleton className="h-4 w-3/4" />
      </div>
    )
  }

  if (error) {
    return (
      <p className="font-mono text-xs text-red-fg" role="alert">
        verlauf konnte nicht geladen werden: {error}
      </p>
    )
  }

  // Fallback-Eintrag für leeren Verlauf (AC3): mindestens das Erstell-Event.
  const items: Activity[] =
    activities.length > 0
      ? activities
      : reporterName && createdAt
        ? [
            {
              id: -1,
              bugId: 0,
              userId: 0,
              userName: reporterName,
              action: 'CREATED',
              field: null,
              oldValue: null,
              newValue: null,
              createdAt,
            },
          ]
        : []

  if (items.length === 0) {
    return <p className="font-mono text-xs text-ink-soft">noch keine aktivitäten erfasst.</p>
  }

  return (
    <ol className="relative pl-5" aria-label="Verlauf">
      <span aria-hidden="true" className="absolute left-1.5 top-1 bottom-1 w-px bg-border" />
      {items.map((a) => (
        <li key={a.id} className="relative mb-4 last:mb-0">
          <span
            aria-hidden="true"
            className="absolute -left-3.5 top-1.5 w-2.5 h-2.5 rounded-full bg-ink border-2 border-bg"
          />
          <div className="font-mono text-xs">
            <p className="text-ink-soft mb-0.5">
              <time dateTime={a.createdAt}>{formatLocalDateTime(a.createdAt)}</time>
              <span className="mx-1.5">·</span>
              <span className="text-ink font-semibold">{a.userName}</span>
            </p>
            <p className="text-ink">{renderActivityLine(a, users, tags)}</p>
          </div>
        </li>
      ))}
    </ol>
  )
}

function renderActivityLine(a: Activity, users: User[], tags: Tag[]): string {
  if (a.action === 'CREATED') return 'bug erstellt.'
  if (a.action !== 'UPDATED' || !a.field) return a.action.toLowerCase()

  const field = a.field.toLowerCase()
  const label = FIELD_LABELS[field] ?? field

  // archived-Toggle als Klartext rendern.
  if (field === 'archived') {
    if (a.newValue === 'true') return 'archiviert.'
    if (a.newValue === 'false') return 'reaktiviert.'
  }

  const oldDisplay = resolveValue(field, a.oldValue, users, tags)
  const newDisplay = resolveValue(field, a.newValue, users, tags)
  return `${label} geändert: ${oldDisplay} → ${newDisplay}`
}

function resolveValue(field: string, raw: string | null, users: User[], tags: Tag[]): string {
  if (raw === null || raw === '') return '∅'

  if (field === 'assigneeid') {
    const id = Number(raw)
    if (!Number.isNaN(id)) {
      const u = users.find((x) => x.id === id)
      if (u) return u.username
    }
    return raw
  }

  if (field === 'tagids') {
    // Backend speichert tagIds als JSON-ähnlichen String "[1, 2]" — parsen + lookup.
    const ids = parseIdList(raw)
    if (ids === null) return raw
    if (ids.length === 0) return '∅'
    return ids.map((id) => tags.find((t) => t.id === id)?.name ?? `#${id}`).join(', ')
  }

  return raw
}

function parseIdList(raw: string): number[] | null {
  // Akzeptiert "[]", "[1, 2]", "[1,2,3]" — defensiv gegen Whitespace.
  const trimmed = raw.trim()
  if (trimmed === '[]') return []
  const match = trimmed.match(/^\[([^\]]*)\]$/)
  if (!match) return null
  const parts = match[1]!.split(',').map((s) => s.trim()).filter(Boolean)
  const ids = parts.map(Number)
  if (ids.some((n) => Number.isNaN(n))) return null
  return ids
}
