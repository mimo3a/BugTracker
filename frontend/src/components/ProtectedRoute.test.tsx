import { render, screen, waitFor } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { ProtectedRoute } from './ProtectedRoute'
import { AuthProvider } from '../context/AuthContext'
import { api } from '../lib/api'

describe('<ProtectedRoute>', () => {
  beforeEach(() => {
    vi.spyOn(api.auth, 'me').mockReset()
  })
  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('rendert die Kinder, wenn der User eingeloggt ist', async () => {
    vi.spyOn(api.auth, 'me').mockResolvedValue({ id: 1, username: 'jane', role: 'TESTER' })

    render(
      <MemoryRouter initialEntries={['/bugs']}>
        <AuthProvider>
          <Routes>
            <Route
              path="/bugs"
              element={
                <ProtectedRoute>
                  <p>geheim</p>
                </ProtectedRoute>
              }
            />
            <Route path="/login" element={<p>login-seite</p>} />
          </Routes>
        </AuthProvider>
      </MemoryRouter>,
    )

    expect(await screen.findByText('geheim')).toBeInTheDocument()
  })

  it('leitet nicht-eingeloggte User auf /login um', async () => {
    vi.spyOn(api.auth, 'me').mockRejectedValue(new Error('401'))

    render(
      <MemoryRouter initialEntries={['/bugs']}>
        <AuthProvider>
          <Routes>
            <Route
              path="/bugs"
              element={
                <ProtectedRoute>
                  <p>geheim</p>
                </ProtectedRoute>
              }
            />
            <Route path="/login" element={<p>login-seite</p>} />
          </Routes>
        </AuthProvider>
      </MemoryRouter>,
    )

    await waitFor(() => {
      expect(screen.getByText('login-seite')).toBeInTheDocument()
    })
    expect(screen.queryByText('geheim')).toBeNull()
  })
})
