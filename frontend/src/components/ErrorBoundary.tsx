import { Component, type ErrorInfo, type ReactNode } from 'react'

type FallbackRenderer = (error: Error, reset: () => void) => ReactNode

interface Props {
  children: ReactNode
  /** Optional inline-fallback für Sub-Boundaries. Default: ganzseitiger Error-Screen. */
  fallback?: FallbackRenderer
}

interface State {
  error: Error | null
}

export class ErrorBoundary extends Component<Props, State> {
  state: State = { error: null }

  static getDerivedStateFromError(error: Error): State {
    return { error }
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error('Unhandled error in component tree:', error, info)
  }

  reset = () => {
    this.setState({ error: null })
  }

  render() {
    if (this.state.error) {
      if (this.props.fallback) {
        return this.props.fallback(this.state.error, this.reset)
      }
      return (
        <div className="min-h-screen bg-bg text-ink flex items-center justify-center px-4">
          <div className="w-full max-w-md border border-red-fg/40 rounded p-6 bg-surface">
            <h1 className="font-mono text-lg font-bold text-red-fg mb-2">Etwas ist schiefgelaufen</h1>
            <p className="font-mono text-xs text-ink-soft mb-4">
              Ein unerwarteter Fehler ist aufgetreten. Versuchen Sie es erneut oder laden Sie die
              Seite neu.
            </p>
            {import.meta.env.DEV && (
              <pre className="font-mono text-[10px] text-red-fg bg-bg border border-border rounded p-2 mb-4 overflow-auto max-h-40">
                {this.state.error.message}
              </pre>
            )}
            <div className="flex gap-2">
              <button
                type="button"
                onClick={this.reset}
                className="font-mono text-xs bg-ink text-bg rounded px-3 py-1.5 hover:opacity-90"
              >
                erneut versuchen
              </button>
              <button
                type="button"
                onClick={() => window.location.reload()}
                className="font-mono text-xs text-ink-soft hover:text-ink border border-border rounded px-3 py-1.5"
              >
                Seite neu laden
              </button>
            </div>
          </div>
        </div>
      )
    }
    return this.props.children
  }
}

/**
 * Kleines inline-Fallback für Sub-Boundaries: Roter Rahmen mit Retry-Button,
 * statt die ganze Seite zu unterbrechen.
 */
export function inlineErrorFallback(label: string): FallbackRenderer {
  return (error, reset) => (
    <div className="border border-red-fg/40 rounded p-3 bg-surface" role="alert">
      <p className="font-mono text-xs text-red-fg mb-1">{label} konnte nicht angezeigt werden.</p>
      {import.meta.env.DEV && (
        <p className="font-mono text-[10px] text-ink-soft mb-2 break-words">{error.message}</p>
      )}
      <button
        type="button"
        onClick={reset}
        className="font-mono text-[11px] text-ink-soft hover:text-ink border border-border rounded px-2 py-0.5"
      >
        erneut versuchen
      </button>
    </div>
  )
}
