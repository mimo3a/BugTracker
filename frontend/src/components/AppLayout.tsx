import { Link, NavLink, useLocation, useNavigate } from 'react-router-dom'
import { useEffect, useState, type ReactNode } from 'react'
import { toast } from 'sonner'
import { useAuth } from '../context/AuthContext'
import { ThemeToggle } from './ThemeToggle'

interface AppLayoutProps {
  children: ReactNode
  /** Optionaler max-width-Container für die main-Section. Default: 7xl. */
  width?: 'sm' | 'md' | 'lg' | 'xl' | '2xl' | '4xl' | '7xl'
}

const widthClass: Record<NonNullable<AppLayoutProps['width']>, string> = {
  sm: 'max-w-sm',
  md: 'max-w-md',
  lg: 'max-w-lg',
  xl: 'max-w-xl',
  '2xl': 'max-w-2xl',
  '4xl': 'max-w-4xl',
  '7xl': 'max-w-7xl',
}

const linkBase =
  'font-mono text-xs px-2 py-1 rounded border border-transparent hover:border-border hover:text-ink'
const linkActive = 'text-ink border-border bg-surface-2'
const linkInactive = 'text-ink-soft'

export function AppLayout({ children, width = '7xl' }: AppLayoutProps) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const isAdmin = user?.role === 'ADMIN'
  const [menuOpen, setMenuOpen] = useState(false)

  // Menü beim Routenwechsel schließen, damit das Panel nicht „hängen" bleibt.
  useEffect(() => setMenuOpen(false), [location.pathname])

  async function handleLogout() {
    try {
      await logout()
      toast.success('logout erfolgreich')
      navigate('/login', { replace: true })
    } catch {
      toast.error('logout fehlgeschlagen')
    }
  }

  const navLinks = (
    <>
      <NavLink
        to="/bugs"
        className={({ isActive }) => `${linkBase} ${isActive ? linkActive : linkInactive}`}
      >
        bugs
      </NavLink>
      {isAdmin && (
        <>
          <NavLink
            to="/admin/users"
            className={({ isActive }) => `${linkBase} ${isActive ? linkActive : linkInactive}`}
          >
            admin/users
          </NavLink>
          <NavLink
            to="/admin/tags"
            className={({ isActive }) => `${linkBase} ${isActive ? linkActive : linkInactive}`}
          >
            admin/tags
          </NavLink>
        </>
      )}
    </>
  )

  const userControls = (
    <>
      {user && (
        <span className="font-mono text-xs text-ink-soft">
          {user.username} · <span className="lowercase">{user.role}</span>
        </span>
      )}
      <button
        type="button"
        onClick={handleLogout}
        className="font-mono text-xs text-ink-soft hover:text-ink border border-border rounded px-2 py-1"
      >
        logout
      </button>
      <ThemeToggle />
    </>
  )

  return (
    <div className="min-h-screen bg-bg text-ink">
      <header className="border-b border-border bg-surface">
        <div className="mx-auto max-w-7xl px-6">
          <div className="h-14 flex items-center justify-between gap-4">
            <div className="flex items-center gap-4">
              <Link to="/bugs" className="font-mono text-sm font-bold text-ink hover:opacity-80">
                <span className="inline-block w-6 h-6 bg-accent text-accent-fg text-center mr-2 rounded">
                  &gt;
                </span>
                bug-tracker
              </Link>
              <nav className="hidden md:flex items-center gap-1" aria-label="Hauptnavigation">
                {navLinks}
              </nav>
            </div>
            <div className="hidden md:flex items-center gap-3">{userControls}</div>
            <button
              type="button"
              onClick={() => setMenuOpen((v) => !v)}
              aria-label="menu"
              aria-expanded={menuOpen}
              aria-controls="mobile-nav"
              className="md:hidden h-8 w-8 inline-flex items-center justify-center border border-border rounded font-mono text-sm text-ink hover:bg-surface-2"
            >
              {menuOpen ? '✕' : '☰'}
            </button>
          </div>
          {menuOpen && (
            <div id="mobile-nav" className="md:hidden border-t border-border py-3 space-y-3">
              <nav className="flex flex-col items-stretch gap-1" aria-label="Hauptnavigation mobil">
                {navLinks}
              </nav>
              <div className="flex items-center justify-between gap-3 flex-wrap pt-3 border-t border-border">
                {userControls}
              </div>
            </div>
          )}
        </div>
      </header>

      <main className={`mx-auto ${widthClass[width]} px-6 py-6`}>{children}</main>
    </div>
  )
}
