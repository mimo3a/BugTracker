import { useState, type FormEvent, type KeyboardEvent } from 'react'
import { toast } from 'sonner'
import { ApiError, api } from '../../lib/api'
import { useComments } from '../../hooks/useComments'
import { formatLocalDateTime } from '../../lib/dates'
import { Skeleton } from '../Skeleton'

interface Props {
  bugId: number
}

/**
 * Kommentar-Liste + Eingabe (T062). Plain-Text-Rendering via
 * `whitespace-pre-wrap` — HTML-Tags werden als Text dargestellt (AC).
 */
export function CommentsSection({ bugId }: Props) {
  const { comments, loading, error, refresh } = useComments(bugId)
  const [draft, setDraft] = useState('')
  const [submitting, setSubmitting] = useState(false)

  function handleKeyDown(e: KeyboardEvent<HTMLTextAreaElement>) {
    // Cmd+Enter (mac) bzw. Ctrl+Enter (win/linux) submitted das Textarea direkt.
    if (e.key === 'Enter' && (e.metaKey || e.ctrlKey)) {
      e.preventDefault()
      void handleSubmit()
    }
  }

  async function handleSubmit(e?: FormEvent) {
    e?.preventDefault()
    const content = draft.trim()
    if (!content) return
    setSubmitting(true)
    try {
      await api.createComment(bugId, { content })
      setDraft('')
      refresh()
      toast.success('kommentar hinzugefügt')
    } catch (err) {
      const msg =
        err instanceof ApiError
          ? err.message
          : err instanceof Error
            ? err.message
            : 'Kommentar fehlgeschlagen'
      toast.error(msg)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section aria-label="Kommentare">
      <h2 className="font-mono text-sm font-bold text-ink mb-3">kommentare</h2>

      {loading && (
        <div className="space-y-3 mb-4" role="status" aria-label="kommentare werden geladen">
          <Skeleton className="h-4 w-2/3" />
          <Skeleton className="h-4 w-1/2" />
        </div>
      )}

      {error && !loading && (
        <p className="font-mono text-xs text-red-fg mb-3" role="alert">
          kommentare konnten nicht geladen werden ({error})
        </p>
      )}

      {!loading && !error && comments.length === 0 && (
        <p className="font-mono text-xs text-ink-soft mb-4">noch keine kommentare.</p>
      )}

      {!loading && comments.length > 0 && (
        <ul className="space-y-3 mb-4">
          {comments.map((c) => (
            <li key={c.id} className="border border-border rounded p-3 bg-surface">
              <p className="font-mono text-xs text-ink-soft mb-1">
                <span className="text-ink font-semibold">{c.userName}</span>
                <span className="mx-1.5">·</span>
                <time dateTime={c.createdAt}>{formatLocalDateTime(c.createdAt)}</time>
              </p>
              <p className="font-mono text-xs text-ink whitespace-pre-wrap break-words">
                {c.content}
              </p>
            </li>
          ))}
        </ul>
      )}

      <form onSubmit={handleSubmit} className="space-y-2">
        <label htmlFor="new-comment" className="block font-mono text-xs text-ink-soft">
          neuer kommentar
        </label>
        <textarea
          id="new-comment"
          rows={3}
          value={draft}
          onChange={(e) => setDraft(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="kommentar schreiben… (cmd/ctrl+enter zum senden)"
          className="w-full px-3 py-2 bg-bg border border-border rounded font-mono text-xs focus:outline-none focus:border-ink resize-y"
        />
        <button
          type="submit"
          disabled={submitting || !draft.trim()}
          className="font-mono text-xs bg-ink text-bg rounded px-3 py-1.5 hover:opacity-90 disabled:opacity-50"
        >
          {submitting ? '...' : 'senden'}
        </button>
      </form>
    </section>
  )
}
