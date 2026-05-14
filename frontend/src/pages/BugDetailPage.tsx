import { useState } from 'react'
import { Link, Navigate, useNavigate, useParams } from 'react-router-dom'
import { toast } from 'sonner'
import { AppLayout } from '../components/AppLayout'
import { ConfirmDialog } from '../components/ConfirmDialog'
import { ErrorBoundary, inlineErrorFallback } from '../components/ErrorBoundary'
import { Skeleton } from '../components/Skeleton'
import { ActivityTimeline } from '../components/bugs/ActivityTimeline'
import { CommentsSection } from '../components/bugs/CommentsSection'
import { useBug } from '../hooks/useBug'
import { useActivities } from '../hooks/useActivities'
import { useFilterOptions } from '../hooks/useBugs'
import { ApiError, api } from '../lib/api'
import { formatLocalDate } from '../lib/dates'
import {
  BUG_STATUSES,
  PRIORITY_LABELS,
  STATUS_LABELS,
  type Bug,
  type BugPriority,
  type BugStatus,
} from '../types/bug'

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

function describeApiError(err: unknown, fallback: string): string {
  if (err instanceof ApiError) {
    if (err.status === 403) return 'keine berechtigung'
    if (err.status === 404) return 'endpoint nicht verfügbar (backend folgt)'
    if (err.status === 405) return 'methode noch nicht implementiert (backend)'
    if (err.status === 409) return err.message || 'konflikt — zustand neu laden'
    return err.message || fallback
  }
  return err instanceof Error ? err.message : fallback
}

export function BugDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const bugId = id && /^\d+$/.test(id) ? Number(id) : null

  const { bug, loading, notFound, error: loadError, refresh, setBug } = useBug(bugId)
  const { activities, loading: actLoading, error: actError, refresh: refreshActivities } =
    useActivities(bugId)
  const { users, tags, usingMockTags } = useFilterOptions()

  const [busyStatus, setBusyStatus] = useState(false)
  const [busyAssignee, setBusyAssignee] = useState(false)
  const [busyTags, setBusyTags] = useState(false)
  const [busyArchive, setBusyArchive] = useState(false)
  const [confirmArchive, setConfirmArchive] = useState(false)
  const [confirmReactivate, setConfirmReactivate] = useState(false)

  if (bugId === null) return <Navigate to="/bugs" replace />

  if (loading) {
    return (
      <AppLayout width="4xl">
        <div className="space-y-3" role="status" aria-label="bug wird geladen">
          <Skeleton className="h-8 w-1/2" />
          <Skeleton className="h-4 w-1/3" />
          <Skeleton className="h-24 w-full" />
          <Skeleton className="h-4 w-1/4" />
          <Skeleton className="h-4 w-1/4" />
        </div>
      </AppLayout>
    )
  }

  if (notFound || loadError || !bug) {
    return (
      <AppLayout width="4xl">
        <div className="text-center py-16">
          <p className="font-mono text-sm text-ink mb-2">
            {notFound ? 'bug nicht gefunden' : 'fehler beim laden'}
          </p>
          {loadError && <p className="font-mono text-xs text-red-fg mb-3">{loadError}</p>}
          <button
            type="button"
            onClick={() => navigate('/bugs')}
            className="font-mono text-xs text-ink-soft hover:text-ink border border-border rounded px-3 py-1.5"
          >
            zurück zur liste
          </button>
        </div>
      </AppLayout>
    )
  }

  async function handleStatusChange(next: BugStatus) {
    if (!bug || next === bug.status) return
    setBusyStatus(true)
    try {
      const updated = await api.updateBugStatus(bug.id, next)
      setBug(updated)
      refreshActivities()
      toast.success(`status: ${STATUS_LABELS[next]}`)
    } catch (err) {
      toast.error(describeApiError(err, 'status-update fehlgeschlagen'))
    } finally {
      setBusyStatus(false)
    }
  }

  async function handleAssigneeChange(nextId: number | null) {
    if (!bug || nextId === bug.assigneeId) return
    setBusyAssignee(true)
    try {
      const updated = await api.updateBugAssignee(bug.id, nextId)
      setBug(updated)
      refreshActivities()
      toast.success(nextId === null ? 'bearbeiter entfernt' : 'bearbeiter geändert')
    } catch (err) {
      toast.error(describeApiError(err, 'bearbeiter-update fehlgeschlagen'))
    } finally {
      setBusyAssignee(false)
    }
  }

  async function toggleTag(tagId: number) {
    if (!bug) return
    const next = bug.tagIds.includes(tagId)
      ? bug.tagIds.filter((x) => x !== tagId)
      : [...bug.tagIds, tagId]
    setBusyTags(true)
    try {
      const updated = await api.updateBug(bug.id, {
        title: bug.title,
        description: bug.description ?? '',
        tagIds: next,
      })
      setBug(updated)
      refreshActivities()
    } catch (err) {
      toast.error(describeApiError(err, 'tag-update fehlgeschlagen'))
    } finally {
      setBusyTags(false)
    }
  }

  async function handleArchive() {
    if (!bug) return
    setBusyArchive(true)
    try {
      const updated = await api.archiveBug(bug.id)
      setBug(updated)
      refreshActivities()
      toast.success('bug archiviert')
      setConfirmArchive(false)
    } catch (err) {
      toast.error(describeApiError(err, 'archivieren fehlgeschlagen'))
    } finally {
      setBusyArchive(false)
    }
  }

  async function handleReactivate() {
    if (!bug) return
    setBusyArchive(true)
    try {
      const updated = await api.reactivateBug(bug.id)
      setBug(updated)
      refreshActivities()
      toast.success('bug reaktiviert')
      setConfirmReactivate(false)
    } catch (err) {
      toast.error(describeApiError(err, 'reaktivieren fehlgeschlagen'))
    } finally {
      setBusyArchive(false)
    }
  }

  const currentBug: Bug = bug

  return (
    <AppLayout width="4xl">
      <header className="flex items-start justify-between mb-5 pb-4 border-b border-border gap-4">
        <div className="min-w-0 flex-1">
          <p className="font-mono text-xs text-ink-soft mb-1">
            BUG-{currentBug.id}
            {currentBug.archived && <span className="ml-2 text-amber-fg">· archiviert</span>}
          </p>
          <h1 className="font-mono text-xl font-bold text-ink break-words">{currentBug.title}</h1>
          <p className="font-mono text-xs text-ink-soft mt-1">GET /api/bugs/{currentBug.id}</p>
        </div>
        <div className="flex items-center gap-2 flex-shrink-0">
          <Link
            to={`/bugs/${currentBug.id}/edit`}
            className="font-mono text-xs bg-ink text-bg rounded px-3 py-1.5 hover:opacity-90"
          >
            bearbeiten
          </Link>
          {currentBug.archived ? (
            <button
              type="button"
              onClick={() => setConfirmReactivate(true)}
              className="font-mono text-xs text-ink-soft hover:text-ink border border-border rounded px-3 py-1.5"
            >
              reaktivieren
            </button>
          ) : (
            <button
              type="button"
              onClick={() => setConfirmArchive(true)}
              className="font-mono text-xs text-red-fg hover:opacity-80 border border-red-fg/40 rounded px-3 py-1.5"
            >
              archivieren
            </button>
          )}
          <button
            type="button"
            onClick={() => navigate('/bugs')}
            className="font-mono text-xs text-ink-soft hover:text-ink border border-border rounded px-3 py-1.5"
          >
            zurück
          </button>
        </div>
      </header>

      <section className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="md:col-span-2 space-y-5">
          <div>
            <h2 className="font-mono text-xs text-ink-soft uppercase tracking-wide mb-2">
              beschreibung
            </h2>
            <p className="font-mono text-sm text-ink whitespace-pre-wrap break-words border border-border rounded p-3 bg-surface">
              {currentBug.description?.trim() ? (
                currentBug.description
              ) : (
                <span className="text-ink-soft italic">keine beschreibung.</span>
              )}
            </p>
          </div>

          <div>
            <h2 className="font-mono text-xs text-ink-soft uppercase tracking-wide mb-2">
              tags
            </h2>
            {usingMockTags ? (
              <p className="font-mono text-xs text-amber-fg" role="status">
                tag-zuordnung deaktiviert · /api/tags noch nicht implementiert (T038a)
              </p>
            ) : (
              <div className="flex flex-wrap gap-2">
                {tags.length === 0 && (
                  <span className="font-mono text-xs text-ink-soft italic">
                    keine tags vorhanden.
                  </span>
                )}
                {tags.map((tag) => {
                  const active = currentBug.tagIds.includes(tag.id)
                  return (
                    <button
                      type="button"
                      key={tag.id}
                      onClick={() => toggleTag(tag.id)}
                      disabled={busyTags}
                      className={`font-mono text-xs px-2 py-1 rounded border transition-colors ${
                        active
                          ? 'bg-ink text-bg border-ink'
                          : 'bg-bg text-ink-soft border-border hover:text-ink'
                      } disabled:opacity-50`}
                    >
                      {tag.name}
                    </button>
                  )
                })}
              </div>
            )}
          </div>
        </div>

        <aside className="space-y-4">
          <div>
            <label
              htmlFor="status"
              className="block font-mono text-xs text-ink-soft uppercase tracking-wide mb-1"
            >
              status
            </label>
            <select
              id="status"
              value={currentBug.status}
              disabled={busyStatus}
              onChange={(e) => handleStatusChange(e.target.value as BugStatus)}
              className="w-full h-9 px-2 bg-surface border border-border rounded font-mono text-xs focus:outline-none focus:border-ink disabled:opacity-50"
            >
              {BUG_STATUSES.map((s) => (
                <option key={s} value={s}>
                  {STATUS_LABELS[s]}
                </option>
              ))}
            </select>
            <span className={`mt-2 ${pillBase} ${statusPill[currentBug.status]}`}>
              {STATUS_LABELS[currentBug.status]}
            </span>
          </div>

          <div>
            <label
              htmlFor="assignee"
              className="block font-mono text-xs text-ink-soft uppercase tracking-wide mb-1"
            >
              bearbeiter
            </label>
            <select
              id="assignee"
              value={currentBug.assigneeId ?? ''}
              disabled={busyAssignee}
              onChange={(e) =>
                handleAssigneeChange(e.target.value ? Number(e.target.value) : null)
              }
              className="w-full h-9 px-2 bg-surface border border-border rounded font-mono text-xs focus:outline-none focus:border-ink disabled:opacity-50"
            >
              <option value="">— unassigned —</option>
              {users.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.username}
                </option>
              ))}
            </select>
          </div>

          <div>
            <span className="block font-mono text-xs text-ink-soft uppercase tracking-wide mb-1">
              priority
            </span>
            <span className={`${pillBase} ${priorityPill[currentBug.priority]}`}>
              {PRIORITY_LABELS[currentBug.priority]}
            </span>
            <p className="font-mono text-[10px] text-ink-soft mt-1">
              ändern via "bearbeiten"
            </p>
          </div>

          <dl className="font-mono text-xs space-y-1 pt-2 border-t border-border">
            <div className="flex justify-between">
              <dt className="text-ink-soft">reporter</dt>
              <dd className="text-ink">{currentBug.reporterName}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-ink-soft">erstellt</dt>
              <dd className="text-ink">{formatLocalDate(currentBug.createdAt)}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-ink-soft">geändert</dt>
              <dd className="text-ink">{formatLocalDate(currentBug.updatedAt)}</dd>
            </div>
          </dl>

          <button
            type="button"
            onClick={refresh}
            className="font-mono text-[10px] text-ink-soft hover:text-ink underline-offset-2 hover:underline"
          >
            neu laden
          </button>
        </aside>
      </section>

      <section className="border-t border-border pt-6 mb-8">
        <h2 className="font-mono text-sm font-bold text-ink mb-3">verlauf</h2>
        <ErrorBoundary fallback={inlineErrorFallback('verlauf')}>
          <ActivityTimeline
            activities={activities}
            loading={actLoading}
            error={actError}
            reporterName={currentBug.reporterName}
            createdAt={currentBug.createdAt}
            users={users}
            tags={tags}
          />
        </ErrorBoundary>
      </section>

      <section className="border-t border-border pt-6">
        <ErrorBoundary fallback={inlineErrorFallback('kommentare')}>
          <CommentsSection bugId={currentBug.id} />
        </ErrorBoundary>
      </section>

      <ConfirmDialog
        open={confirmArchive}
        title="bug archivieren?"
        message={`BUG-${currentBug.id} wird auf "ARCHIVIERT" gesetzt. später reaktivierbar.`}
        confirmLabel="archivieren"
        destructive
        onCancel={() => setConfirmArchive(false)}
        onConfirm={handleArchive}
        busy={busyArchive}
      />
      <ConfirmDialog
        open={confirmReactivate}
        title="bug reaktivieren?"
        message={`BUG-${currentBug.id} wird zurück auf "NEU" gesetzt.`}
        confirmLabel="reaktivieren"
        onCancel={() => setConfirmReactivate(false)}
        onConfirm={handleReactivate}
        busy={busyArchive}
      />
    </AppLayout>
  )
}
