import { useNavigate } from 'react-router-dom'
import {
  PRIORITY_LABELS,
  STATUS_LABELS,
  type Bug,
  type BugPriority,
  type BugStatus,
} from '../../types/bug'
import { formatLocalDate } from '../../lib/dates'
import { highlight } from '../../lib/highlight'
import { SkeletonRow } from '../Skeleton'

interface BugTableProps {
  bugs: Bug[]
  loading: boolean
  /** Aktueller Suchbegriff zum Hervorheben im Titel (T060). */
  searchQuery?: string
}

const priorityPill: Record<BugPriority, string> = {
  NIEDRIG: 'bg-green-bg text-green-fg',
  MITTEL: 'bg-amber-bg text-amber-fg',
  HOCH: 'bg-orange-bg text-orange-fg',
  KRITISCH: 'bg-red-bg text-red-fg',
}

const statusPill: Record<BugStatus, string> = {
  NEU: 'bg-blue-bg text-blue-fg',
  IN_BEARBEITUNG: 'bg-purple-bg text-purple-fg',
  IM_REVIEW: 'bg-amber-bg text-amber-fg',
  ERLEDIGT: 'bg-green-bg text-green-fg',
  ABGELEHNT: 'bg-red-bg text-red-fg',
  ARCHIVIERT: 'bg-slate-bg text-slate-fg',
}

const pillBase =
  'inline-block px-2 py-0.5 rounded text-[11px] font-semibold uppercase tracking-wide'

export function BugTable({ bugs, loading, searchQuery = '' }: BugTableProps) {
  const navigate = useNavigate()

  if (loading) {
    return (
      <div className="border border-border rounded p-3" role="status" aria-label="bugs werden geladen">
        {Array.from({ length: 6 }, (_, i) => (
          <SkeletonRow key={i} cells={6} />
        ))}
      </div>
    )
  }

  if (bugs.length === 0) {
    return (
      <div className="py-16 text-center border border-dashed border-border rounded">
        <p className="font-mono text-sm text-ink mb-1">keine bugs gefunden</p>
        <p className="font-mono text-xs text-ink-soft mb-4">
          ändere die filter — oder lege einen neuen bug an.
        </p>
        <button
          type="button"
          onClick={() => navigate('/bugs/new')}
          className="font-mono text-xs bg-ink text-bg rounded px-3 py-1.5 hover:opacity-90"
        >
          + new bug
        </button>
      </div>
    )
  }

  return (
    <div className="overflow-x-auto border border-border rounded">
      <table className="w-full font-mono border-collapse">
        <thead>
          <tr className="bg-surface-2">
            <th className="text-left px-3.5 py-3 text-[11px] font-semibold uppercase tracking-[0.6px] text-ink-soft border-b border-border w-20">
              ID
            </th>
            <th className="text-left px-3.5 py-3 text-[11px] font-semibold uppercase tracking-[0.6px] text-ink-soft border-b border-border">
              Title
            </th>
            <th className="text-left px-3.5 py-3 text-[11px] font-semibold uppercase tracking-[0.6px] text-ink-soft border-b border-border w-32">
              Tag
            </th>
            <th className="text-left px-3.5 py-3 text-[11px] font-semibold uppercase tracking-[0.6px] text-ink-soft border-b border-border w-32">
              Priority
            </th>
            <th className="text-left px-3.5 py-3 text-[11px] font-semibold uppercase tracking-[0.6px] text-ink-soft border-b border-border w-40">
              Status
            </th>
            <th className="text-left px-3.5 py-3 text-[11px] font-semibold uppercase tracking-[0.6px] text-ink-soft border-b border-border w-28">
              Created
            </th>
            <th className="text-left px-3.5 py-3 text-[11px] font-semibold uppercase tracking-[0.6px] text-ink-soft border-b border-border w-32">
              Assignee
            </th>
          </tr>
        </thead>
        <tbody>
          {bugs.map((b) => (
            <tr
              key={b.id}
              onClick={() => {
                // Wenn User Text in der Zeile markiert hat, nicht navigieren — sonst
                // verliert man das Copy-Paste, weil mouseup gleichzeitig click triggert.
                if (window.getSelection()?.toString().length) return
                navigate(`/bugs/${b.id}`)
              }}
              onKeyDown={(e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                  e.preventDefault()
                  navigate(`/bugs/${b.id}`)
                }
              }}
              tabIndex={0}
              role="link"
              aria-label={`Bug ${b.id}: ${b.title} öffnen`}
              className="cursor-pointer hover:bg-surface-2 focus:bg-surface-2 focus:outline-none transition-colors"
            >
              <td className="px-3.5 py-3.5 border-b border-border text-[13px] text-ink-soft">
                BUG-{b.id}
              </td>
              <td className="px-3.5 py-3.5 border-b border-border text-[13px] text-ink">
                {highlight(b.title, searchQuery)}
              </td>
              <td className="px-3.5 py-3.5 border-b border-border text-[13px] text-ink-soft">
                {b.tagNames.length > 0 ? b.tagNames.join(', ') : '—'}
              </td>
              <td className="px-3.5 py-3.5 border-b border-border">
                <span className={`${pillBase} ${priorityPill[b.priority]}`}>
                  {PRIORITY_LABELS[b.priority]}
                </span>
              </td>
              <td className="px-3.5 py-3.5 border-b border-border">
                <span className={`${pillBase} ${statusPill[b.status]}`}>
                  {STATUS_LABELS[b.status]}
                </span>
              </td>
              <td className="px-3.5 py-3.5 border-b border-border text-[13px] text-ink-soft">
                {formatLocalDate(b.createdAt)}
              </td>
              <td className="px-3.5 py-3.5 border-b border-border text-[13px] text-ink-soft">
                {b.assigneeName ?? <span className="italic">unassigned</span>}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
