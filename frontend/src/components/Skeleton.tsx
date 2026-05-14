/**
 * Schlanker Skeleton-Block für Loading-States (T055).
 * Tailwind `animate-pulse` reicht — keine extra Animation-Lib.
 */
export function Skeleton({ className = '' }: { className?: string }) {
  return <div className={`animate-pulse rounded bg-surface-2 ${className}`} aria-hidden="true" />
}

export function SkeletonRow({ cells = 4 }: { cells?: number }) {
  return (
    <div className="flex gap-3 py-2">
      {Array.from({ length: cells }, (_, i) => (
        <Skeleton key={i} className="h-4 flex-1" />
      ))}
    </div>
  )
}
