import { act, render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, useLocation } from 'react-router-dom'
import { describe, expect, it } from 'vitest'
import { useBugFilters } from './useBugFilters'

// Test-Harness, der die Hook-Ausgaben in den DOM rendert, damit wir mit
// userEvent + Assertions arbeiten können.
function Harness() {
  const { filters, setFilters, reset, isDirty } = useBugFilters()
  const location = useLocation()

  return (
    <div>
      <pre data-testid="filters">{JSON.stringify(filters)}</pre>
      <pre data-testid="search-params">{location.search}</pre>
      <span data-testid="dirty">{isDirty ? 'dirty' : 'clean'}</span>
      <button
        onClick={() =>
          setFilters({
            status: ['NEU'],
            priority: 'HOCH',
            tagId: 3,
            assigneeId: 5,
            search: 'login',
            archived: true,
          })
        }
      >
        set-all
      </button>
      <button onClick={reset}>reset</button>
    </div>
  )
}

function renderWith(initialUrl: string) {
  return render(
    <MemoryRouter initialEntries={[initialUrl]}>
      <Harness />
    </MemoryRouter>,
  )
}

describe('useBugFilters', () => {
  describe('URL → filters parsing', () => {
    it('liest leere URL als EMPTY_FILTERS', () => {
      renderWith('/bugs')
      expect(JSON.parse(screen.getByTestId('filters').textContent!)).toEqual({
        status: [],
        priority: '',
        tagId: null,
        assigneeId: null,
        search: '',
        archived: false,
      })
      expect(screen.getByTestId('dirty').textContent).toBe('clean')
    })

    it('parsed multi-status + priority + tag + search + archived', () => {
      renderWith('/bugs?status=NEU&status=IM_REVIEW&priority=KRITISCH&tagId=2&search=login&archived=true')
      const filters = JSON.parse(screen.getByTestId('filters').textContent!)
      expect(filters.status).toEqual(['NEU', 'IM_REVIEW'])
      expect(filters.priority).toBe('KRITISCH')
      expect(filters.tagId).toBe(2)
      expect(filters.search).toBe('login')
      expect(filters.archived).toBe(true)
      expect(screen.getByTestId('dirty').textContent).toBe('dirty')
    })

    it('ignoriert unbekannte Status- und Priority-Werte', () => {
      renderWith('/bugs?status=NONSENSE&priority=BOGUS')
      const filters = JSON.parse(screen.getByTestId('filters').textContent!)
      expect(filters.status).toEqual([])
      expect(filters.priority).toBe('')
    })

    it('ignoriert nicht-numerische tagId/assigneeId', () => {
      renderWith('/bugs?tagId=abc&assigneeId=xyz')
      const filters = JSON.parse(screen.getByTestId('filters').textContent!)
      expect(filters.tagId).toBeNull()
      expect(filters.assigneeId).toBeNull()
    })
  })

  describe('setFilters / reset', () => {
    it('setFilters schreibt URL korrekt', async () => {
      renderWith('/bugs')
      await act(async () => {
        await userEvent.click(screen.getByText('set-all'))
      })
      const search = screen.getByTestId('search-params').textContent ?? ''
      expect(search).toContain('status=NEU')
      expect(search).toContain('priority=HOCH')
      expect(search).toContain('tagId=3')
      expect(search).toContain('assigneeId=5')
      expect(search).toContain('search=login')
      expect(search).toContain('archived=true')
    })

    it('reset leert die URL', async () => {
      renderWith('/bugs?status=NEU&priority=HOCH')
      expect(screen.getByTestId('dirty').textContent).toBe('dirty')
      await act(async () => {
        await userEvent.click(screen.getByText('reset'))
      })
      expect(screen.getByTestId('search-params').textContent).toBe('')
      expect(screen.getByTestId('dirty').textContent).toBe('clean')
    })
  })

  describe('isDirty', () => {
    it('whitespace-only search ist nicht dirty', () => {
      renderWith('/bugs?search=%20%20')
      expect(screen.getByTestId('dirty').textContent).toBe('clean')
    })

    it('archived=false ist nicht dirty', () => {
      renderWith('/bugs?archived=false')
      expect(screen.getByTestId('dirty').textContent).toBe('clean')
    })
  })
})
