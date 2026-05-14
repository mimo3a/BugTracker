import { act, renderHook } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { useDebounce } from './useDebounce'

describe('useDebounce()', () => {
  beforeEach(() => {
    vi.useFakeTimers()
  })
  afterEach(() => {
    vi.useRealTimers()
  })

  it('gibt den initialen Wert sofort zurück', () => {
    const { result } = renderHook(({ v }) => useDebounce(v, 300), {
      initialProps: { v: 'foo' },
    })
    expect(result.current).toBe('foo')
  })

  it('aktualisiert erst nach Ablauf der Verzögerung', () => {
    const { result, rerender } = renderHook(({ v }) => useDebounce(v, 300), {
      initialProps: { v: 'foo' },
    })
    rerender({ v: 'bar' })

    expect(result.current).toBe('foo')

    act(() => {
      vi.advanceTimersByTime(299)
    })
    expect(result.current).toBe('foo')

    act(() => {
      vi.advanceTimersByTime(1)
    })
    expect(result.current).toBe('bar')
  })

  it('verwirft frühere Updates, wenn der Wert sich erneut ändert', () => {
    const { result, rerender } = renderHook(({ v }) => useDebounce(v, 300), {
      initialProps: { v: 'a' },
    })
    rerender({ v: 'b' })
    act(() => {
      vi.advanceTimersByTime(200)
    })
    rerender({ v: 'c' })
    act(() => {
      vi.advanceTimersByTime(200)
    })
    expect(result.current).toBe('a')
    act(() => {
      vi.advanceTimersByTime(100)
    })
    expect(result.current).toBe('c')
  })
})
