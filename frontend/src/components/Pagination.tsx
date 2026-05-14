interface PaginationProps {
  /** 1-based aktuelle Seite. */
  page: number
  pageSize: number
  total: number
  onPageChange: (page: number) => void
}

/**
 * Schlanke Pagination für die Bug-Liste (T046 AC: Pagination funktioniert).
 * Zeigt < 1 ... 4 5 6 ... N > — kompakt, ohne extra Lib.
 */
export function Pagination({ page, pageSize, total, onPageChange }: PaginationProps) {
  const totalPages = Math.max(1, Math.ceil(total / pageSize))
  if (totalPages <= 1) return null

  const window: number[] = []
  const lower = Math.max(1, page - 1)
  const upper = Math.min(totalPages, page + 1)
  for (let i = lower; i <= upper; i++) window.push(i)

  const btn =
    'h-7 min-w-7 px-2 border border-border rounded font-mono text-xs hover:bg-surface-2 disabled:opacity-40 disabled:cursor-not-allowed'
  const activeBtn = 'h-7 min-w-7 px-2 border border-ink bg-ink text-bg rounded font-mono text-xs'

  return (
    <nav className="flex items-center gap-1.5" aria-label="Pagination">
      <button
        type="button"
        className={btn}
        disabled={page <= 1}
        onClick={() => onPageChange(page - 1)}
        aria-label="vorherige Seite"
      >
        ‹
      </button>

      {window[0]! > 1 && (
        <>
          <button type="button" className={btn} onClick={() => onPageChange(1)}>
            1
          </button>
          {window[0]! > 2 && <span className="font-mono text-xs text-ink-soft px-1">…</span>}
        </>
      )}

      {window.map((p) => (
        <button
          key={p}
          type="button"
          className={p === page ? activeBtn : btn}
          onClick={() => onPageChange(p)}
          aria-current={p === page ? 'page' : undefined}
        >
          {p}
        </button>
      ))}

      {window[window.length - 1]! < totalPages && (
        <>
          {window[window.length - 1]! < totalPages - 1 && (
            <span className="font-mono text-xs text-ink-soft px-1">…</span>
          )}
          <button type="button" className={btn} onClick={() => onPageChange(totalPages)}>
            {totalPages}
          </button>
        </>
      )}

      <button
        type="button"
        className={btn}
        disabled={page >= totalPages}
        onClick={() => onPageChange(page + 1)}
        aria-label="nächste Seite"
      >
        ›
      </button>
    </nav>
  )
}
