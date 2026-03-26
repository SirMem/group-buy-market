import http from '../request/request'

export interface MerchantSkuItem {
  goodsId: string
  name: string
  price: number
  totalStock: number
  source: string
  channel: string
  activityStatus?: '未绑定' | '已绑定' | '生效中'
}

export interface MerchantSkuQueryParams {
  keyword?: string
  activityStatus?: string
  page?: number
  pageSize?: number
}

export interface MerchantSkuFormData {
  goodsId?: string
  name: string
  price: number
  totalStock: number
  source: string
  channel: string
}

function adaptSkuResponse(dto: any): MerchantSkuItem {
  const goods = dto.goods || {}
  const inventory = dto.inventory || {}
  const bindings: any[] = dto.activityBindings || []
  let activityStatus: '未绑定' | '已绑定' | '生效中' = '未绑定'
  if (bindings.length > 0) {
    const hasOnline = bindings.some((b) => b.status === 'ONLINE')
    activityStatus = hasOnline ? '生效中' : '已绑定'
  }
  return {
    goodsId: goods.goodsId || '',
    name: goods.name || '',
    price: Number(goods.price || 0),
    totalStock: Number(inventory.totalStock || 0),
    source: goods.source || '',
    channel: goods.channel || '',
    activityStatus,
  }
}

export const skuService = {
  listApi: '/api/v1/gbm/merchant/sku/query',
  createApi: '/api/v1/gbm/merchant/sku/create',
  updateApi: '/api/v1/gbm/merchant/sku/update',
  deleteApi: '/api/v1/gbm/merchant/sku/delete',

  async queryList(params?: MerchantSkuQueryParams): Promise<MerchantSkuItem[]> {
    const res: any = await http.post(this.listApi, {
      page: params?.page || 1,
      pageSize: params?.pageSize || 50,
      keyword: params?.keyword || undefined,
    })
    const list: any[] = res?.data?.list || []
    return list.map(adaptSkuResponse)
  },

  async queryDetail(goodsId: string): Promise<MerchantSkuFormData | null> {
    const res: any = await http.post(this.listApi, { goodsId, page: 1, pageSize: 1 })
    const list: any[] = res?.data?.list || []
    if (!list.length) return null
    const dto = list[0]
    const goods = dto.goods || {}
    const inventory = dto.inventory || {}
    return {
      goodsId: goods.goodsId || '',
      name: goods.name || '',
      price: Number(goods.price || 0),
      totalStock: Number(inventory.totalStock || 0),
      source: goods.source || '',
      channel: goods.channel || '',
    }
  },

  async create(data: MerchantSkuFormData): Promise<any> {
    return http.post(this.createApi, data)
  },

  async update(data: MerchantSkuFormData): Promise<any> {
    const { goodsId, ...body } = data
    return http.put(`${this.updateApi}?goodsId=${goodsId}`, body)
  },

  async remove(goodsId: string): Promise<any> {
    return http.delete(`${this.deleteApi}?goodsId=${goodsId}`)
  },
}
