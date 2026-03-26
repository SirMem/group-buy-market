import gbmHttp from '../request/gbm'

export interface TeamStatisticDTO {
  allTeamCount: number
  allTeamCompleteCount: number
  allTeamUserCount: number
}

export interface InventoryDTO {
  totalStock: number
  reservedStock: number
  soldStock: number
  availableStock: number
}

export interface MarketGoodsCardDTO {
  activityId: number
  goodsId: string
  goodsName: string
  originalPrice: number
  deductionPrice: number
  payPrice: number
  visible: boolean
  enable: boolean
  effective: boolean
  inventory?: InventoryDTO
  teamStatistic?: TeamStatisticDTO
}

export interface TeamDTO {
  userId: string
  teamId: string
  activityId: number
  targetCount: number
  completeCount: number
  lockCount: number
  validStartTime: string
  validEndTime: string
  validTimeCountdown: string
  outTradeNo: string
}

export interface GoodsDetailDTO {
  activityId: number
  goods: {
    goodsId: string
    originalPrice: number
    deductionPrice: number
    payPrice: number
  }
  teamList: TeamDTO[]
  teamStatistic: TeamStatisticDTO
}

export interface GoodsDetailRequest {
  userId: string
  source: string
  channel: string
  goodsId: string
}

export interface PageInfoDTO<T> {
  pageNum: number
  pageSize: number
  total: number
  pages: number
  list: T[]
}

export interface QueryMarketGoodsPageRequest {
  userId: string
  source: string
  channel: string
  page: number
  pageSize: number
  keyword?: string
  onlyEffective?: boolean
  includeInventory?: boolean
  includeTeamStatistic?: boolean
  includeTeamList?: boolean
}

export interface ApiResponse<T> {
  code: string | number
  info: string
  data: T
}

export async function queryMarketGoodsPage(payload: QueryMarketGoodsPageRequest) {
  return gbmHttp.post<any, ApiResponse<PageInfoDTO<MarketGoodsCardDTO>>>(
    '/api/v1/gbm/index/query_market_goods_page',
    payload,
  )
}

export async function queryGroupBuyMarketConfig(payload: GoodsDetailRequest) {
  return gbmHttp.post<any, ApiResponse<GoodsDetailDTO>>(
    '/api/v1/gbm/index/query_group_buy_market_config',
    payload,
  )
}
