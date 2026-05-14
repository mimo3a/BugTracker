import { useState } from 'react'
import { toast } from 'sonner'
import { AppLayout } from '../components/AppLayout'
import { ConfirmDialog } from '../components/ConfirmDialog'
import { Skeleton } from '../components/Skeleton'
import { useTags } from '../hooks/useTags'
import { ApiError, api } from '../lib/api'
import type { Tag } from '../types/bug'

const PRESET_COLORS = [
  '#ef4444',
  '#f97316',
  '#eab308',
  '#22c55e',
  '#06b6d4',
  '#3b82f6',
  '#8b5cf6',
  '#ec4899',
  '#64748b',
]

interface EditDraft {
  id: number | null
  name: string
  color: string
}

const EMPTY_DRAFT: EditDraft = { id: null, name: '', color: PRESET_COLORS[0]! }

function describeApiError(err: unknown, fallback: string): string {
  if (err instanceof ApiError) {
    if (err.status === 403) return 'keine berechtigung'
    if (err.status === 404 || err.status === 405)
      return 'backend-endpoint noch nicht verfügbar — feature kommt mit dem tag-controller'
    if (err.status === 409) return err.message || 'tag existiert bereits'
    return err.message || fallback
  }
  return err instanceof Error ? err.message : fallback
}

export function AdminTagsPage() {
  const { tags, loading, error, usingMock, refresh } = useTags()
  const [draft, setDraft] = useState<EditDraft>(EMPTY_DRAFT)
  const [submitting, setSubmitting] = useState(false)
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const [deleting, setDeleting] = useState(false)

  function startCreate() {
    setDraft(EMPTY_DRAFT)
  }

  function startEdit(tag: Tag) {
    setDraft({ id: tag.id, name: tag.name, color: tag.color ?? PRESET_COLORS[0]! })
  }

  function cancel() {
    setDraft(EMPTY_DRAFT)
  }

  async function handleSave() {
    const name = draft.name.trim()
    if (!name) {
      toast.error('name darf nicht leer sein')
      return
    }
    setSubmitting(true)
    try {
      if (draft.id === null) {
        await api.createTag({ name, color: draft.color })
        toast.success(`tag "${name}" angelegt`)
      } else {
        await api.updateTag(draft.id, { name, color: draft.color })
        toast.success(`tag "${name}" gespeichert`)
      }
      setDraft(EMPTY_DRAFT)
      refresh()
    } catch (err) {
      toast.error(describeApiError(err, 'speichern fehlgeschlagen'))
    } finally {
      setSubmitting(false)
    }
  }

  async function handleDelete() {
    if (deleteId === null) return
    setDeleting(true)
    try {
      await api.deleteTag(deleteId)
      toast.success('tag gelöscht')
      setDeleteId(null)
      refresh()
    } catch (err) {
      toast.error(describeApiError(err, 'löschen fehlgeschlagen'))
    } finally {
      setDeleting(false)
    }
  }

  return (
    <AppLayout width="4xl">
      <header className="flex items-center justify-between mb-5 pb-4 border-b border-border">
        <div>
          <h1 className="font-mono text-2xl font-bold text-ink">admin · tags</h1>
          <p className="font-mono text-xs text-ink-soft mt-1">/api/tags</p>
        </div>
        <button
          type="button"
          onClick={startCreate}
          disabled={usingMock}
          title={usingMock ? 'anlegen verfügbar mit /api/tags-Backend (T038a)' : undefined}
          className="font-mono text-xs bg-ink text-bg rounded px-3 py-1.5 hover:opacity-90 disabled:opacity-40 disabled:cursor-not-allowed"
        >
          + neuer tag
        </button>
      </header>

      {usingMock && (
        <p className="font-mono text-xs text-amber-fg mb-3" role="status">
          mock-daten · /api/tags nicht erreichbar — anlegen / bearbeiten / löschen
          deaktiviert bis T038a-Backend gemerged ist.
        </p>
      )}
      {error && !usingMock && (
        <p className="font-mono text-xs text-red-fg mb-3" role="alert">
          {error}
        </p>
      )}

      {/* Edit-Form */}
      <section
        className={`border border-border rounded p-4 mb-6 bg-surface ${usingMock ? 'opacity-50 pointer-events-none' : ''}`}
        aria-disabled={usingMock}
      >
        <h2 className="font-mono text-xs text-ink-soft uppercase tracking-wide mb-3">
          {draft.id === null ? 'neuer tag' : `tag bearbeiten · #${draft.id}`}
        </h2>
        <div className="grid grid-cols-1 sm:grid-cols-[1fr_auto_auto] gap-3 items-end">
          <div>
            <label htmlFor="tag-name" className="block font-mono text-xs text-ink-soft mb-1">
              name <span className="text-red-fg">*</span>
            </label>
            <input
              id="tag-name"
              type="text"
              value={draft.name}
              onChange={(e) => setDraft({ ...draft, name: e.target.value })}
              disabled={usingMock}
              maxLength={50}
              className="w-full px-3 py-2 bg-bg border border-border rounded font-mono text-sm focus:outline-none focus:border-ink disabled:opacity-50"
            />
          </div>
          <div>
            <label htmlFor="tag-color" className="block font-mono text-xs text-ink-soft mb-1">
              farbe
            </label>
            <div className="flex items-center gap-2">
              <input
                id="tag-color"
                type="color"
                value={draft.color}
                onChange={(e) => setDraft({ ...draft, color: e.target.value })}
                disabled={usingMock}
                className="h-9 w-12 cursor-pointer rounded border border-border disabled:opacity-50"
              />
              <div className="flex gap-1">
                {PRESET_COLORS.map((c) => (
                  <button
                    key={c}
                    type="button"
                    onClick={() => setDraft({ ...draft, color: c })}
                    disabled={usingMock}
                    aria-label={`Farbe ${c}`}
                    className={`h-6 w-6 rounded border disabled:cursor-not-allowed ${
                      draft.color.toLowerCase() === c ? 'border-ink' : 'border-border'
                    }`}
                    style={{ backgroundColor: c }}
                  />
                ))}
              </div>
            </div>
          </div>
          <div className="flex gap-2 self-end">
            <button
              type="button"
              onClick={handleSave}
              disabled={submitting || !draft.name.trim() || usingMock}
              className="px-3 py-2 bg-ink text-bg font-mono text-xs rounded hover:opacity-90 disabled:opacity-50"
            >
              {submitting ? '...' : draft.id === null ? 'anlegen' : 'speichern'}
            </button>
            {draft.id !== null && (
              <button
                type="button"
                onClick={cancel}
                className="px-3 py-2 font-mono text-xs border border-border rounded text-ink-soft hover:text-ink"
              >
                abbrechen
              </button>
            )}
          </div>
        </div>
      </section>

      {/* List */}
      <section>
        <h2 className="font-mono text-xs text-ink-soft uppercase tracking-wide mb-3">tags</h2>
        {loading ? (
          <div className="space-y-2" role="status" aria-label="tags werden geladen">
            {Array.from({ length: 4 }, (_, i) => (
              <Skeleton key={i} className="h-10 w-full" />
            ))}
          </div>
        ) : tags.length === 0 ? (
          <p className="font-mono text-xs text-ink-soft py-6">noch keine tags angelegt.</p>
        ) : (
          <ul className="border border-border rounded divide-y divide-border">
            {tags.map((t) => (
              <li key={t.id} className="flex items-center justify-between gap-3 px-3 py-2.5">
                <div className="flex items-center gap-3 min-w-0">
                  <span
                    aria-hidden="true"
                    className="inline-block h-3.5 w-3.5 rounded border border-border flex-shrink-0"
                    style={{ backgroundColor: t.color ?? '#cccccc' }}
                  />
                  <span className="font-mono text-sm text-ink truncate">{t.name}</span>
                  <span className="font-mono text-[10px] text-ink-soft">#{t.id}</span>
                </div>
                <div className="flex items-center gap-2 flex-shrink-0">
                  <button
                    type="button"
                    onClick={() => startEdit(t)}
                    disabled={usingMock}
                    title={usingMock ? 'bearbeiten verfügbar mit T038a-Backend' : undefined}
                    className="font-mono text-xs text-ink-soft hover:text-ink border border-border rounded px-2 py-1 disabled:opacity-40 disabled:cursor-not-allowed"
                  >
                    bearbeiten
                  </button>
                  <button
                    type="button"
                    onClick={() => setDeleteId(t.id)}
                    disabled={usingMock}
                    title={usingMock ? 'löschen verfügbar mit T038a-Backend' : undefined}
                    className="font-mono text-xs text-red-fg hover:opacity-80 border border-red-fg/40 rounded px-2 py-1 disabled:opacity-40 disabled:cursor-not-allowed"
                  >
                    löschen
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </section>

      <ConfirmDialog
        open={deleteId !== null}
        title="tag löschen?"
        message="der tag wird unwiderruflich entfernt. zugewiesene bugs behalten ihre einträge in der historie."
        confirmLabel="löschen"
        destructive
        onCancel={() => setDeleteId(null)}
        onConfirm={handleDelete}
        busy={deleting}
      />
    </AppLayout>
  )
}
