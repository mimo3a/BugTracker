import { BugTable } from '../components/bugs/BugTable'
import { FilterBar } from '../components/bugs/FilterBar'
import { ThemeToggle } from '../components/ThemeToggle'
import { useBugFilters } from '../hooks/useBugFilters'
import { useBugs, useFilterOptions } from '../hooks/useBugs'

export function BugListPage() {
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
          <ThemeToggle />
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
