import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { ActivityTimeline } from './ActivityTimeline'
import type { Activity, Tag, User } from '../../types/bug'

const USERS: User[] = [
  { id: 2, username: 'max2' },
  { id: 3, username: 'admin' },
]

const TAGS: Tag[] = [
  { id: 1, name: 'Backend', color: '#3b82f6' },
  { id: 2, name: 'Frontend', color: '#a855f7' },
]

function act(partial: Partial<Activity>): Activity {
  return {
    id: 1,
    bugId: 1,
    userId: 3,
    userName: 'admin',
    action: 'UPDATED',
    field: null,
    oldValue: null,
    newValue: null,
    createdAt: new Date(2026, 4, 14, 20, 30).toISOString(),
    ...partial,
  }
}

function renderTimeline(activities: Activity[]) {
  return render(
    <ActivityTimeline
      activities={activities}
      loading={false}
      error={null}
      users={USERS}
      tags={TAGS}
    />,
  )
}

describe('<ActivityTimeline>', () => {
  describe('loading + error states', () => {
    it('zeigt Skeleton mit role=status während loading', () => {
      render(
        <ActivityTimeline activities={[]} loading={true} error={null} />,
      )
      expect(screen.getByRole('status')).toBeInTheDocument()
    })

    it('zeigt Error-Text mit role=alert bei error', () => {
      render(
        <ActivityTimeline activities={[]} loading={false} error="oops" />,
      )
      expect(screen.getByRole('alert')).toHaveTextContent(/oops/i)
    })
  })

  describe('empty list', () => {
    it('zeigt Fallback "bug erstellt" wenn reporterName + createdAt gegeben', () => {
      render(
        <ActivityTimeline
          activities={[]}
          loading={false}
          error={null}
          reporterName="alice"
          createdAt={new Date(2026, 4, 14, 10, 0).toISOString()}
        />,
      )
      expect(screen.getByText(/bug erstellt/i)).toBeInTheDocument()
      expect(screen.getByText('alice')).toBeInTheDocument()
    })

    it('zeigt "noch keine aktivitäten" ohne Reporter-Daten', () => {
      render(
        <ActivityTimeline activities={[]} loading={false} error={null} />,
      )
      expect(screen.getByText(/noch keine aktivitäten/i)).toBeInTheDocument()
    })
  })

  describe('field-label mapping + ID resolution', () => {
    it('resolved assigneeid auf username via users-array', () => {
      renderTimeline([
        act({ field: 'assigneeid', oldValue: null, newValue: '2' }),
      ])
      // "bearbeiter geändert: ∅ → max2"
      expect(screen.getByText(/bearbeiter geändert: ∅ → max2/i)).toBeInTheDocument()
    })

    it('resolved tagids auf Tag-Namen', () => {
      renderTimeline([
        act({ field: 'tagids', oldValue: '[1]', newValue: '[1, 2]' }),
      ])
      expect(screen.getByText(/tags geändert: Backend → Backend, Frontend/i)).toBeInTheDocument()
    })

    it('rendert leere Tag-Liste als ∅', () => {
      renderTimeline([
        act({ field: 'tagids', oldValue: '[1]', newValue: '[]' }),
      ])
      expect(screen.getByText(/tags geändert: Backend → ∅/i)).toBeInTheDocument()
    })

    it('fällt auf #ID zurück wenn Tag nicht gefunden', () => {
      renderTimeline([
        act({ field: 'tagids', oldValue: '[]', newValue: '[99]' }),
      ])
      expect(screen.getByText(/tags geändert: ∅ → #99/i)).toBeInTheDocument()
    })

    it('rendert archived=true als "archiviert."', () => {
      renderTimeline([
        act({ field: 'archived', oldValue: 'false', newValue: 'true' }),
      ])
      expect(screen.getByText(/archiviert\./)).toBeInTheDocument()
      expect(screen.queryByText(/→/)).not.toBeInTheDocument()
    })

    it('rendert archived=false als "reaktiviert."', () => {
      renderTimeline([
        act({ field: 'archived', oldValue: 'true', newValue: 'false' }),
      ])
      expect(screen.getByText(/reaktiviert\./)).toBeInTheDocument()
    })

    it('rendert status-Wechsel direkt (kein lookup nötig)', () => {
      renderTimeline([
        act({ field: 'status', oldValue: 'NEU', newValue: 'IN_BEARBEITUNG' }),
      ])
      expect(screen.getByText(/status geändert: NEU → IN_BEARBEITUNG/i)).toBeInTheDocument()
    })

    it('mapped title/description Field-Namen auf deutsche Labels', () => {
      renderTimeline([
        act({ id: 1, field: 'title', oldValue: 'alt', newValue: 'neu' }),
        act({ id: 2, field: 'description', oldValue: 'a', newValue: 'b' }),
      ])
      expect(screen.getByText(/titel geändert: alt → neu/i)).toBeInTheDocument()
      expect(screen.getByText(/beschreibung geändert: a → b/i)).toBeInTheDocument()
    })
  })

  describe('CREATED action', () => {
    it('rendert "bug erstellt." ohne Field-Lookup', () => {
      renderTimeline([
        act({ action: 'CREATED', field: null, oldValue: null, newValue: null }),
      ])
      expect(screen.getByText(/bug erstellt\./i)).toBeInTheDocument()
    })
  })
})
