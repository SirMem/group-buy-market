import axios, { AxiosHeaders, type InternalAxiosRequestConfig } from 'axios'
import router from '../router'
import { AUTH_STORAGE_KEY } from '../store/auth'

const GBM_BASE = (import.meta.env.VITE_GBM_API_BASE || '').replace(/\/$/, '')

const gbmHttp = axios.create({
  baseURL: GBM_BASE,
  timeout: 60000,
})

gbmHttp.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const stored = localStorage.getItem(AUTH_STORAGE_KEY)
  const auth = stored ? JSON.parse(stored) : null
  const token = auth?.token

  const headers =
    config.headers instanceof AxiosHeaders
      ? config.headers
      : new AxiosHeaders(config.headers || {})

  headers.set('Accept', 'application/json')
  if (!(config.data instanceof FormData)) {
    headers.set('Content-Type', 'application/json')
  }
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  config.headers = headers
  return config
})

gbmHttp.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error?.response?.status === 401) {
      localStorage.removeItem(AUTH_STORAGE_KEY)
      if (router.currentRoute.value.path !== '/login') {
        router.replace('/login')
      }
    }
    return Promise.reject(error)
  },
)

export default gbmHttp
