export const USER_ROLES = ['TESTER', 'DEVELOPER', 'ADMIN'] as const

export type UserRole = (typeof USER_ROLES)[number]

export const ROLE_LABELS: Record<UserRole, string> = {
  TESTER: 'tester',
  DEVELOPER: 'developer',
  ADMIN: 'admin',
}

export interface Me {
  id: number
  username: string
  role: UserRole
}

/** Vollständiger User aus GET /api/users (UserWithoutHash). */
export interface AdminUser {
  id: number
  username: string
  email: string
  role: UserRole
  active: boolean
  createdAt: string
}

export interface RegisterInput {
  username: string
  email: string
  password: string
  passwordConfirm: string
}
