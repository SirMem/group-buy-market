import http from '../request/request'

export interface MerchantActivityItem {
  activityId: string
  activityName: string
  discountId: string
  groupType: string
  takeLimitCount: number
  targetCount: number
  validTime: number
  status: '草稿' | '已绑定' | '生效中'
  startTime?: string
  endTime?: string
}

const STATUS_INT_TO_LABEL: Record<number, '草稿' | '已绑定' | '生效中'> = {
  0: '草稿',
  1: '已绑定',
  2: '生效中',
}

const STATUS_LABEL_TO_INT: Record<string, number> = {
  草稿: 0,
  已绑定: 1,
  生效中: 2,
}

const GROUP_TYPE_INT_TO_STR: Record<number, string> = {
  1: 'TEAM',
}

const GROUP_TYPE_STR_TO_INT: Record<string, number> = {
  TEAM: 1,
}

function adaptActivityResponse(dto: any): MerchantActivityItem {
  return {
    activityId: String(dto.activityId || ''),
    activityName: dto.activityName || '',
    discountId: dto.discountId || '',
    groupType: GROUP_TYPE_INT_TO_STR[dto.groupType] || 'TEAM',
    takeLimitCount: dto.takeLimitCount || 1,
    targetCount: dto.target || 2,
    validTime: dto.validTime || 24,
    status: STATUS_INT_TO_LABEL[dto.status] || '草稿',
    startTime: dto.startTime ? new Date(dto.startTime).toISOString().slice(0, 16) : undefined,
    endTime: dto.endTime ? new Date(dto.endTime).toISOString().slice(0, 16) : undefined,
  }
}

export { STATUS_LABEL_TO_INT, GROUP_TYPE_STR_TO_INT }

export const activityService = {
  listApi: '/api/v1/gbm/merchant/activity/query_list',
  queryApi: '/api/v1/gbm/merchant/activity/query',
  createApi: '/api/v1/gbm/merchant/activity/create',
  updateApi: '/api/v1/gbm/merchant/activity/update',
  deleteApi: '/api/v1/gbm/merchant/activity/delete',

  async queryList(_params?: Record<string, any>): Promise<MerchantActivityItem[]> {
    const res: any = await http.get(this.listApi)
    const list: any[] = res?.data || []
    return list.map(adaptActivityResponse)
  },

  async queryDetail(params?: Record<string, any>): Promise<MerchantActivityItem | null> {
    const activityId = params?.activityId
    if (!activityId) return null
    const res: any = await http.get(this.queryApi, { params: { activityId } })
    if (!res?.data) return null
    return adaptActivityResponse(res.data)
  },

  async create(data: Record<string, any>): Promise<any> {
    return http.post(this.createApi, data)
  },

  async update(data: Record<string, any>): Promise<any> {
    const { activityId, ...body } = data
    return http.put(`${this.updateApi}?activityId=${activityId}`, body)
  },

  async remove(data: Record<string, any>): Promise<any> {
    const activityId = data?.activityId
    return http.delete(`${this.deleteApi}?activityId=${activityId}`)
  },
}
