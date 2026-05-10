import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { ThemeToggle } from '../components/ThemeToggle'
import { api } from '../lib/api'
import { MOCK_BUGS } from '../lib/mockData'
import { PRIORITY_LABELS, type Bug, type BugStatus } from '../types/bug'

const EDITABLE_STATUSES: BugStatus[] = [
  'NEU',
  'IN_BEARBEITUNG',
  'IM_REVIEW',
  'ERLEDIGT',
  'ABGELEHNT',
]

const allowedTransitions: Record<BugStatus, BugStatus[]> = {
  NEU: ['IN_BEARBEITUNG', 'ARCHIVIERT'],
  IN_BEARBEITUNG: ['IM_REVIEW', 'ARCHIVIERT'],
  IM_REVIEW: ['ERLEDIGT', 'ABGELEHNT', 'ARCHIVIERT'],
  ERLEDIGT: ['ARCHIVIERT'],
  ABGELEHNT: ['ARCHIVIERT'],
  ARCHIVIERT: [],
}

export function BugDetailPage() {
  const { id } = useParams()
  const bugId = Number(id)
  const [bug, setBug] = useState<Bug | null>(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [usingMock, setUsingMock] = useState(false)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    api
      .getBug(bugId)
      .then((res) => {
        if (cancelled) return
        setBug(res)
        setUsingMock(false)
        setError(null)
      })
      .catch(() => {
        if (cancelled) return
        const mockBug = MOCK_BUGS.find((b) => b.id === bugId) ?? null
        setBug(mockBug)
        setUsingMock(true)
        setError(mockBug ? null : 'Bug nicht gefunden')
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [bugId])

  const changeStatus = async (nextStatus: BugStatus) => {
    if (!bug || nextStatus === bug.status) return
    setSaving(true)
    setError(null)
    try {
      const updated = usingMock
        ? updateMockStatus(bug, nextStatus)
        : await api.updateBugStatus(bug.id, nextStatus)
      setBug(updated)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ungültiger Statuswechsel')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="min-h-screen bg-bg text-ink">
      <main className="mx-auto max-w-4xl px-6 py-6">
        <header className="mb-5 flex items-center justify-between border-b border-border pb-4">
          <div>
            <Link to="/bugs" className="font-mono text-xs text-ink-soft hover:text-ink">
              Back to bugs
            </Link>
            <h1 className="mt-2 font-mono text-2xl font-bold text-ink">
              {bug ? `BUG-${bug.id}` : 'bug'}
            </h1>
          </div>
          <ThemeToggle />
        </header>

        {loading ? (
          <div className="py-12 text-center font-mono text-sm text-ink-soft">loading bug...</div>
        ) : bug ? (
          <section className="grid gap-5">
            <div className="border-b border-border pb-5">
              <h2 className="font-mono text-xl font-semibold">{bug.title}</h2>
              <p className="mt-3 max-w-2xl text-sm leading-6 text-ink-soft">
                {bug.description ?? 'Keine Beschreibung vorhanden.'}
              </p>
            </div>

            <div className="grid gap-4 sm:grid-cols-2">
              <label className="grid gap-2 font-mono text-xs text-ink-soft">
                Status
                <select
                  value={bug.status === 'ARCHIVIERT' ? '' : bug.status}
                  onChange={(e) => changeStatus(e.target.value as BugStatus)}
                  disabled={saving || bug.status === 'ARCHIVIERT'}
                  className="h-10 rounded border border-border bg-surface px-3 text-sm text-ink focus:border-ink focus:outline-none disabled:opacity-60"
                >
                  {bug.status === 'ARCHIVIERT' && <option value="">archiviert</option>}
                  {EDITABLE_STATUSES.map((status) => (
                    <option key={status} value={status}>
                      {status}
                    </option>
                  ))}
                </select>
              </label>

              <div className="grid gap-2 font-mono text-xs text-ink-soft">
                Priority
                <div className="flex h-10 items-center rounded border border-border bg-surface px-3 text-sm text-ink">
                  {PRIORITY_LABELS[bug.priority]}
                </div>
              </div>
            </div>

            {error && (
              <div className="rounded border border-red-fg bg-red-bg px-3 py-2 font-mono text-sm text-red-fg">
                {error}
              </div>
            )}

            <dl className="grid gap-3 border-t border-border pt-5 font-mono text-sm sm:grid-cols-2">
              <div>
                <dt className="text-xs uppercase text-ink-soft">Reporter</dt>
                <dd>{bug.reporterName}</dd>
              </div>
              <div>
                <dt className="text-xs uppercase text-ink-soft">Assignee</dt>
                <dd>{bug.assigneeName ?? 'unassigned'}</dd>
              </div>
              <div>
                <dt className="text-xs uppercase text-ink-soft">Tags</dt>
                <dd>{bug.tagName ?? bug.tagNames?.join(', ') ?? '-'}</dd>
              </div>
              <div>
                <dt className="text-xs uppercase text-ink-soft">Updated</dt>
                <dd>{new Date(bug.updatedAt).toISOString().slice(0, 10)}</dd>
              </div>
            </dl>

            {usingMock && <p className="font-mono text-xs text-amber-fg">mock data · backend offline</p>}
          </section>
        ) : (
          <div className="py-12 text-center font-mono text-sm text-red-fg">{error}</div>
        )}
      </main>
    </div>
  )
}

function updateMockStatus(bug: Bug, nextStatus: BugStatus): Bug {
  if (!allowedTransitions[bug.status].includes(nextStatus)) {
    throw new Error('Ungültiger Statuswechsel')
  }
  return {
    ...bug,
    status: nextStatus,
    archived: nextStatus === 'ARCHIVIERT' ? true : bug.archived,
    updatedAt: new Date().toISOString(),
  }
}
