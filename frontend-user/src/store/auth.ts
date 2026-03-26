import { defineStore } from 'pinia'

export const AUTH_STORAGE_KEY = 'user_auth_info'

export type LoginType = 'wechat' | 'account'

export interface StoredAuthInfo {
  token: string
  userId: string
  username: string
  loginType: LoginType
}

function readStoredAuth(): StoredAuthInfo | null {
  try {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY)
    return raw ? (JSON.parse(raw) as StoredAuthInfo) : null
  } catch {
    return null
  }
}

function writeStoredAuth(auth: StoredAuthInfo | null) {
  if (!auth) {
    localStorage.removeItem(AUTH_STORAGE_KEY)
    return
  }
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(auth))
}

export const useAuthStore = defineStore('auth', {
  state: (): StoredAuthInfo => ({
    token: '',
    userId: '',
    username: '',
    loginType: 'account',
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.token),
  },
  actions: {
    hydrate() {
      const stored = readStoredAuth()
      if (!stored) return
      this.token = stored.token
      this.userId = stored.userId
      this.username = stored.username
      this.loginType = stored.loginType
    },
    setAccountAuth(payload: { token: string; username: string }) {
      this.token = payload.token
      this.userId = payload.username
      this.username = payload.username
      this.loginType = 'account'
      writeStoredAuth({
        token: this.token,
        userId: this.userId,
        username: this.username,
        loginType: this.loginType,
      })
    },
    setWechatAuth(openidToken: string) {
      this.token = openidToken
      this.userId = openidToken
      this.username = '微信用户'
      this.loginType = 'wechat'
      writeStoredAuth({
        token: this.token,
        userId: this.userId,
        username: this.username,
        loginType: this.loginType,
      })
    },
    clear() {
      this.token = ''
      this.userId = ''
      this.username = ''
      this.loginType = 'account'
      writeStoredAuth(null)
    },
  },
})

export function getStoredAuth() {
  return readStoredAuth()
}
