import {
  PRIORITY_LABELS,
  STATUS_LABELS,
  type Bug,
  type BugPriority,
  type BugStatus,
} from '../../types/bug'

interface BugTableProps {
  bugs: Bug[]
  loading: boolean
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

function formatDate(iso: string): string {
  return new Date(iso).toISOString().slice(0, 10)
}

export function BugTable({ bugs, loading }: BugTableProps) {
  if (loading) {
    return <div className="py-12 text-center font-mono text-sm text-ink-soft">loading bugs…</div>
  }

  if (bugs.length === 0) {
    return (
      <div className="py-12 text-center font-mono text-sm text-ink-soft">
        no bugs match the current filters.
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
            <tr key={b.id} className="hover:bg-surface-2 transition-colors">
              <td className="px-3.5 py-3.5 border-b border-border text-[13px] text-ink-soft">
                BUG-{b.id}
              </td>
              <td className="px-3.5 py-3.5 border-b border-border text-[13px] text-ink">
                {b.title}
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
                {formatDate(b.createdAt)}
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
