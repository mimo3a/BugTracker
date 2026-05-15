import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { AppLayout } from '../components/AppLayout'
import { BugForm, type BugFormValues } from '../components/bugs/BugForm'
import { ApiError, api } from '../lib/api'
import { useTags } from '../hooks/useTags'
import { BUG_PRIORITIES, PRIORITY_LABELS, type BugPriority } from '../types/bug'

export function BugCreatePage() {
  const navigate = useNavigate()
  const { tags } = useTags()
  const [priority, setPriority] = useState<BugPriority>('MITTEL')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(values: BugFormValues) {
    setError(null)
    setSubmitting(true)
    try {
      const created = await api.createBug({
        title: values.title,
        description: values.description,
        priority,
        tagIds: values.tagIds,
      })
      toast.success(`bug BUG-${created.id} angelegt`)
      navigate(`/bugs/${created.id}`, { replace: true })
    } catch (err) {
      let msg: string
      if (err instanceof ApiError && err.status === 400) {
        msg = err.message || 'Eingaben unvollständig'
      } else if (err instanceof ApiError && err.status === 401) {
        msg = 'Session abgelaufen — bitte neu einloggen'
      } else {
        msg = err instanceof Error ? err.message : 'Bug konnte nicht angelegt werden'
      }
      setError(msg)
      toast.error('bug konnte nicht angelegt werden')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AppLayout width="2xl">
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

      <BugForm
        tags={tags}
        submitLabel="create"
        submitting={submitting}
        error={error}
        onSubmit={handleSubmit}
        onCancel={() => navigate('/bugs')}
        extraFields={
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
        }
      />
    </AppLayout>
  )
}
