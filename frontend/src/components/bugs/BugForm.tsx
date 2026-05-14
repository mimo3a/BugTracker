import { useState, type FormEvent, type ReactNode } from 'react'
import type { Tag } from '../../types/bug'

const TITLE_MAX = 255

export interface BugFormValues {
  title: string
  description: string
  tagIds: number[]
}

interface BugFormProps {
  initial?: Partial<BugFormValues>
  tags: Tag[]
  submitLabel: string
  submitting: boolean
  error: string | null
  /** Wird über die Standard-Felder gerendert (z.B. Archive-Warnung). */
  topAlert?: ReactNode
  /** Wird zwischen Description und Tag-Liste gerendert (z.B. Priority-Dropdown). */
  extraFields?: ReactNode
  onSubmit: (values: BugFormValues) => void | Promise<void>
  onCancel: () => void
}

/**
 * Gemeinsame Form-Struktur für BugCreate und BugEdit. Title + Description +
 * Tags + optionale Extras + Submit/Cancel.
 */
export function BugForm({
  initial,
  tags,
  submitLabel,
  submitting,
  error,
  topAlert,
  extraFields,
  onSubmit,
  onCancel,
}: BugFormProps) {
  const [title, setTitle] = useState(initial?.title ?? '')
  const [description, setDescription] = useState(initial?.description ?? '')
  const [tagIds, setTagIds] = useState<number[]>(initial?.tagIds ?? [])

  function toggleTag(id: number) {
    setTagIds((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]))
  }

  function handleSubmit(e: FormEvent) {
    e.preventDefault()
    onSubmit({ title: title.trim(), description: description.trim(), tagIds })
  }

  const canSubmit = title.trim().length > 0 && description.trim().length > 0 && !submitting

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {topAlert}

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

      {extraFields}

      <div>
        <span className="block font-mono text-xs text-ink-soft mb-1">tags</span>
        <div className="flex flex-wrap gap-2">
          {tags.length === 0 && (
            <span className="font-mono text-xs text-ink-soft italic">keine tags verfügbar.</span>
          )}
          {tags.map((tag) => {
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

      {error && (
        <p className="font-mono text-xs text-red-fg" role="alert">
          {error}
        </p>
      )}

      <div className="flex gap-2 pt-2">
        <button
          type="submit"
          disabled={!canSubmit}
          className="px-4 py-2 bg-ink text-bg font-mono text-sm rounded hover:opacity-90 disabled:opacity-50"
        >
          {submitting ? '...' : submitLabel}
        </button>
        <button
          type="button"
          onClick={onCancel}
          className="px-4 py-2 font-mono text-sm border border-border rounded text-ink-soft hover:text-ink"
        >
          cancel
        </button>
      </div>
    </form>
  )
}
