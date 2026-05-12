export const USER_ROLES = ['TESTER', 'DEVELOPER', 'ADMIN'] as const

export type UserRole = (typeof USER_ROLES)[number]

export interface Me {
  id: number
  username: string
  role: UserRole
}
