import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes, useLocation } from 'react-router-dom'
import { describe, expect, it } from 'vitest'
import { BugTable } from './BugTable'
import type { Bug } from '../../types/bug'

const sampleBug: Bug = {
  id: 42,
  title: 'Login button defekt',
  description: 'tritt nur in Firefox auf',
  status: 'NEU',
  priority: 'HOCH',
  reporterId: 1,
  reporterName: 'jane',
  assigneeId: null,
  assigneeName: null,
  tagIds: [],
  tagNames: [],
  archived: false,
  createdAt: '2026-01-15T10:00:00Z',
  updatedAt: '2026-01-15T10:00:00Z',
}

function LocationProbe() {
  const loc = useLocation()
  return <p data-testid="loc">{loc.pathname}</p>
}

describe('<BugTable>', () => {
  it('zeigt den leeren Zustand mit CTA, wenn keine Bugs vorhanden', () => {
    render(
      <MemoryRouter>
        <BugTable bugs={[]} loading={false} />
      </MemoryRouter>,
    )
    expect(screen.getByText(/keine bugs gefunden/i)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /new bug/i })).toBeInTheDocument()
  })

  it('navigiert beim Klick auf eine Zeile zur Bug-Detail-Seite', async () => {
    render(
      <MemoryRouter initialEntries={['/bugs']}>
        <Routes>
          <Route path="/bugs" element={<BugTable bugs={[sampleBug]} loading={false} />} />
          <Route path="/bugs/:id" element={<LocationProbe />} />
        </Routes>
      </MemoryRouter>,
    )

    await userEvent.click(screen.getByRole('link', { name: /Bug 42/i }))
    expect(screen.getByTestId('loc').textContent).toBe('/bugs/42')
  })

  it('hebt den Suchbegriff im Titel hervor', () => {
    const { container } = render(
      <MemoryRouter>
        <BugTable bugs={[sampleBug]} loading={false} searchQuery="login" />
      </MemoryRouter>,
    )
    const mark = container.querySelector('mark')
    expect(mark).not.toBeNull()
    expect(mark!.textContent).toBe('Login')
  })
})
