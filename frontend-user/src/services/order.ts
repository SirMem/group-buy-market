import payHttp from '../request/pay'

export interface ApiResponse<T> {
  code: string | number
  info: string
  data: T
}

export interface CreatePayOrderRequest {
  userId: string
  productId: string
  teamId?: string
  activityId?: number
  marketType: number
}

export interface OrderInfoDTO {
  id: number
  userId: string
  productId: string
  productName: string
  orderId: string
  orderTime: string
  totalAmount: number
  status: string
  payUrl: string
  marketType: number
  marketDeductionAmount: number
  payAmount: number
  payTime: string
}

export interface QueryOrderListResponseDTO {
  orderList: OrderInfoDTO[]
  hasMore: boolean
  lastId: number | null
}

export interface RefundOrderResponseDTO {
  success: boolean
  message: string
  orderId: string
}

export async function createPayOrder(payload: CreatePayOrderRequest) {
  return payHttp.post<any, ApiResponse<string>>('/api/v1/alipay/create_pay_order', payload)
}

export async function queryUserOrderList(payload: { userId: string; lastId?: number | null; pageSize: number }) {
  return payHttp.post<any, ApiResponse<QueryOrderListResponseDTO>>('/api/v1/alipay/query_user_order_list', payload)
}

export async function refundOrder(payload: { userId: string; orderId: string }) {
  return payHttp.post<any, ApiResponse<RefundOrderResponseDTO>>('/api/v1/alipay/refund_order', payload)
}

export async function activePayNotify(outTradeNo: string) {
  return payHttp.post<any, ApiResponse<string>>('/api/v1/alipay/active_pay_notify', null, {
    params: { outTradeNo },
  })
}
