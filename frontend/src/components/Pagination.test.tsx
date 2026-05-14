import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import { Pagination } from './Pagination'

describe('<Pagination>', () => {
  it('rendert nichts wenn nur eine Seite vorhanden ist', () => {
    const { container } = render(
      <Pagination page={1} pageSize={20} total={5} onPageChange={() => {}} />,
    )
    expect(container.firstChild).toBeNull()
  })

  it('deaktiviert "vorherige Seite" auf Seite 1', () => {
    render(<Pagination page={1} pageSize={10} total={100} onPageChange={() => {}} />)
    expect(screen.getByLabelText('vorherige Seite')).toBeDisabled()
    expect(screen.getByLabelText('nächste Seite')).not.toBeDisabled()
  })

  it('ruft onPageChange mit nächstem Wert auf', async () => {
    const onPageChange = vi.fn()
    render(<Pagination page={2} pageSize={10} total={100} onPageChange={onPageChange} />)
    await userEvent.click(screen.getByLabelText('nächste Seite'))
    expect(onPageChange).toHaveBeenCalledWith(3)
  })

  it('markiert die aktuelle Seite mit aria-current', () => {
    render(<Pagination page={3} pageSize={10} total={100} onPageChange={() => {}} />)
    expect(screen.getByRole('button', { name: '3' })).toHaveAttribute('aria-current', 'page')
  })
})
