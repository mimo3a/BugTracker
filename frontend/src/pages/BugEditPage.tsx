import { useState } from 'react'
import { Navigate, useNavigate, useParams } from 'react-router-dom'
import { toast } from 'sonner'
import { AppLayout } from '../components/AppLayout'
import { BugForm, type BugFormValues } from '../components/bugs/BugForm'
import { Skeleton } from '../components/Skeleton'
import { useBug } from '../hooks/useBug'
import { useTags } from '../hooks/useTags'
import { ApiError, api } from '../lib/api'

export function BugEditPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const bugId = id && /^\d+$/.test(id) ? Number(id) : null
  const { bug, loading, notFound, error: loadError } = useBug(bugId)
  const { tags } = useTags()

  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  if (bugId === null) return <Navigate to="/bugs" replace />

  async function handleSubmit(values: BugFormValues) {
    if (bugId === null) return
    setError(null)
    setSubmitting(true)
    try {
      await api.updateBug(bugId, values)
      toast.success('bug gespeichert')
      navigate(`/bugs/${bugId}`, { replace: true })
    } catch (err) {
      let msg: string
      if (err instanceof ApiError && err.status === 409) {
        msg = err.message || 'Bug ist archiviert oder Status-Konflikt'
      } else if (err instanceof ApiError && err.status === 403) {
        msg = 'Keine Berechtigung diesen Bug zu bearbeiten'
      } else if (err instanceof ApiError && err.status === 400) {
        msg = err.message || 'Eingaben unvollständig'
      } else if (err instanceof ApiError && err.status === 401) {
        msg = 'Session abgelaufen — bitte neu einloggen'
      } else {
        msg = err instanceof Error ? err.message : 'Bug konnte nicht gespeichert werden'
      }
      setError(msg)
      toast.error(msg)
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <AppLayout width="2xl">
        <div className="space-y-3" role="status" aria-label="bug wird geladen">
          <Skeleton className="h-8 w-1/2" />
          <Skeleton className="h-4 w-1/3" />
          <Skeleton className="h-24 w-full" />
        </div>
      </AppLayout>
    )
  }

  if (notFound || loadError) {
    return (
      <AppLayout width="2xl">
        <div className="text-center py-16">
          <p className="font-mono text-sm text-ink mb-2">
            {notFound ? 'Bug nicht gefunden' : 'Fehler beim Laden'}
          </p>
          {loadError && <p className="font-mono text-xs text-red-fg mb-3">{loadError}</p>}
          <button
            type="button"
            onClick={() => navigate('/bugs')}
            className="font-mono text-xs text-ink-soft hover:text-ink border border-border rounded px-3 py-1.5"
          >
            zurück zur Liste
          </button>
        </div>
      </AppLayout>
    )
  }

  if (!bug) return null

  return (
    <AppLayout width="2xl">
      <header className="flex items-center justify-between mb-5 pb-4 border-b border-border">
        <div>
          <h1 className="font-mono text-2xl font-bold text-ink">edit bug</h1>
          <p className="font-mono text-xs text-ink-soft mt-1">
            BUG-{bug.id} · PUT /api/bugs/{bug.id}
            {bug.archived && <span className="ml-2 text-amber-fg">· archiviert</span>}
          </p>
        </div>
        <button
          type="button"
          onClick={() => navigate(`/bugs/${bug.id}`)}
          className="font-mono text-xs text-ink-soft hover:text-ink border border-border rounded px-2 py-1"
        >
          cancel
        </button>
      </header>

      <BugForm
        tags={tags}
        initial={{ title: bug.title, description: bug.description ?? '', tagIds: bug.tagIds }}
        submitLabel="save"
        submitting={submitting}
        error={error}
        onSubmit={handleSubmit}
        onCancel={() => navigate(`/bugs/${bug.id}`)}
        topAlert={
          bug.archived ? (
            <p className="font-mono text-xs text-amber-fg" role="alert">
              Dieser Bug ist archiviert. Speichern wird mit 409 abgelehnt — erst reaktivieren.
            </p>
          ) : null
        }
      />
    </AppLayout>
  )
}
