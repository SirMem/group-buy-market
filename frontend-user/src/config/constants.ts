export interface OptionItem {
  label: string
  value: string
}

export const SOURCE_OPTIONS: OptionItem[] = [
  { label: '美团', value: 'meituan' },
  { label: '抖音', value: 'douyin' },
  { label: '小红书', value: 'xiaohongshu' },
]

export const CHANNEL_OPTIONS: OptionItem[] = [
  { label: 'APP', value: 'app' },
  { label: '小程序', value: 'miniapp' },
  { label: 'H5网页', value: 'h5' },
]

export const DEFAULT_SOURCE = SOURCE_OPTIONS[0].value
export const DEFAULT_CHANNEL = CHANNEL_OPTIONS[0].value

export const ORDER_STATUS_MAP: Record<string, { label: string; type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  CREATE: { label: '创建完成', type: 'info' },
  PAY_WAIT: { label: '待支付', type: 'warning' },
  PAY_SUCCESS: { label: '已支付', type: 'primary' },
  DEAL_DONE: { label: '交易完成', type: 'success' },
  CLOSE: { label: '已关闭', type: 'info' },
  WAIT_REFUND: { label: '待退款', type: 'danger' },
  REFUND: { label: '已退款', type: 'info' },
}
