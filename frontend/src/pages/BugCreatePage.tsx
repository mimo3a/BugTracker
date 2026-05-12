import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { ApiError, api } from '../lib/api'
import { SEED_TAGS } from '../lib/seedTags'
import { BUG_PRIORITIES, PRIORITY_LABELS, type BugPriority } from '../types/bug'

const TITLE_MAX = 255

export function BugCreatePage() {
  const navigate = useNavigate()
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [priority, setPriority] = useState<BugPriority>('MITTEL')
  const [tagIds, setTagIds] = useState<number[]>([])
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  function toggleTag(id: number) {
    setTagIds((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]))
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      await api.createBug({ title: title.trim(), description: description.trim(), priority, tagIds })
      navigate('/bugs', { replace: true })
    } catch (err) {
      if (err instanceof ApiError && err.status === 400) {
        setError(err.message || 'Eingaben unvollständig')
      } else if (err instanceof ApiError && err.status === 401) {
        setError('Session abgelaufen — bitte neu einloggen')
      } else {
        setError(err instanceof Error ? err.message : 'Bug konnte nicht angelegt werden')
      }
    } finally {
      setSubmitting(false)
    }
  }

  const canSubmit = title.trim().length > 0 && description.trim().length > 0 && !submitting

  return (
    <div className="min-h-screen bg-bg text-ink">
      <main className="mx-auto max-w-2xl px-6 py-6">
        <header className="flex items-center justify-between mb-5 pb-4 border-b border-border">
          <div>
            <h1 className="font-mono text-2xl font-bold text-ink">new bug</h1>
            <p className="font-mono text-xs text-ink-soft mt-1">POST /api/bugs</p>
          </div>
          <button
            type="button"
            onClick={() => navigate('/bugs')}
            className="font-mono text-xs text-ink-soft hover:text-ink border border-border rounded px-2 py-1"
          >
            cancel
          </button>
        </header>

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
            <label htmlFor="priority" className="block font-mono text-xs text-ink-soft mb-1">
              priority
            </label>
            <select
              id="priority"
              value={priority}
              onChange={(e) => setPriority(e.target.value as BugPriority)}
              className="w-full px-3 py-2 bg-bg border border-border rounded font-mono text-sm focus:outline-none focus:border-ink"
            >
              {BUG_PRIORITIES.map((p) => (
                <option key={p} value={p}>
                  {PRIORITY_LABELS[p]}
                </option>
              ))}
            </select>
          </div>

          <div>
            <span className="block font-mono text-xs text-ink-soft mb-1">tags</span>
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
              {submitting ? '...' : 'create'}
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
