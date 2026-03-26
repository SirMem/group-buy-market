<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import GoodsCard from '../../components/GoodsCard.vue'
import { CHANNEL_OPTIONS, DEFAULT_CHANNEL, DEFAULT_SOURCE, SOURCE_OPTIONS } from '../../config/constants'
import { queryMarketGoodsPage, type MarketGoodsCardDTO } from '../../services/goods'
import { useAuthStore } from '../../store/auth'

const CHANNEL_FILTER_STORAGE_KEY = 'goods_filter_selection'

const router = useRouter()
const authStore = useAuthStore()

const filters = reactive({
  source: DEFAULT_SOURCE,
  channel: DEFAULT_CHANNEL,
  keyword: '',
})

try {
  const stored = localStorage.getItem(CHANNEL_FILTER_STORAGE_KEY)
  if (stored) {
    const parsed = JSON.parse(stored) as { source?: string; channel?: string }
    if (parsed.source) filters.source = parsed.source
    if (parsed.channel) filters.channel = parsed.channel
  }
} catch {
  // ignore local parse errors
}

const loading = ref(false)
const loadingMore = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const goodsList = ref<MarketGoodsCardDTO[]>([])

const hasMore = () => goodsList.value.length < total.value

async function loadGoods(reset = false) {
  if (!authStore.userId) return
  if (reset) {
    page.value = 1
    total.value = 0
    goodsList.value = []
    loading.value = true
  } else {
    loadingMore.value = true
  }

  try {
    const response = await queryMarketGoodsPage({
      userId: authStore.userId,
      source: filters.source,
      channel: filters.channel,
      page: page.value,
      pageSize,
      keyword: filters.keyword.trim() || undefined,
      onlyEffective: true,
      includeInventory: true,
      includeTeamStatistic: true,
      includeTeamList: false,
    })

    if (!(response.code === '0000' || response.code === 200)) {
      ElMessage.error(response.info || '商品加载失败')
      return
    }

    const pageData = response.data
    total.value = pageData?.total ?? 0
    const nextList = pageData?.list ?? []
    goodsList.value = reset ? nextList : [...goodsList.value, ...nextList]
    page.value += 1
  } catch (error: any) {
    const message = error?.response?.data?.info || error?.message || '商品加载失败'
    ElMessage.error(message)
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

function searchGoods() {
  void loadGoods(true)
}

function openGoodsDetail(goodsId: string) {
  router.push(`/goods/${goodsId}`)
}

watch(
  () => [filters.source, filters.channel],
  () => {
    localStorage.setItem(
      CHANNEL_FILTER_STORAGE_KEY,
      JSON.stringify({ source: filters.source, channel: filters.channel }),
    )
    void loadGoods(true)
  },
)

onMounted(() => {
  void loadGoods(true)
})
</script>

<template>
  <div class="min-h-screen px-4 py-5">
    <section class="sirmem-surface-card p-5">
      <div class="flex items-start justify-between gap-4">
        <div>
          <p class="sirmem-kicker text-sm">欢迎回来</p>
          <h1 class="sirmem-section-title mt-1 text-2xl">拼团商品首页</h1>
          <p class="mt-2 text-sm text-slate-500">根据 source / channel 查看对应投放渠道的商品</p>
        </div>
        <el-tag type="info" round>{{ authStore.username || authStore.userId }}</el-tag>
      </div>

      <div class="mt-5">
        <p class="mb-2 text-sm font-medium text-slate-700">source</p>
        <div class="flex flex-wrap gap-2">
          <el-button
            v-for="item in SOURCE_OPTIONS"
            :key="item.value"
            :type="filters.source === item.value ? 'primary' : 'default'"
            round
            @click="filters.source = item.value"
          >
            {{ item.label }}
          </el-button>
        </div>
      </div>

      <div class="mt-5">
        <p class="mb-2 text-sm font-medium text-slate-700">channel</p>
        <div class="flex flex-wrap gap-2">
          <el-button
            v-for="item in CHANNEL_OPTIONS"
            :key="item.value"
            :type="filters.channel === item.value ? 'primary' : 'default'"
            round
            @click="filters.channel = item.value"
          >
            {{ item.label }}
          </el-button>
        </div>
      </div>

      <div class="mt-5 flex gap-3">
        <el-input v-model="filters.keyword" size="large" clearable placeholder="搜索商品名称" @keyup.enter="searchGoods" />
        <el-button type="primary" size="large" @click="searchGoods">搜索</el-button>
      </div>
    </section>

    <section class="mt-5 space-y-4">
      <div v-if="loading" class="sirmem-surface-card p-10 text-center shadow-sm">
        <el-skeleton animated :rows="6" />
      </div>

      <template v-else>
        <button
          v-for="goods in goodsList"
          :key="goods.goodsId"
          type="button"
          class="block w-full text-left"
          @click="openGoodsDetail(goods.goodsId)"
        >
          <GoodsCard :goods="goods" />
        </button>

        <div v-if="goodsList.length === 0" class="sirmem-empty-shell sirmem-empty-state">
          <span class="sirmem-empty-state__icon">◎</span>
          <div>
            <p class="sirmem-empty-state__title">暂无商品</p>
            <p class="sirmem-empty-state__description">当前筛选条件下暂无可展示商品，后续商品列表会持续复用统一的 Sirmem 空状态样式。</p>
          </div>
        </div>

        <div v-else class="pb-2 text-center">
          <el-button v-if="hasMore()" :loading="loadingMore" plain size="large" @click="loadGoods(false)">
            加载更多
          </el-button>
          <p v-else class="text-sm text-slate-400">已加载全部商品</p>
        </div>
      </template>
    </section>
  </div>
</template>
