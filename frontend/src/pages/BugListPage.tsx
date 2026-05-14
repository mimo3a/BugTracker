import { Link, useSearchParams } from 'react-router-dom'
import { BugTable } from '../components/bugs/BugTable'
import { FilterBar } from '../components/bugs/FilterBar'
import { Pagination } from '../components/Pagination'
import { AppLayout } from '../components/AppLayout'
import { useBugFilters } from '../hooks/useBugFilters'
import { useBugs, useFilterOptions } from '../hooks/useBugs'

export function BugListPage() {
  const { filters, setFilters, reset, isDirty } = useBugFilters()
  const { users, tags, usingMockTags } = useFilterOptions()

  const [searchParams, setSearchParams] = useSearchParams()
  const pageRaw = searchParams.get('page')
  const page = pageRaw && /^\d+$/.test(pageRaw) ? Math.max(1, Number(pageRaw)) : 1

  const { bugs, total, pageSize, loading, usingMock, error } = useBugs(filters, page)

  function setPage(next: number) {
    const params = new URLSearchParams(searchParams)
    if (next <= 1) params.delete('page')
    else params.set('page', String(next))
    setSearchParams(params, { replace: false })
  }

  // setFilters schreibt die URL komplett neu (ohne page) — kein zweiter
  // setSearchParams-Call hier, sonst Race in React's batching (last write wins).

  return (
    <AppLayout>
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
        <Link
          to="/bugs/new"
          className="font-mono text-xs bg-ink text-bg rounded px-3 py-1.5 hover:opacity-90"
        >
          + new bug
        </Link>
      </header>

      {usingMockTags && (
        <p className="font-mono text-[11px] text-amber-fg mb-2" role="status">
          <span className="inline-block w-1.5 h-1.5 rounded-full bg-amber-fg mr-1.5 align-middle" />
          tags zeigen mock-daten · /api/tags antwortet nicht
        </p>
      )}

      <FilterBar
        filters={filters}
        onChange={setFilters}
        onReset={reset}
        isDirty={isDirty}
        users={users}
        tags={tags}
      />

      {error && (
        <p className="font-mono text-xs text-red-fg mb-3" role="alert">
          {error}
        </p>
      )}

      <BugTable bugs={bugs} loading={loading} searchQuery={filters.search} />

      <footer className="mt-4 flex items-center justify-between font-mono text-xs text-ink-soft">
        <span>
          {total} {total === 1 ? 'bug' : 'bugs'}
          {total > pageSize && (
            <span>
              {' · '}seite {page} / {Math.max(1, Math.ceil(total / pageSize))}
            </span>
          )}
        </span>
        <Pagination page={page} pageSize={pageSize} total={total} onPageChange={setPage} />
        {isDirty && <span>filters active · share URL to share view</span>}
      </footer>
    </AppLayout>
  )
}
