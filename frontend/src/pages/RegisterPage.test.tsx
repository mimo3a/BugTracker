import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { RegisterPage } from './RegisterPage'
import { AuthProvider } from '../context/AuthContext'
import { api } from '../lib/api'

function renderPage() {
  return render(
    <MemoryRouter initialEntries={['/register']}>
      <AuthProvider>
        <Routes>
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<p>login-seite</p>} />
        </Routes>
      </AuthProvider>
    </MemoryRouter>,
  )
}

describe('<RegisterPage>', () => {
  beforeEach(() => {
    vi.spyOn(api.auth, 'me').mockRejectedValue(new Error('401'))
  })
  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('zeigt Validierungsfehler für zu kurzen Username + ungültige E-Mail', async () => {
    renderPage()
    await userEvent.type(await screen.findByLabelText('username'), 'ab')
    await userEvent.type(screen.getByLabelText('email'), 'kein-email')
    await userEvent.type(screen.getByLabelText('password'), '1234567')
    await userEvent.type(screen.getByLabelText('password confirm'), '7654321')
    await userEvent.click(screen.getByRole('button', { name: /register/i }))

    expect(await screen.findByText(/Benutzername mindestens 3 Zeichen/i)).toBeInTheDocument()
    expect(screen.getByText(/Keine gültige E-Mail/i)).toBeInTheDocument()
    expect(screen.getByText(/Passwort mindestens 8 Zeichen/i)).toBeInTheDocument()
  })

  it('zeigt Fehler, wenn beide Passwörter nicht übereinstimmen', async () => {
    renderPage()
    await userEvent.type(await screen.findByLabelText('username'), 'jane'),
      await userEvent.type(screen.getByLabelText('email'), 'jane@example.com')
    await userEvent.type(screen.getByLabelText('password'), 'abcd1234')
    await userEvent.type(screen.getByLabelText('password confirm'), 'XXXX1234')
    await userEvent.click(screen.getByRole('button', { name: /register/i }))

    expect(await screen.findByText(/Passwörter stimmen nicht überein/i)).toBeInTheDocument()
  })
})
