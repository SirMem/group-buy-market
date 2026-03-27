import { defineStore } from 'pinia'

const AUTH_KEY = 'gbm_auth_info'

export interface AuthUser {
  id?: string | number
  username?: string
  name?: string
  role?: string
  userStatus?: number
}

interface AuthState {
  token: string
  user: AuthUser | null
}

const normalizeUser = (user: any): AuthUser | null => {
  if (!user) return null
  return {
    id: user.id,
    username: user.username || user.name || '',
    name: user.name || user.username || '',
    role: user.role,
    userStatus: user.userStatus,
  }
}

const loadAuth = (): AuthState => {
  try {
    const raw = localStorage.getItem(AUTH_KEY)
    if (!raw) return { token: '', user: null }
    const parsed = JSON.parse(raw)
    return {
      token: parsed.token || parsed.jwtToken || '',
      user: normalizeUser(parsed.user),
    }
  } catch {
    return { token: '', user: null }
  }
}

export const getStoredAuth = () => loadAuth()

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: loadAuth().token,
    user: loadAuth().user,
  }),
  getters: {
    isLogin: (state) => Boolean(state.token),
  },
  actions: {
    setAuth(payload: AuthState) {
      this.token = payload.token || ''
      this.user = normalizeUser(payload.user)
      this.persist()
    },
    clear() {
      this.token = ''
      this.user = null
      localStorage.removeItem(AUTH_KEY)
    },
    persist() {
      localStorage.setItem(
        AUTH_KEY,
        JSON.stringify({ token: this.token, user: this.user }),
      )
    },
  },
})

