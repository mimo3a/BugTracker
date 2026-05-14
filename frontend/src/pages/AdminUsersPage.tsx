import { useMemo, useState } from 'react'
import { toast } from 'sonner'
import { AppLayout } from '../components/AppLayout'
import { Skeleton } from '../components/Skeleton'
import { useAuth } from '../context/AuthContext'
import { useAdminUsers } from '../hooks/useAdminUsers'
import { ApiError, api } from '../lib/api'
import { formatLocalDate } from '../lib/dates'
import { ROLE_LABELS, USER_ROLES, type UserRole } from '../types/auth'

function describeApiError(err: unknown, fallback: string): string {
  if (err instanceof ApiError) {
    if (err.status === 403) return 'keine berechtigung'
    if (err.status === 404 || err.status === 405)
      return 'backend-endpoint noch nicht verfügbar — feature kommt mit user-PATCH'
    return err.message || fallback
  }
  return err instanceof Error ? err.message : fallback
}

export function AdminUsersPage() {
  const { user: me } = useAuth()
  const { users, loading, error, refresh } = useAdminUsers()
  const [busyId, setBusyId] = useState<number | null>(null)
  const [search, setSearch] = useState('')

  const filteredUsers = useMemo(() => {
    const q = search.trim().toLowerCase()
    if (!q) return users
    return users.filter(
      (u) => u.username.toLowerCase().includes(q) || u.email.toLowerCase().includes(q),
    )
  }, [users, search])

  async function handleRoleChange(userId: number, role: UserRole) {
    setBusyId(userId)
    try {
      await api.updateUser(userId, { role })
      toast.success(`rolle: ${ROLE_LABELS[role]}`)
      refresh()
    } catch (err) {
      toast.error(describeApiError(err, 'rolle konnte nicht geändert werden'))
    } finally {
      setBusyId(null)
    }
  }

  async function handleActiveToggle(userId: number, nextActive: boolean) {
    setBusyId(userId)
    try {
      await api.updateUser(userId, { active: nextActive })
      toast.success(nextActive ? 'account aktiviert' : 'account deaktiviert')
      refresh()
    } catch (err) {
      toast.error(describeApiError(err, 'status konnte nicht geändert werden'))
    } finally {
      setBusyId(null)
    }
  }

  return (
    <AppLayout width="4xl">
      <header className="flex items-center justify-between mb-5 pb-4 border-b border-border">
        <div>
          <h1 className="font-mono text-2xl font-bold text-ink">admin · users</h1>
          <p className="font-mono text-xs text-ink-soft mt-1">/api/users</p>
        </div>
      </header>

      {error && (
        <p className="font-mono text-xs text-red-fg mb-3" role="alert">
          {error}
        </p>
      )}

      <p className="font-mono text-[11px] text-ink-soft mb-3">
        hinweis: rollen- und aktiv-updates erfordern PATCH /api/users/{'{id}'} (T038b) —
        falls noch nicht implementiert, kommt ein 404-toast.
      </p>

      <div className="mb-3">
        <input
          type="search"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="suche user/email…"
          aria-label="User suchen"
          className="h-8 w-64 px-3 bg-surface border border-border rounded font-mono text-xs text-ink placeholder:text-ink-soft focus:outline-none focus:border-ink"
        />
      </div>

      {loading ? (
        <div className="space-y-2" role="status" aria-label="user werden geladen">
          {Array.from({ length: 4 }, (_, i) => (
            <Skeleton key={i} className="h-12 w-full" />
          ))}
        </div>
      ) : filteredUsers.length === 0 ? (
        <p className="font-mono text-xs text-ink-soft py-6">
          {users.length === 0 ? 'keine user gefunden.' : 'kein treffer für die suche.'}
        </p>
      ) : (
        <div className="overflow-x-auto border border-border rounded">
          <table className="w-full font-mono border-collapse">
            <thead>
              <tr className="bg-surface-2">
                <th className="text-left px-3 py-2.5 text-[11px] font-semibold uppercase tracking-[0.6px] text-ink-soft border-b border-border w-20">
                  ID
                </th>
                <th className="text-left px-3 py-2.5 text-[11px] font-semibold uppercase tracking-[0.6px] text-ink-soft border-b border-border">
                  Username
                </th>
                <th className="text-left px-3 py-2.5 text-[11px] font-semibold uppercase tracking-[0.6px] text-ink-soft border-b border-border">
                  Email
                </th>
                <th className="text-left px-3 py-2.5 text-[11px] font-semibold uppercase tracking-[0.6px] text-ink-soft border-b border-border w-44">
                  Rolle
                </th>
                <th className="text-left px-3 py-2.5 text-[11px] font-semibold uppercase tracking-[0.6px] text-ink-soft border-b border-border w-24">
                  Aktiv
                </th>
                <th className="text-left px-3 py-2.5 text-[11px] font-semibold uppercase tracking-[0.6px] text-ink-soft border-b border-border w-28">
                  Erstellt
                </th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.map((u) => {
                const isSelf = me?.id === u.id
                return (
                  <tr key={u.id} className="hover:bg-surface-2 transition-colors">
                    <td className="px-3 py-2.5 border-b border-border text-[12px] text-ink-soft">
                      {u.id}
                    </td>
                    <td className="px-3 py-2.5 border-b border-border text-[12px] text-ink">
                      {u.username}
                      {isSelf && (
                        <span className="ml-2 text-[10px] text-amber-fg">(du)</span>
                      )}
                    </td>
                    <td className="px-3 py-2.5 border-b border-border text-[12px] text-ink-soft">
                      {u.email}
                    </td>
                    <td className="px-3 py-2.5 border-b border-border">
                      <select
                        value={u.role}
                        disabled={isSelf || busyId === u.id}
                        onChange={(e) => handleRoleChange(u.id, e.target.value as UserRole)}
                        aria-label={`Rolle für ${u.username}`}
                        className="h-8 px-2 bg-surface border border-border rounded font-mono text-xs focus:outline-none focus:border-ink disabled:opacity-50 disabled:cursor-not-allowed"
                      >
                        {USER_ROLES.map((r) => (
                          <option key={r} value={r}>
                            {ROLE_LABELS[r]}
                          </option>
                        ))}
                      </select>
                    </td>
                    <td className="px-3 py-2.5 border-b border-border">
                      <label className="inline-flex items-center gap-1.5 cursor-pointer select-none">
                        <input
                          type="checkbox"
                          checked={u.active}
                          disabled={isSelf || busyId === u.id}
                          onChange={(e) => handleActiveToggle(u.id, e.target.checked)}
                          aria-label={`${u.username} ${u.active ? 'deaktivieren' : 'aktivieren'}`}
                          className="accent-ink disabled:opacity-50 disabled:cursor-not-allowed"
                        />
                        <span
                          className={`font-mono text-[11px] ${
                            u.active ? 'text-ink' : 'text-ink-soft'
                          }`}
                        >
                          {u.active ? 'aktiv' : 'inaktiv'}
                        </span>
                      </label>
                    </td>
                    <td className="px-3 py-2.5 border-b border-border text-[12px] text-ink-soft">
                      {formatLocalDate(u.createdAt)}
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      )}

      <p className="font-mono text-[10px] text-ink-soft mt-3">
        Eigener Account ist gegen versehentliche Selbst-Degradierung geschützt (Lock-out-Schutz).
      </p>
    </AppLayout>
  )
}
