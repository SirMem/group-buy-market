import http from '../request/request'

export interface MerchantDiscountItem {
  discountId: string
  discountName: string
  discountDesc: string
  discountType: '满减' | '直减' | '折扣'
  marketPlan: string
  marketExpr: string
  status: '草稿' | '生效中'
}

const MARKET_PLAN_TO_LABEL: Record<string, '满减' | '直减' | '折扣'> = {
  MJ: '满减',
  ZJ: '直减',
  N: '折扣',
}

const LABEL_TO_MARKET_PLAN: Record<string, string> = {
  满减: 'MJ',
  直减: 'ZJ',
  折扣: 'N',
}

function adaptDiscountResponse(dto: any): MerchantDiscountItem {
  const marketPlan = dto.marketPlan || 'ZJ'
  return {
    discountId: dto.discountId || '',
    discountName: dto.discountName || '',
    discountDesc: dto.discountDesc || '',
    discountType: MARKET_PLAN_TO_LABEL[marketPlan] || '直减',
    marketPlan,
    marketExpr: dto.marketExpr || '',
    status: '生效中',
  }
}

export { LABEL_TO_MARKET_PLAN }

export const discountService = {
  listApi: '/api/v1/gbm/merchant/discount/query_list',
  queryApi: '/api/v1/gbm/merchant/discount/query',
  createApi: '/api/v1/gbm/merchant/discount/create',
  updateApi: '/api/v1/gbm/merchant/discount/update',
  deleteApi: '/api/v1/gbm/merchant/discount/delete',

  async queryList(_params?: Record<string, any>): Promise<MerchantDiscountItem[]> {
    const res: any = await http.get(this.listApi)
    const list: any[] = res?.data || []
    return list.map(adaptDiscountResponse)
  },

  async queryDetail(params?: Record<string, any>): Promise<MerchantDiscountItem | null> {
    const discountId = params?.discountId
    if (!discountId) return null
    const res: any = await http.get(this.queryApi, { params: { discountId } })
    if (!res?.data) return null
    return adaptDiscountResponse(res.data)
  },

  async create(data: Record<string, any>): Promise<any> {
    return http.post(this.createApi, data)
  },

  async update(data: Record<string, any>): Promise<any> {
    const { discountId, ...body } = data
    return http.put(`${this.updateApi}?discountId=${discountId}`, body)
  },

  async remove(data: Record<string, any>): Promise<any> {
    const discountId = data?.discountId
    return http.delete(`${this.deleteApi}?discountId=${discountId}`)
  },
}
