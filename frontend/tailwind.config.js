/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'ui-monospace', 'SFMono-Regular', 'monospace'],
      },
      colors: {
        // Surface tokens — driven by CSS variables defined in index.css
        ink: 'rgb(var(--ink) / <alpha-value>)',
        'ink-soft': 'rgb(var(--ink-soft) / <alpha-value>)',
        'on-ink': 'rgb(var(--on-ink) / <alpha-value>)',
        surface: 'rgb(var(--surface) / <alpha-value>)',
        'surface-2': 'rgb(var(--surface-2) / <alpha-value>)',
        bg: 'rgb(var(--bg) / <alpha-value>)',
        border: 'rgb(var(--border) / <alpha-value>)',
        accent: 'rgb(var(--accent) / <alpha-value>)',
        'accent-fg': 'rgb(var(--accent-fg) / <alpha-value>)',
        // Pill / status backgrounds
        'green-bg': 'rgb(var(--green-bg) / <alpha-value>)',
        'green-fg': 'rgb(var(--green-fg) / <alpha-value>)',
        'amber-bg': 'rgb(var(--amber-bg) / <alpha-value>)',
        'amber-fg': 'rgb(var(--amber-fg) / <alpha-value>)',
        'orange-bg': 'rgb(var(--orange-bg) / <alpha-value>)',
        'orange-fg': 'rgb(var(--orange-fg) / <alpha-value>)',
        'red-bg': 'rgb(var(--red-bg) / <alpha-value>)',
        'red-fg': 'rgb(var(--red-fg) / <alpha-value>)',
        'blue-bg': 'rgb(var(--blue-bg) / <alpha-value>)',
        'blue-fg': 'rgb(var(--blue-fg) / <alpha-value>)',
        'purple-bg': 'rgb(var(--purple-bg) / <alpha-value>)',
        'purple-fg': 'rgb(var(--purple-fg) / <alpha-value>)',
        'slate-bg': 'rgb(var(--slate-bg) / <alpha-value>)',
        'slate-fg': 'rgb(var(--slate-fg) / <alpha-value>)',
      },
      borderRadius: {
        DEFAULT: 'var(--r)',
      },
    },
  },
  plugins: [],
}
