import axios, { AxiosHeaders, type InternalAxiosRequestConfig } from 'axios'
import router from '../router/router'
import { useAuthStore } from '../router/pinia'

const BASE_URL = (import.meta.env.VITE_API_BASE || '').replace(/\/$/, '')
const REQUEST_TIMEOUT = 60000
const AUTH_KEY = 'gbm_auth_info'

const http = axios.create({
  baseURL: BASE_URL,
  timeout: REQUEST_TIMEOUT,
})

export const normalizeError = (error: any) => {
  if (axios.isCancel(error)) {
    return {
      message: '请求已取消',
      status: null,
      isNetworkError: false,
      raw: error,
    }
  }

  const isNetworkError = !error.response
  const status = error.response?.status ?? null
  const data = error.response?.data ?? {}
  const message = data.message || data.error || error.message || '请求失败，请稍后再试'

  return {
    message,
    status,
    isNetworkError,
    raw: error,
  }
}

http.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    let authStore: ReturnType<typeof useAuthStore> | null = null
    try {
      authStore = useAuthStore()
    } catch {
      authStore = null
    }

    const storedAuth = localStorage.getItem(AUTH_KEY)
    const auth = storedAuth ? JSON.parse(storedAuth) : {}
    const token = authStore?.token || auth.token
    const isFormData = config.data instanceof FormData

    const headers =
      config.headers instanceof AxiosHeaders
        ? config.headers
        : new AxiosHeaders(config.headers || {})

    headers.set('Accept', 'application/json')
    if (!isFormData) {
      headers.set('Content-Type', 'application/json')
    }
    if (token) {
      headers.set('Authorization', `Bearer ${token}`)
    }

    config.headers = headers
    return config
  },
  (error) => Promise.reject(normalizeError(error)),
)

http.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const status = error?.response?.status
    let authStore: ReturnType<typeof useAuthStore> | null = null

    try {
      authStore = useAuthStore()
    } catch {
      authStore = null
    }

    if (status === 401) {
      authStore?.clear()
      if (router.currentRoute.value.path !== '/') {
        router.replace('/')
      }
    }

    return Promise.reject(normalizeError(error))
  },
)

export default http
