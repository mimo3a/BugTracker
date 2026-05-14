import { useEffect, useState } from 'react'
import {
  BUG_PRIORITIES,
  BUG_STATUSES,
  PRIORITY_LABELS,
  STATUS_LABELS,
  type BugFilters,
  type BugPriority,
  type BugStatus,
  type Tag,
  type User,
} from '../../types/bug'
import { useDebounce } from '../../hooks/useDebounce'

interface FilterBarProps {
  filters: BugFilters
  onChange: (next: BugFilters) => void
  onReset: () => void
  isDirty: boolean
  users: User[]
  tags: Tag[]
}

const tokenClass =
  'h-8 px-3 pr-8 bg-surface border border-border rounded font-mono text-xs text-ink ' +
  'appearance-none cursor-pointer focus:outline-none focus:border-ink ' +
  'bg-[url("data:image/svg+xml;utf8,<svg xmlns=%27http://www.w3.org/2000/svg%27 width=%2710%27 height=%276%27 viewBox=%270 0 10 6%27><path fill=%27%23666%27 d=%27M0 0l5 6 5-6z%27/></svg>")] ' +
  'bg-no-repeat bg-[right_10px_center]'

export function FilterBar({ filters, onChange, onReset, isDirty, users, tags }: FilterBarProps) {
  // Local search input — debounced 300ms (T060 AC2). Erst dann landet
  // der Suchbegriff in den URL-Filtern, sodass Tipp-Bursts nicht jede
  // Tastatureingabe als Filter-Update durchschlagen.
  const [searchDraft, setSearchDraft] = useState(filters.search)
  const [lastSeenSearch, setLastSeenSearch] = useState(filters.search)
  const debouncedSearch = useDebounce(searchDraft, 300)

  // Wenn der externe Filter sich ändert (z.B. Reset), Draft synchronisieren.
  if (filters.search !== lastSeenSearch) {
    setLastSeenSearch(filters.search)
    setSearchDraft(filters.search)
  }

  // Debounced-Wert in URL/Filter übertragen, sobald er sich vom aktuellen
  // Filter unterscheidet (und nicht gerade von außen synchronisiert wurde).
  useEffect(() => {
    if (debouncedSearch !== filters.search) {
      onChange({ ...filters, search: debouncedSearch })
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedSearch])

  const toggleStatus = (s: BugStatus) => {
    const active = filters.status.includes(s)
    onChange({
      ...filters,
      status: active ? filters.status.filter((x) => x !== s) : [...filters.status, s],
    })
  }

  const handlePriorityChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const v = e.target.value as BugPriority | ''
    onChange({ ...filters, priority: v })
  }

  const handleTagChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const v = e.target.value
    onChange({ ...filters, tagId: v ? Number(v) : null })
  }

  const handleAssigneeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const v = e.target.value
    onChange({ ...filters, assigneeId: v ? Number(v) : null })
  }

  const handleArchivedChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onChange({ ...filters, archived: e.target.checked })
  }

  return (
    <div className="mb-5" role="search" aria-label="Bug filters">
      <div className="flex flex-wrap items-center gap-2">
        <div
          className="flex h-8 w-8 items-center justify-center rounded bg-accent text-accent-fg font-mono text-sm font-bold"
          aria-hidden="true"
        >
          &gt;
        </div>

        <div className="relative">
          <input
            type="search"
            value={searchDraft}
            onChange={(e) => setSearchDraft(e.target.value)}
            placeholder="search title…"
            aria-label="Search by title"
            className="h-8 w-56 px-3 pr-7 bg-surface border border-border rounded font-mono text-xs text-ink placeholder:text-ink-soft focus:outline-none focus:border-ink"
          />
          {searchDraft !== filters.search && (
            <span
              aria-hidden="true"
              title="warte auf eingabe…"
              className="absolute right-2 top-1/2 -translate-y-1/2 inline-block h-1.5 w-1.5 rounded-full bg-amber-fg animate-pulse"
            />
          )}
        </div>

        <select
          value={filters.priority}
          onChange={handlePriorityChange}
          aria-label="Filter by priority"
          className={tokenClass}
        >
          <option value="">priority:any</option>
          {BUG_PRIORITIES.map((p) => (
            <option key={p} value={p}>
              priority:{PRIORITY_LABELS[p]}
            </option>
          ))}
        </select>

        <select
          value={filters.tagId ?? ''}
          onChange={handleTagChange}
          aria-label="Filter by tag"
          className={tokenClass}
        >
          <option value="">tag:any</option>
          {tags.map((t) => (
            <option key={t.id} value={t.id}>
              tag:{t.name.toLowerCase()}
            </option>
          ))}
        </select>

        <select
          value={filters.assigneeId ?? ''}
          onChange={handleAssigneeChange}
          aria-label="Filter by assignee"
          className={tokenClass}
        >
          <option value="">assignee:any</option>
          {users.map((u) => (
            <option key={u.id} value={u.id}>
              assignee:{u.username}
            </option>
          ))}
        </select>

        <label className="flex items-center gap-2 px-2 h-8 font-mono text-xs text-ink cursor-pointer select-none">
          <input
            type="checkbox"
            checked={filters.archived}
            onChange={handleArchivedChange}
            className="accent-ink"
          />
          show archived
        </label>

        <div className="flex-1" />

        <button
          type="button"
          onClick={onReset}
          disabled={!isDirty}
          className="h-8 px-4 border border-border bg-surface text-ink rounded font-mono text-xs font-semibold hover:bg-surface-2 disabled:opacity-40 disabled:cursor-not-allowed"
        >
          Reset
        </button>
      </div>

      <div
        className="flex flex-wrap items-center gap-1.5 mt-2"
        role="group"
        aria-label="Filter by status"
      >
        <span className="font-mono text-[11px] text-ink-soft mr-1">status:</span>
        {BUG_STATUSES.map((s) => {
          const active = filters.status.includes(s)
          return (
            <button
              type="button"
              key={s}
              onClick={() => toggleStatus(s)}
              aria-pressed={active}
              className={`font-mono text-[11px] px-2 py-0.5 rounded border transition-colors ${
                active
                  ? 'bg-ink text-bg border-ink'
                  : 'bg-bg text-ink-soft border-border hover:text-ink'
              }`}
            >
              {STATUS_LABELS[s]}
            </button>
          )
        })}
      </div>
    </div>
  )
}
