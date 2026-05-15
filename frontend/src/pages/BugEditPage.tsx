import { useEffect, useState, type FormEvent } from 'react'
import { Navigate, useNavigate, useParams } from 'react-router-dom'
import { useBug } from '../hooks/useBug'
import { ApiError, api } from '../lib/api'
import { SEED_TAGS } from '../lib/seedTags'

const TITLE_MAX = 255

export function BugEditPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const bugId = id && /^\d+$/.test(id) ? Number(id) : null
  const { bug, loading, notFound, error: loadError } = useBug(bugId)

  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [tagIds, setTagIds] = useState<number[]>([])
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  // Form mit Bug-Daten initialisieren, sobald geladen.
  useEffect(() => {
    if (bug) {
      setTitle(bug.title)
      setDescription(bug.description ?? '')
      setTagIds(bug.tagIds)
    }
  }, [bug])

  if (bugId === null) return <Navigate to="/bugs" replace />

  function toggleTag(tagId: number) {
    setTagIds((prev) => (prev.includes(tagId) ? prev.filter((x) => x !== tagId) : [...prev, tagId]))
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    if (bugId === null) return
    setError(null)
    setSubmitting(true)
    try {
      await api.updateBug(bugId, {
        title: title.trim(),
        description: description.trim(),
        tagIds,
      })
      navigate('/bugs', { replace: true })
    } catch (err) {
      if (err instanceof ApiError && err.status === 409) {
        setError(err.message || 'Bug ist archiviert oder Status-Konflikt')
      } else if (err instanceof ApiError && err.status === 403) {
        setError('Keine Berechtigung diesen Bug zu bearbeiten')
      } else if (err instanceof ApiError && err.status === 400) {
        setError(err.message || 'Eingaben unvollständig')
      } else if (err instanceof ApiError && err.status === 401) {
        setError('Session abgelaufen — bitte neu einloggen')
      } else {
        setError(err instanceof Error ? err.message : 'Bug konnte nicht gespeichert werden')
      }
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-bg text-ink flex items-center justify-center">
        <span className="font-mono text-sm text-ink-soft">loading bug…</span>
      </div>
    )
  }

  if (notFound || loadError) {
    return (
      <div className="min-h-screen bg-bg text-ink flex items-center justify-center px-4">
        <div className="font-mono text-sm text-center">
          <p className="text-ink mb-2">{notFound ? 'Bug nicht gefunden' : 'Fehler beim Laden'}</p>
          {loadError && <p className="text-red-fg text-xs mb-3">{loadError}</p>}
          <button
            type="button"
            onClick={() => navigate('/bugs')}
            className="text-ink-soft hover:text-ink border border-border rounded px-2 py-1"
          >
            zurück zur Liste
          </button>
        </div>
      </div>
    )
  }

  if (!bug) return null

  const canSubmit = title.trim().length > 0 && description.trim().length > 0 && !submitting

  return (
    <div className="min-h-screen bg-bg text-ink">
      <main className="mx-auto max-w-2xl px-6 py-6">
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
            onClick={() => navigate('/bugs')}
            className="font-mono text-xs text-ink-soft hover:text-ink border border-border rounded px-2 py-1"
          >
            cancel
          </button>
        </header>

        {bug.archived && (
          <p className="font-mono text-xs text-amber-fg mb-4">
            Dieser Bug ist archiviert. Speichern wird mit 409 abgelehnt — erst reaktivieren.
          </p>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="title" className="block font-mono text-xs text-ink-soft mb-1">
              title <span className="text-red-fg">*</span>
            </label>
            <input
              id="title"
              type="text"
              autoFocus
              maxLength={TITLE_MAX}
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full px-3 py-2 bg-bg border border-border rounded font-mono text-sm focus:outline-none focus:border-ink"
              required
            />
            <p className="font-mono text-[10px] text-ink-soft mt-1">
              {title.length} / {TITLE_MAX}
            </p>
          </div>

          <div>
            <label htmlFor="description" className="block font-mono text-xs text-ink-soft mb-1">
              description <span className="text-red-fg">*</span>
            </label>
            <textarea
              id="description"
              rows={6}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full px-3 py-2 bg-bg border border-border rounded font-mono text-sm focus:outline-none focus:border-ink resize-y"
              required
            />
          </div>

          <div>
            <span className="block font-mono text-xs text-ink-soft mb-1">
              tags · status/priority/assignee über PATCH-Endpoints (T050 Inline-Edit)
            </span>
            <div className="flex flex-wrap gap-2">
              {SEED_TAGS.map((tag) => {
                const active = tagIds.includes(tag.id)
                return (
                  <button
                    type="button"
                    key={tag.id}
                    onClick={() => toggleTag(tag.id)}
                    className={`font-mono text-xs px-2 py-1 rounded border transition-colors ${
                      active
                        ? 'bg-ink text-bg border-ink'
                        : 'bg-bg text-ink-soft border-border hover:text-ink'
                    }`}
                  >
                    {tag.name}
                  </button>
                )
              })}
            </div>
          </div>

          {error && <p className="font-mono text-xs text-red-fg">{error}</p>}

          <div className="flex gap-2 pt-2">
            <button
              type="submit"
              disabled={!canSubmit}
              className="px-4 py-2 bg-ink text-bg font-mono text-sm rounded hover:opacity-90 disabled:opacity-50"
            >
              {submitting ? '...' : 'save'}
            </button>
            <button
              type="button"
              onClick={() => navigate('/bugs')}
              className="px-4 py-2 font-mono text-sm border border-border rounded text-ink-soft hover:text-ink"
            >
              cancel
            </button>
          </div>
        </form>
      </main>
    </div>
  )
}
