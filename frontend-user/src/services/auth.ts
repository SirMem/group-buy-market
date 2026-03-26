import gbmHttp from '../request/gbm'
import payHttp from '../request/pay'

export interface AuthRequest {
  username: string
  password: string
}

export interface AuthResponse {
  token: string
  id: number
  username: string
  role: string
  userStatus: number
}

export interface ApiResult<T> {
  code: number | string
  info: string
  data: T
}

export async function login(payload: AuthRequest) {
  return gbmHttp.post<any, ApiResult<AuthResponse>>('/api/v1/auth/login', payload)
}

export async function register(payload: AuthRequest) {
  return gbmHttp.post<any, ApiResult<AuthResponse>>('/api/v1/auth/register', payload)
}

export async function getWeixinTicket() {
  return payHttp.get<any, ApiResult<string>>('/api/v1/login/weixin_qrcode_ticket')
}

export async function checkLogin(ticket: string) {
  return payHttp.get<any, ApiResult<string>>('/api/v1/login/check_login', {
    params: { ticket },
  })
}
