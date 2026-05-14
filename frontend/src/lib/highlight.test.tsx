import { render } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import { highlight } from './highlight'

describe('highlight()', () => {
  it('gibt rohen Text zurück, wenn kein Suchbegriff übergeben wird', () => {
    const { container } = render(<>{highlight('Login Button defekt', '')}</>)
    expect(container.textContent).toBe('Login Button defekt')
    expect(container.querySelector('mark')).toBeNull()
  })

  it('umrahmt Matches case-insensitive mit <mark>', () => {
    const { container } = render(<>{highlight('LOGIN button defekt', 'login')}</>)
    const marks = container.querySelectorAll('mark')
    expect(marks).toHaveLength(1)
    expect(marks[0]!.textContent).toBe('LOGIN')
  })

  it('hebt mehrere Treffer im Text hervor', () => {
    const { container } = render(<>{highlight('Bug bug Bug', 'bug')}</>)
    expect(container.querySelectorAll('mark')).toHaveLength(3)
  })

  it('lässt Text unverändert, wenn der Suchbegriff nicht enthalten ist', () => {
    const { container } = render(<>{highlight('Login Button defekt', 'xyz')}</>)
    expect(container.querySelector('mark')).toBeNull()
    expect(container.textContent).toBe('Login Button defekt')
  })
})
