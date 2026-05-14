import { useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'sonner'
import { useAuth } from '../context/AuthContext'
import { ApiError } from '../lib/api'

const registerSchema = z
  .object({
    username: z
      .string()
      .min(3, 'Benutzername mindestens 3 Zeichen')
      .max(50, 'Maximal 50 Zeichen')
      .regex(/^[a-zA-Z0-9_.-]+$/, 'Nur Buchstaben, Zahlen, _ . -'),
    email: z.string().email('Keine gültige E-Mail-Adresse'),
    password: z
      .string()
      .min(8, 'Passwort mindestens 8 Zeichen')
      .regex(/[0-9]/, 'Mindestens eine Zahl erforderlich'),
    passwordConfirm: z.string(),
  })
  .refine((data) => data.password === data.passwordConfirm, {
    message: 'Passwörter stimmen nicht überein',
    path: ['passwordConfirm'],
  })

type RegisterValues = z.infer<typeof registerSchema>

export function RegisterPage() {
  const { user, loading, register: registerAccount } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [submitError, setSubmitError] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: { username: '', email: '', password: '', passwordConfirm: '' },
  })

  if (loading) return null
  if (user) return <Navigate to="/bugs" replace />

  async function onSubmit(values: RegisterValues) {
    setSubmitError(null)
    try {
      const { autoLoggedIn } = await registerAccount({
        username: values.username,
        email: values.email,
        password: values.password,
        passwordConfirm: values.passwordConfirm,
      })
      const redirectTo = (location.state as { from?: string } | null)?.from ?? '/bugs'
      if (autoLoggedIn) {
        toast.success('account angelegt — willkommen')
        navigate(redirectTo, { replace: true })
      } else {
        toast.success('account angelegt — bitte einloggen')
        navigate('/login', { replace: true, state: { from: redirectTo } })
      }
    } catch (err) {
      if (err instanceof ApiError && err.status === 409) {
        setSubmitError(err.message || 'Benutzername oder E-Mail bereits vergeben')
      } else if (err instanceof ApiError && err.status === 400) {
        setSubmitError(err.message || 'Eingaben unvollständig')
      } else {
        setSubmitError(err instanceof Error ? err.message : 'Registrierung fehlgeschlagen')
      }
    }
  }

  const inputClass =
    'w-full px-3 py-2 bg-bg border border-border rounded font-mono text-sm focus:outline-none focus:border-ink'

  return (
    <div className="min-h-screen bg-bg text-ink flex items-center justify-center px-4">
      <form
        onSubmit={handleSubmit(onSubmit)}
        noValidate
        className="w-full max-w-sm border border-border rounded p-6 bg-surface"
      >
        <h1 className="font-mono text-2xl font-bold mb-1">register</h1>
        <p className="font-mono text-xs text-ink-soft mb-5">POST /api/auth/register</p>

        <label htmlFor="username" className="block font-mono text-xs text-ink-soft mb-1">
          username
        </label>
        <input
          id="username"
          type="text"
          autoComplete="username"
          autoFocus
          aria-invalid={errors.username ? 'true' : 'false'}
          className={`${inputClass} mb-1`}
          {...register('username')}
        />
        {errors.username && (
          <p className="font-mono text-[11px] text-red-fg mb-2">{errors.username.message}</p>
        )}
        <div className="mb-3" />

        <label htmlFor="email" className="block font-mono text-xs text-ink-soft mb-1">
          email
        </label>
        <input
          id="email"
          type="email"
          autoComplete="email"
          aria-invalid={errors.email ? 'true' : 'false'}
          className={`${inputClass} mb-1`}
          {...register('email')}
        />
        {errors.email && (
          <p className="font-mono text-[11px] text-red-fg mb-2">{errors.email.message}</p>
        )}
        <div className="mb-3" />

        <label htmlFor="password" className="block font-mono text-xs text-ink-soft mb-1">
          password
        </label>
        <input
          id="password"
          type="password"
          autoComplete="new-password"
          aria-invalid={errors.password ? 'true' : 'false'}
          className={`${inputClass} mb-1`}
          {...register('password')}
        />
        {errors.password && (
          <p className="font-mono text-[11px] text-red-fg mb-2">{errors.password.message}</p>
        )}
        <div className="mb-3" />

        <label htmlFor="passwordConfirm" className="block font-mono text-xs text-ink-soft mb-1">
          password confirm
        </label>
        <input
          id="passwordConfirm"
          type="password"
          autoComplete="new-password"
          aria-invalid={errors.passwordConfirm ? 'true' : 'false'}
          className={`${inputClass} mb-1`}
          {...register('passwordConfirm')}
        />
        {errors.passwordConfirm && (
          <p className="font-mono text-[11px] text-red-fg mb-2">
            {errors.passwordConfirm.message}
          </p>
        )}
        <div className="mb-4" />

        {submitError && (
          <p className="font-mono text-xs text-red-fg mb-3" role="alert">
            {submitError}
          </p>
        )}

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full px-3 py-2 bg-ink text-bg font-mono text-sm rounded hover:opacity-90 disabled:opacity-50"
        >
          {isSubmitting ? '...' : 'register'}
        </button>

        <p className="font-mono text-xs text-ink-soft mt-4 text-center">
          schon registriert?{' '}
          <Link to="/login" className="text-ink hover:underline underline-offset-2">
            login
          </Link>
        </p>
      </form>
    </div>
  )
}
