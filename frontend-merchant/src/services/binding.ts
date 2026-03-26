import http from '../request/request'

export interface MerchantBindingInfo {
  goodsId: string
  source: string
  channel: string
  activityId: string
  activityName: string
}

export const bindingService = {
  bindApi: '/api/v1/gbm/merchant/activity/bind_sku_activity',
  unbindApi: '/api/v1/gbm/merchant/activity/unbind_sku_activity',
  queryApi: '/api/v1/gbm/merchant/activity/query_sku_activity',

  async bind(data: { goodsId: string; activityId: string; source: string; channel: string }): Promise<any> {
    return http.post(this.bindApi, {
      goodsId: data.goodsId,
      activityId: Number(data.activityId),
      source: data.source,
      channel: data.channel,
    })
  },

  async unbind(data: { goodsId: string; source: string; channel: string }): Promise<any> {
    return http.delete(this.unbindApi, {
      params: { goodsId: data.goodsId, source: data.source, channel: data.channel },
    })
  },

  async query(data: { goodsId: string; source: string; channel: string }): Promise<MerchantBindingInfo | null> {
    const res: any = await http.get(this.queryApi, {
      params: { goodsId: data.goodsId, source: data.source, channel: data.channel },
    })
    if (!res?.data) return null
    return {
      goodsId: res.data.goodsId || data.goodsId,
      source: res.data.source || data.source,
      channel: res.data.channel || data.channel,
      activityId: String(res.data.activityId || ''),
      activityName: res.data.activityName || '',
    }
  },
}
