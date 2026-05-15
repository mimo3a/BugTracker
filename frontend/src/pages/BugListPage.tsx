import { Link } from 'react-router-dom'
import { BugTable } from '../components/bugs/BugTable'
import { FilterBar } from '../components/bugs/FilterBar'
import { ThemeToggle } from '../components/ThemeToggle'
import { useAuth } from '../context/AuthContext'
import { useBugFilters } from '../hooks/useBugFilters'
import { useBugs, useFilterOptions } from '../hooks/useBugs'

export function BugListPage() {
  const { user, logout } = useAuth()
  const { filters, setFilters, reset, isDirty } = useBugFilters()
  const { users, tags } = useFilterOptions()
  const { bugs, total, loading, usingMock } = useBugs(filters)

  return (
    <div className="min-h-screen bg-bg text-ink">
      <main className="mx-auto max-w-7xl px-6 py-6">
        <header className="flex items-center justify-between mb-5 pb-4 border-b border-border">
          <div>
            <h1 className="font-mono text-2xl font-bold text-ink">bugs</h1>
            <p className="font-mono text-xs text-ink-soft mt-1">
              {usingMock ? (
                <>
                  <span className="inline-block w-1.5 h-1.5 rounded-full bg-amber-fg mr-1.5 align-middle" />
                  mock data · backend offline
                </>
              ) : (
                <>
                  <span className="inline-block w-1.5 h-1.5 rounded-full bg-green-fg mr-1.5 align-middle" />
                  live · /api/bugs
                </>
              )}
            </p>
          </div>
          <div className="flex items-center gap-3">
            <Link
              to="/bugs/new"
              className="font-mono text-xs bg-ink text-bg rounded px-2 py-1 hover:opacity-90"
            >
              + new
            </Link>
            {user && (
              <span className="font-mono text-xs text-ink-soft">
                {user.username} · <span className="lowercase">{user.role}</span>
              </span>
            )}
            <button
              type="button"
              onClick={logout}
              className="font-mono text-xs text-ink-soft hover:text-ink border border-border rounded px-2 py-1"
            >
              logout
            </button>
            <ThemeToggle />
          </div>
        </header>

        <FilterBar
          filters={filters}
          onChange={setFilters}
          onReset={reset}
          isDirty={isDirty}
          users={users}
          tags={tags}
        />

        <BugTable bugs={bugs} loading={loading} />

        <footer className="mt-4 flex justify-between font-mono text-xs text-ink-soft">
          <span>
            {total} {total === 1 ? 'bug' : 'bugs'}
          </span>
          {isDirty && <span>filters active · share URL to share view</span>}
        </footer>
      </main>
    </div>
  )
}
