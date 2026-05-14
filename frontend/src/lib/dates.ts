// Datumsformatierung in lokaler Zeitzone. Backend liefert UTC-ISO-Strings,
// User sieht lokale Werte. toISOString() würde immer UTC ausgeben.

const pad = (n: number) => String(n).padStart(2, '0')

export function formatLocalDate(iso: string): string {
  const d = new Date(iso)
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

export function formatLocalDateTime(iso: string): string {
  const d = new Date(iso)
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
