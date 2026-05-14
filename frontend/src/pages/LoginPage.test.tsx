import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { LoginPage } from './LoginPage'
import { AuthProvider } from '../context/AuthContext'
import { ApiError, api } from '../lib/api'

function renderLogin() {
  return render(
    <MemoryRouter initialEntries={['/login']}>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/bugs" element={<p>bug-liste</p>} />
        </Routes>
      </AuthProvider>
    </MemoryRouter>,
  )
}

describe('<LoginPage>', () => {
  beforeEach(() => {
    vi.spyOn(api.auth, 'me').mockRejectedValue(new Error('401'))
  })
  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('zeigt das Formular mit Username- und Passwort-Feld', async () => {
    renderLogin()
    expect(await screen.findByLabelText('username')).toBeInTheDocument()
    expect(screen.getByLabelText('password')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument()
  })

  it('navigiert nach erfolgreichem Login zu /bugs', async () => {
    vi.spyOn(api.auth, 'login').mockResolvedValue({ id: 1, username: 'jane', role: 'TESTER' })
    renderLogin()

    await userEvent.type(await screen.findByLabelText('username'), 'jane')
    await userEvent.type(screen.getByLabelText('password'), 'secret123')
    await userEvent.click(screen.getByRole('button', { name: /login/i }))

    await waitFor(() => {
      expect(screen.getByText('bug-liste')).toBeInTheDocument()
    })
  })

  it('zeigt bei 401 eine verständliche Fehlermeldung', async () => {
    vi.spyOn(api.auth, 'login').mockRejectedValue(new ApiError(401, 'Unauthorized'))
    renderLogin()

    await userEvent.type(await screen.findByLabelText('username'), 'jane')
    await userEvent.type(screen.getByLabelText('password'), 'wrong')
    await userEvent.click(screen.getByRole('button', { name: /login/i }))

    expect(await screen.findByText(/Falscher Benutzername oder Passwort/i)).toBeInTheDocument()
  })
})
