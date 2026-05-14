import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { ApiError, api } from './api'

// Hilfs-Response für `fetch`-Mock.
function jsonResponse(status: number, body: unknown): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  })
}

describe('api error parsing', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', vi.fn())
  })
  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('liest Spring-HttpException-Response ({error: "..."})', async () => {
    vi.mocked(fetch).mockResolvedValue(
      jsonResponse(409, { error: 'Username or email already taken' }),
    )

    await expect(
      api.auth.register({
        username: 'x',
        email: 'x@y.de',
        password: 'pass1234',
        passwordConfirm: 'pass1234',
      }),
    ).rejects.toMatchObject({ status: 409, message: 'Username or email already taken' })
  })

  it('aggregiert Spring-Bean-Validation-Errors ({field: msg, ...})', async () => {
    vi.mocked(fetch).mockResolvedValue(
      jsonResponse(400, {
        password: 'Passwort muss mindestens 8 Zeichen lang sein',
        email: 'Ungültiges E-Mail-Format',
      }),
    )

    await expect(
      api.auth.register({
        username: 'x',
        email: 'invalid',
        password: 'short',
        passwordConfirm: 'short',
      }),
    ).rejects.toMatchObject({
      status: 400,
      // Beide Field-Errors müssen im message auftauchen.
      message: expect.stringContaining('Passwort muss mindestens 8 Zeichen lang sein'),
    })
  })

  it('fällt auf statusText zurück wenn Body kein JSON ist', async () => {
    vi.mocked(fetch).mockResolvedValue(
      new Response('Internal Server Error (plain text)', { status: 500, statusText: 'Server Error' }),
    )

    await expect(api.auth.me()).rejects.toBeInstanceOf(ApiError)
    await expect(api.auth.me()).rejects.toMatchObject({ status: 500 })
  })

  it('liefert undefined bei 204 No Content', async () => {
    vi.mocked(fetch).mockResolvedValue(new Response(null, { status: 204 }))
    await expect(api.auth.logout()).resolves.toBeUndefined()
  })
})

describe('api pagination query', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', vi.fn(() => Promise.resolve(jsonResponse(200, { bugs: [], total: 0, page: 0, pageSize: 20 }))))
  })
  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('sendet kein page-Parameter für Seite 1 (Backend default = 0-indexed first page)', async () => {
    await api.listBugs(
      { status: [], priority: '', tagId: null, assigneeId: null, search: '', archived: false },
      1,
    )
    const call = vi.mocked(fetch).mock.calls[0]?.[0] as string
    expect(call).not.toContain('page=')
  })

  it('konvertiert UI-page=2 zu Backend-page=1 (0-indexed)', async () => {
    await api.listBugs(
      { status: [], priority: '', tagId: null, assigneeId: null, search: '', archived: false },
      2,
    )
    const call = vi.mocked(fetch).mock.calls[0]?.[0] as string
    expect(call).toContain('page=1')
    expect(call).not.toContain('page=2')
  })
})
