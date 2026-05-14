import type { ReactNode } from 'react'

/**
 * Wandelt Text in ein Array von ReactNodes um und hebt Matches des
 * `needle` optisch hervor. Case-insensitive. Verwendet in BugTable für die
 * Live-Suche (T060 AC4 — Treffer hervorgehoben).
 */
export function highlight(text: string, needle: string): ReactNode {
  const q = needle.trim()
  if (!q) return text

  const lower = text.toLowerCase()
  const target = q.toLowerCase()
  const out: ReactNode[] = []
  let cursor = 0
  let key = 0

  while (cursor < text.length) {
    const idx = lower.indexOf(target, cursor)
    if (idx === -1) {
      out.push(text.slice(cursor))
      break
    }
    if (idx > cursor) out.push(text.slice(cursor, idx))
    out.push(
      <mark key={`hl-${key++}`} className="bg-accent text-accent-fg rounded px-0.5">
        {text.slice(idx, idx + q.length)}
      </mark>,
    )
    cursor = idx + q.length
  }

  return <>{out}</>
}
