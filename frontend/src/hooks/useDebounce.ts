import { useEffect, useState } from 'react'

/**
 * Verzögert das Update eines Werts um `delayMs` Millisekunden. Genutzt für
 * Live-Suche (T060): Tastatureingabe wird gepuffert, bis User pausiert.
 */
export function useDebounce<T>(value: T, delayMs: number): T {
  const [debounced, setDebounced] = useState(value)

  useEffect(() => {
    const handle = window.setTimeout(() => setDebounced(value), delayMs)
    return () => window.clearTimeout(handle)
  }, [value, delayMs])

  return debounced
}
