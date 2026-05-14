import type { Tag } from '../types/bug'

// Hardcoded fallback so the Create-Form has Tag-Optionen, solange T038a
// (`GET /api/tags`) noch nicht implementiert ist. IDs müssen mit V2-Migration
// im Backend übereinstimmen: backend/src/main/resources/db/migration/V2*.sql.
// TODO: Sobald T038a fertig ist, durch api.listTags() ersetzen.
export const SEED_TAGS: readonly Tag[] = [
  { id: 1, name: 'Backend', color: '#3B82F6' },
  { id: 2, name: 'Frontend', color: '#10B981' },
  { id: 3, name: 'Bug', color: '#EF4444' },
  { id: 4, name: 'Feature', color: '#8B5CF6' },
]
