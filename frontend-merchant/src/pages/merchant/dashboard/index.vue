<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { skuService, type MerchantSkuItem } from '../../../services/sku'
import { discountService, type MerchantDiscountItem } from '../../../services/discount'
import { activityService, type MerchantActivityItem } from '../../../services/activity'

const router = useRouter()
const loading = ref(false)
const goodsList = ref<MerchantSkuItem[]>([])
const discountList = ref<MerchantDiscountItem[]>([])
const activityList = ref<MerchantActivityItem[]>([])

const loadDashboardData = async () => {
  loading.value = true
  try {
    const [goods, discounts, activities] = await Promise.all([
      skuService.queryList(),
      discountService.queryList(),
      activityService.queryList(),
    ])

    goodsList.value = Array.isArray(goods) ? goods : []
    discountList.value = Array.isArray(discounts) ? discounts : []
    activityList.value = Array.isArray(activities) ? activities : []
  } catch (error: any) {
    ElMessage.error(error?.message || '加载 Dashboard 数据失败')
  } finally {
    loading.value = false
  }
}

const statCards = computed(() => [
  {
    label: '商品总数',
    value: goodsList.value.length,
    accent: 'text-slate-900',
  },
  {
    label: '折扣规则数',
    value: discountList.value.length,
    accent: 'text-amber-500',
  },
  {
    label: '拼团活动数',
    value: activityList.value.length,
    accent: 'text-rose-500',
  },
  {
    label: '已绑定商品数',
    value: goodsList.value.filter((item) => item.activityStatus === '已绑定' || item.activityStatus === '生效中').length,
    accent: 'text-indigo-500',
  },
])

const quickActions = [
  {
    title: '新建商品',
    desc: '录入新的商品基础信息，供后续活动绑定使用',
    path: '/merchant/goods/edit',
  },
  {
    title: '新建折扣规则',
    desc: '创建营销优惠规则，供活动配置时选择',
    path: '/merchant/discount/edit',
  },
  {
    title: '新建拼团活动',
    desc: '配置拼团规则、时间和折扣关联',
    path: '/merchant/activity/edit',
  },
  {
    title: '去商品绑定',
    desc: '将商品和拼团活动建立绑定关系',
    path: '/merchant/binding',
  },
]

const recentActivities = computed(() => activityList.value.slice(0, 3))
const recentDiscounts = computed(() => discountList.value.slice(0, 3))
const recentGoods = computed(() => goodsList.value.slice(0, 3))

const activityStatusClassMap: Record<string, string> = {
  草稿: 'bg-slate-100 text-slate-600',
  已绑定: 'bg-amber-100 text-amber-700',
  生效中: 'bg-rose-100 text-rose-600',
}

const discountStatusClassMap: Record<string, string> = {
  草稿: 'bg-slate-100 text-slate-600',
  生效中: 'bg-rose-100 text-rose-600',
}

const go = (path: string) => {
  router.push(path)
}

const formatPrice = (price: number) => `¥ ${(price / 100).toFixed(2)}`

onMounted(() => {
  loadDashboardData()
})
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-800">
    <div class="mx-auto max-w-7xl px-6 py-8">
      <div class="mb-6 flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <p class="mb-2 text-sm font-medium text-rose-500">Merchant / Dashboard</p>
          <h1 class="text-3xl font-bold tracking-tight text-slate-900">商家后台首页</h1>
          <p class="mt-2 text-sm text-slate-500">
            汇总商品、折扣、活动和绑定关系，帮助你快速进入商家运营配置流程。
          </p>
        </div>

        <button
          class="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 shadow-sm transition hover:border-slate-300 hover:bg-slate-50"
          @click="loadDashboardData"
        >
          刷新首页数据
        </button>
      </div>

      <div v-if="loading" class="rounded-3xl bg-white px-6 py-10 text-sm text-slate-500 shadow-sm ring-1 ring-slate-100">
        正在加载 Dashboard 数据...
      </div>

      <template v-else>
        <div class="mb-6 grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          <div
            v-for="card in statCards"
            :key="card.label"
            class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100"
          >
            <p class="text-sm text-slate-500">{{ card.label }}</p>
            <p class="mt-3 text-3xl font-bold" :class="card.accent">{{ card.value }}</p>
          </div>
        </div>

        <section class="mb-6 rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="mb-5 flex items-center justify-between">
            <div>
              <h2 class="text-lg font-semibold text-slate-900">快捷入口</h2>
              <p class="mt-1 text-sm text-slate-500">从首页直接进入最常用的商家配置动作</p>
            </div>
          </div>

          <div class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
            <button
              v-for="action in quickActions"
              :key="action.title"
              class="rounded-3xl border border-slate-200 bg-slate-50 p-5 text-left transition hover:border-rose-200 hover:bg-rose-50"
              @click="go(action.path)"
            >
              <div class="text-base font-semibold text-slate-900">{{ action.title }}</div>
              <p class="mt-2 text-sm leading-6 text-slate-500">{{ action.desc }}</p>
            </button>
          </div>
        </section>

        <div class="grid gap-6 xl:grid-cols-[1.2fr_1fr]">
          <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
            <div class="mb-5 flex items-center justify-between">
              <div>
                <h2 class="text-lg font-semibold text-slate-900">最近活动</h2>
                <p class="mt-1 text-sm text-slate-500">最近可配置和可编辑的拼团活动</p>
              </div>
              <button class="text-sm font-medium text-rose-500" @click="go('/merchant/activity')">查看全部</button>
            </div>

            <div class="space-y-4">
              <div
                v-for="activity in recentActivities"
                :key="activity.activityId"
                class="rounded-2xl border border-slate-200 p-4"
              >
                <div class="flex items-start justify-between gap-4">
                  <div>
                    <div class="text-base font-semibold text-slate-900">{{ activity.activityName }}</div>
                    <div class="mt-1 text-xs text-slate-500">activityId：{{ activity.activityId }}</div>
                  </div>
                  <span class="rounded-full px-3 py-1 text-xs font-medium" :class="activityStatusClassMap[activity.status]">
                    {{ activity.status }}
                  </span>
                </div>
                <div class="mt-4 grid gap-3 md:grid-cols-3">
                  <div class="rounded-xl bg-slate-50 p-3">
                    <p class="text-xs text-slate-500">成团人数</p>
                    <p class="mt-1 text-sm font-medium text-slate-800">{{ activity.targetCount }} 人</p>
                  </div>
                  <div class="rounded-xl bg-slate-50 p-3">
                    <p class="text-xs text-slate-500">discountId</p>
                    <p class="mt-1 text-sm font-medium text-slate-800">{{ activity.discountId }}</p>
                  </div>
                  <div class="rounded-xl bg-slate-50 p-3">
                    <p class="text-xs text-slate-500">有效时长</p>
                    <p class="mt-1 text-sm font-medium text-slate-800">{{ activity.validTime }} 小时</p>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <section class="space-y-6">
            <div class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
              <div class="mb-5 flex items-center justify-between">
                <div>
                  <h2 class="text-lg font-semibold text-slate-900">最近折扣规则</h2>
                  <p class="mt-1 text-sm text-slate-500">最近可用于活动关联的折扣配置</p>
                </div>
                <button class="text-sm font-medium text-rose-500" @click="go('/merchant/discount')">查看全部</button>
              </div>

              <div class="space-y-3">
                <div
                  v-for="discount in recentDiscounts"
                  :key="discount.discountId"
                  class="rounded-2xl border border-slate-200 p-4"
                >
                  <div class="flex items-start justify-between gap-4">
                    <div>
                      <div class="text-sm font-semibold text-slate-900">{{ discount.discountName }}</div>
                      <div class="mt-1 text-xs text-slate-500">{{ discount.discountId }}</div>
                    </div>
                    <span class="rounded-full px-3 py-1 text-xs font-medium" :class="discountStatusClassMap[discount.status]">
                      {{ discount.status }}
                    </span>
                  </div>
                  <p class="mt-2 text-xs leading-5 text-slate-500">{{ discount.discountDesc }}</p>
                </div>
              </div>
            </div>

            <div class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
              <div class="mb-5 flex items-center justify-between">
                <div>
                  <h2 class="text-lg font-semibold text-slate-900">最近商品配置</h2>
                  <p class="mt-1 text-sm text-slate-500">最近可参与拼团活动的商品</p>
                </div>
                <button class="text-sm font-medium text-rose-500" @click="go('/merchant/goods')">查看全部</button>
              </div>

              <div class="space-y-3">
                <div
                  v-for="goods in recentGoods"
                  :key="goods.goodsId"
                  class="rounded-2xl border border-slate-200 p-4"
                >
                  <div class="flex items-start justify-between gap-4">
                    <div>
                      <div class="text-sm font-semibold text-slate-900">{{ goods.name }}</div>
                      <div class="mt-1 text-xs text-slate-500">goodsId：{{ goods.goodsId }}</div>
                    </div>
                    <span class="rounded-full bg-slate-100 px-3 py-1 text-xs font-medium text-slate-600">
                      {{ goods.activityStatus || '未绑定' }}
                    </span>
                  </div>
                  <div class="mt-2 text-xs text-slate-500">
                    {{ goods.source }} / {{ goods.channel }} ｜ {{ formatPrice(goods.price) }}
                  </div>
                </div>
              </div>
            </div>
          </section>
        </div>
      </template>
    </div>
  </div>
</template>
