<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import TeamProgress from '../../components/TeamProgress.vue'
import { DEFAULT_CHANNEL, DEFAULT_SOURCE } from '../../config/constants'
import { queryGroupBuyMarketConfig, type GoodsDetailDTO } from '../../services/goods'
import { createPayOrder } from '../../services/order'
import { useAuthStore } from '../../store/auth'

const FILTER_STORAGE_KEY = 'goods_filter_selection'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const submitting = ref(false)
const detail = ref<GoodsDetailDTO | null>(null)

const goodsId = computed(() => String(route.params.goodsId || ''))
const selectedFilter = computed(() => {
  try {
    const stored = localStorage.getItem(FILTER_STORAGE_KEY)
    if (stored) {
      const parsed = JSON.parse(stored) as { source?: string; channel?: string }
      return {
        source: parsed.source || DEFAULT_SOURCE,
        channel: parsed.channel || DEFAULT_CHANNEL,
      }
    }
  } catch {
    // ignore local parse errors
  }

  return {
    source: DEFAULT_SOURCE,
    channel: DEFAULT_CHANNEL,
  }
})

const goods = computed(() => detail.value?.goods)
const statistic = computed(() => detail.value?.teamStatistic)
const teams = computed(() => detail.value?.teamList ?? [])

async function loadDetail() {
  if (!authStore.userId || !goodsId.value) return
  loading.value = true
  try {
    const response = await queryGroupBuyMarketConfig({
      userId: authStore.userId,
      source: selectedFilter.value.source,
      channel: selectedFilter.value.channel,
      goodsId: goodsId.value,
    })

    if (!(response.code === '0000' || response.code === 200) || !response.data) {
      ElMessage.error(response.info || '商品详情加载失败')
      return
    }

    detail.value = response.data
  } catch (error: any) {
    const message = error?.response?.data?.info || error?.message || '商品详情加载失败'
    ElMessage.error(message)
  } finally {
    loading.value = false
  }
}

async function submitOrder(params: { marketType: number; teamId?: string }) {
  if (!authStore.userId || !goods.value || !detail.value?.activityId) {
    ElMessage.warning('商品信息未准备完成')
    return
  }
  if (submitting.value) return

  submitting.value = true
  try {
    const response = await createPayOrder({
      userId: authStore.userId,
      productId: goods.value.goodsId,
      marketType: params.marketType,
      teamId: params.teamId,
      activityId: params.marketType === 1 ? detail.value.activityId : undefined,
    })

    if (!(response.code === '0000' || response.code === 200) || !response.data) {
      ElMessage.error(response.info || '下单失败')
      return
    }

    window.open(response.data, '_blank', 'noopener,noreferrer')
    ElMessage.success('下单成功，已为你打开支付页面，请完成支付后前往订单页查看状态')
  } catch (error: any) {
    const message = error?.response?.data?.info || error?.message || '下单失败'
    ElMessage.error(message)
  } finally {
    submitting.value = false
  }
}

function joinTeam(teamId: string) {
  void submitOrder({ marketType: 1, teamId })
}

function buyAlone() {
  void submitOrder({ marketType: 0 })
}

function buyByGroup() {
  void submitOrder({ marketType: 1 })
}

onMounted(() => {
  void loadDetail()
})
</script>

<template>
  <div class="min-h-screen px-4 py-5 pb-28">
    <div class="mb-4 flex items-center gap-3">
      <el-button circle plain @click="router.back()">
        <span class="text-base">←</span>
      </el-button>
      <div>
        <h1 class="text-xl font-bold text-slate-900">商品详情</h1>
        <p class="text-sm text-slate-500">当前渠道：{{ selectedFilter.source }} / {{ selectedFilter.channel }}</p>
      </div>
    </div>

    <div v-if="loading" class="rounded-3xl bg-white p-8 shadow-sm ring-1 ring-slate-100">
      <el-skeleton animated :rows="8" />
    </div>

    <template v-else-if="detail && goods">
      <section class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
        <p class="text-sm font-medium text-rose-500">goodsId：{{ goods.goodsId }}</p>
        <h2 class="mt-2 text-2xl font-bold text-slate-900">拼团商品</h2>
        <div class="mt-5 flex items-end gap-3">
          <span class="text-3xl font-bold text-rose-500">¥{{ goods.payPrice }}</span>
          <span class="text-base text-slate-400 line-through">¥{{ goods.originalPrice }}</span>
          <span class="rounded-full bg-emerald-50 px-3 py-1 text-sm font-medium text-emerald-600">
            优惠 ¥{{ goods.deductionPrice }}
          </span>
        </div>
      </section>

      <section class="mt-4 rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
        <h3 class="text-lg font-semibold text-slate-900">拼团统计</h3>
        <div class="mt-4 grid grid-cols-3 gap-3 text-center">
          <div class="rounded-2xl bg-slate-50 p-4">
            <p class="text-xl font-bold text-slate-900">{{ statistic?.allTeamCount ?? 0 }}</p>
            <p class="mt-1 text-sm text-slate-500">开团数</p>
          </div>
          <div class="rounded-2xl bg-slate-50 p-4">
            <p class="text-xl font-bold text-slate-900">{{ statistic?.allTeamCompleteCount ?? 0 }}</p>
            <p class="mt-1 text-sm text-slate-500">成团数</p>
          </div>
          <div class="rounded-2xl bg-slate-50 p-4">
            <p class="text-xl font-bold text-slate-900">{{ statistic?.allTeamUserCount ?? 0 }}</p>
            <p class="mt-1 text-sm text-slate-500">参团人数</p>
          </div>
        </div>
      </section>

      <section class="mt-4 rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold text-slate-900">正在拼团</h3>
          <p class="text-sm text-slate-400">可加入已有团，或自己开新团</p>
        </div>

        <div v-if="teams.length" class="mt-4 space-y-3">
          <div v-for="team in teams" :key="team.teamId" class="rounded-3xl border border-slate-100 p-3">
            <TeamProgress :team="team" />
            <el-button class="mt-3 w-full" type="primary" plain :loading="submitting" @click="joinTeam(team.teamId)">
              加入此团
            </el-button>
          </div>
        </div>

        <div v-else class="mt-4 rounded-2xl bg-slate-50 p-8 text-center text-sm text-slate-500">
          暂无进行中的团队，成为第一个开团的人吧
        </div>
      </section>
    </template>

    <div v-else class="rounded-3xl bg-white p-10 text-center text-sm text-slate-500 shadow-sm ring-1 ring-slate-100">
      商品详情加载失败
    </div>

    <div class="fixed bottom-0 left-0 right-0 mx-auto max-w-[480px] border-t border-slate-200 bg-white p-4">
      <div class="mb-3 flex items-center justify-between text-sm text-slate-500">
        <span>支付完成后可前往订单页确认状态</span>
        <button class="font-medium text-rose-500" type="button" @click="router.push('/orders')">去订单页</button>
      </div>
      <div class="flex gap-3">
        <el-button class="!h-12 flex-1" size="large" :loading="submitting" @click="buyAlone">
          单独购买 {{ goods ? `¥${goods.originalPrice}` : '' }}
        </el-button>
        <el-button class="!h-12 flex-1" size="large" type="primary" :loading="submitting" @click="buyByGroup">
          拼团购买 {{ goods ? `¥${goods.payPrice}` : '' }}
        </el-button>
      </div>
    </div>
  </div>
</template>
