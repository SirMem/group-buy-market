import http from '../request/request'

export interface LoginRequest {
  username: string
  password: string
}

export interface AuthApiResponse {
  token?: string
  jwtToken?: string
  id?: string | number
  username?: string
  role?: string
  userStatus?: number
}

export interface ApiResult<T> {
  code?: string
  info?: string
  message?: string
  data: T
}

export const login = (payload: LoginRequest) => {
  return http.post<any, ApiResult<AuthApiResponse>>('/api/v1/auth/login', payload)
}

export const profile = () => {
  return http.post<any, ApiResult<AuthApiResponse>>('/api/v1/auth/profile')
}
