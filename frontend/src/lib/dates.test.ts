import { describe, expect, it } from 'vitest'
import { formatLocalDate, formatLocalDateTime } from './dates'

// Tests sind TZ-unabhängig: wir bauen Date mit lokalem Konstruktor und
// erwarten Round-Trip via ISO-String zurück zur selben lokalen Anzeige.

describe('dates', () => {
  describe('formatLocalDate', () => {
    it('round-trip ergibt YYYY-MM-DD in lokaler Zeit', () => {
      const local = new Date(2026, 4, 14, 22, 30) // 14.05.2026 22:30 lokal
      expect(formatLocalDate(local.toISOString())).toBe('2026-05-14')
    })

    it('paddet einstelligen Monat und Tag', () => {
      const local = new Date(2026, 0, 5, 12, 0) // 05.01.2026
      expect(formatLocalDate(local.toISOString())).toBe('2026-01-05')
    })

    it('würde bei toISOString().slice(0,10) den Tag verschieben — hier nicht', () => {
      // 23:30 lokal → toISOString liefert je nach TZ +1h-6h → in UTC könnte
      // der Tag schon "+1" sein. formatLocalDate muss trotzdem den lokalen Tag liefern.
      const local = new Date(2026, 4, 14, 23, 30)
      expect(formatLocalDate(local.toISOString())).toBe('2026-05-14')
    })
  })

  describe('formatLocalDateTime', () => {
    it('round-trip ergibt YYYY-MM-DD HH:MM in lokaler Zeit', () => {
      const local = new Date(2026, 4, 14, 20, 30)
      expect(formatLocalDateTime(local.toISOString())).toBe('2026-05-14 20:30')
    })

    it('paddet einstellige Stunden und Minuten', () => {
      const local = new Date(2026, 4, 14, 9, 5)
      expect(formatLocalDateTime(local.toISOString())).toBe('2026-05-14 09:05')
    })
  })
})
