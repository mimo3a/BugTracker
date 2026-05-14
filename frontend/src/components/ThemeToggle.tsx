import { useEffect, useState } from 'react'

type Theme = 'light' | 'dark'

function readInitial(): Theme {
  try {
    const stored = localStorage.getItem('theme')
    if (stored === 'dark' || stored === 'light') return stored
  } catch {
    // localStorage blocked (private mode, etc.) → fall through to OS pref
  }
  if (typeof window !== 'undefined' && window.matchMedia?.('(prefers-color-scheme: dark)').matches) {
    return 'dark'
  }
  return 'light'
}

export function ThemeToggle() {
  const [theme, setTheme] = useState<Theme>(readInitial)

  useEffect(() => {
    document.documentElement.classList.toggle('dark', theme === 'dark')
    try {
      localStorage.setItem('theme', theme)
    } catch {
      /* ignore */
    }
  }, [theme])

  return (
    <button
      type="button"
      onClick={() => setTheme((t) => (t === 'dark' ? 'light' : 'dark'))}
      aria-label="Toggle dark mode"
      className="h-8 px-3 border border-border rounded font-mono text-xs text-ink hover:bg-surface-2"
    >
      {theme === 'dark' ? '☾ dark' : '☀ light'}
    </button>
  )
}
