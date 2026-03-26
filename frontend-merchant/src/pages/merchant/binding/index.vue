<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { skuService, type MerchantSkuFormData } from '../../../services/sku'
import { activityService, type MerchantActivityItem } from '../../../services/activity'
import { bindingService, type MerchantBindingInfo } from '../../../services/binding'

const route = useRoute()
const router = useRouter()

const goodsId = computed(() => String(route.query.goodsId || ''))
const from = computed(() => String(route.query.from || 'goods'))
const loading = ref(false)
const submitting = ref(false)
const currentGoods = ref<MerchantSkuFormData | null>(null)
const activityList = ref<MerchantActivityItem[]>([])
const selectedActivityId = ref('')
const currentBinding = ref<MerchantBindingInfo | null>(null)

const selectedActivity = computed(() => {
  return activityList.value.find((item) => item.activityId === selectedActivityId.value) || null
})

const formatPrice = (price?: number) => `¥ ${((price || 0) / 100).toFixed(2)}`

const resolveBackPath = () => {
  if (from.value === 'activity') return '/merchant/activity'
  if (from.value === 'dashboard') return '/merchant/dashboard'
  return '/merchant/goods'
}

const loadPageData = async () => {
  if (!goodsId.value) {
    ElMessage.warning('缺少 goodsId，已返回商品列表')
    router.push(resolveBackPath())
    return
  }

  loading.value = true
  try {
    const [goodsDetail, activities] = await Promise.all([
      skuService.queryDetail(goodsId.value),
      activityService.queryList(),
    ])

    if (!goodsDetail) {
      ElMessage.warning('未找到对应商品，已返回商品列表')
      router.push(resolveBackPath())
      return
    }

    currentGoods.value = goodsDetail
    activityList.value = Array.isArray(activities) ? activities : []

    const binding = await bindingService.query({
      goodsId: goodsId.value,
      source: goodsDetail.source,
      channel: goodsDetail.channel,
    })
    currentBinding.value = binding

    if (binding?.activityId) {
      selectedActivityId.value = binding.activityId
    }
  } catch (error: any) {
    ElMessage.error(error?.message || '加载绑定页数据失败')
  } finally {
    loading.value = false
  }
}

const submitBinding = async () => {
  if (!goodsId.value) {
    ElMessage.warning('缺少商品编号')
    return
  }
  if (!selectedActivityId.value) {
    ElMessage.warning('请选择要绑定的活动')
    return
  }

  submitting.value = true
  try {
    await bindingService.bind({
      goodsId: goodsId.value,
      activityId: selectedActivityId.value,
      source: currentGoods.value?.source || '',
      channel: currentGoods.value?.channel || '',
    })

    currentBinding.value = {
      goodsId: goodsId.value,
      source: currentGoods.value?.source || '',
      channel: currentGoods.value?.channel || '',
      activityId: selectedActivityId.value,
      activityName: selectedActivity.value?.activityName || '',
    }

    ElMessage.success('商品活动绑定成功')
  } catch (error: any) {
    ElMessage.error(error?.message || '绑定失败')
  } finally {
    submitting.value = false
  }
}

const handleUnbind = async () => {
  if (!goodsId.value) return
  submitting.value = true
  try {
    await bindingService.unbind({
      goodsId: goodsId.value,
      source: currentGoods.value?.source || '',
      channel: currentGoods.value?.channel || '',
    })
    currentBinding.value = null
    selectedActivityId.value = ''
    ElMessage.success('已解除绑定')
  } catch (error: any) {
    ElMessage.error(error?.message || '解绑失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadPageData()
})
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-800">
    <div class="mx-auto max-w-7xl px-6 py-8">
      <div class="mb-6 flex flex-wrap items-center justify-between gap-4">
        <div>
          <p class="mb-2 text-sm font-medium text-rose-500">Merchant / Goods / Binding</p>
          <h1 class="text-3xl font-bold tracking-tight text-slate-900">商品活动绑定</h1>
          <p class="mt-2 text-sm text-slate-500">
            为商品选择拼团活动，建立商品与活动之间的关系。
          </p>
        </div>

        <button
          class="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 shadow-sm transition hover:border-slate-300 hover:bg-slate-50"
          @click="router.push(resolveBackPath())"
        >
          返回商品管理
        </button>
      </div>

      <div v-if="loading" class="rounded-3xl bg-white px-6 py-10 text-sm text-slate-500 shadow-sm ring-1 ring-slate-100">
        正在加载绑定页数据...
      </div>

      <div v-else class="grid gap-6 xl:grid-cols-[1fr_1.2fr]">
        <section class="space-y-6">
          <div class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
            <div class="mb-5 flex items-center justify-between">
              <div>
                <h2 class="text-lg font-semibold text-slate-900">当前商品</h2>
                <p class="mt-1 text-sm text-slate-500">当前准备绑定活动的商品基础信息</p>
              </div>
              <span class="rounded-full bg-rose-50 px-3 py-1 text-xs font-medium text-rose-500">
                goodsId: {{ currentGoods?.goodsId || '-' }}
              </span>
            </div>

            <div v-if="currentGoods" class="space-y-4">
              <div>
                <div class="text-lg font-semibold text-slate-900">{{ currentGoods.name }}</div>
                <div class="mt-1 text-sm text-slate-500">商品编号：{{ currentGoods.goodsId }}</div>
              </div>

              <div class="grid gap-4 md:grid-cols-2">
                <div class="rounded-2xl bg-slate-50 p-4">
                  <p class="text-xs text-slate-500">价格</p>
                  <p class="mt-2 text-xl font-bold text-rose-500">{{ formatPrice(currentGoods.price) }}</p>
                </div>
                <div class="rounded-2xl bg-slate-50 p-4">
                  <p class="text-xs text-slate-500">库存</p>
                  <p class="mt-2 text-xl font-bold text-slate-900">{{ currentGoods.totalStock }}</p>
                </div>
              </div>

              <div class="grid gap-4 md:grid-cols-2">
                <div class="rounded-2xl border border-slate-200 p-4">
                  <p class="text-xs text-slate-500">source</p>
                  <p class="mt-2 text-sm font-medium text-slate-800">{{ currentGoods.source }}</p>
                </div>
                <div class="rounded-2xl border border-slate-200 p-4">
                  <p class="text-xs text-slate-500">channel</p>
                  <p class="mt-2 text-sm font-medium text-slate-800">{{ currentGoods.channel }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
            <div class="mb-5 flex items-center justify-between">
              <div>
                <h2 class="text-lg font-semibold text-slate-900">当前绑定状态</h2>
                <p class="mt-1 text-sm text-slate-500">查看当前商品是否已绑定拼团活动</p>
              </div>
              <span
                class="rounded-full px-3 py-1 text-xs font-medium"
                :class="currentBinding ? 'bg-amber-100 text-amber-700' : 'bg-slate-100 text-slate-600'"
              >
                {{ currentBinding ? '已绑定' : '未绑定' }}
              </span>
            </div>

            <div v-if="currentBinding" class="space-y-3">
              <div class="rounded-2xl bg-slate-50 p-4">
                <p class="text-xs text-slate-500">已绑定活动 ID</p>
                <p class="mt-2 text-base font-semibold text-slate-900">{{ currentBinding.activityId }}</p>
              </div>
              <div class="rounded-2xl bg-slate-50 p-4">
                <p class="text-xs text-slate-500">已绑定活动名称</p>
                <p class="mt-2 text-base font-semibold text-slate-900">{{ currentBinding.activityName || '当前 mock 绑定未补活动名称' }}</p>
              </div>
              <button
                class="rounded-2xl border border-red-200 bg-red-50 px-4 py-2 text-sm font-medium text-red-600 transition hover:bg-red-100"
                :disabled="submitting"
                @click="handleUnbind"
              >
                解除绑定
              </button>
            </div>

            <div v-else class="rounded-2xl bg-slate-50 p-4 text-sm text-slate-500">
              当前商品还没有绑定活动，可以从右侧选择一个活动并执行绑定。
            </div>
          </div>
        </section>

        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="mb-5 flex items-center justify-between">
            <div>
              <h2 class="text-lg font-semibold text-slate-900">选择活动</h2>
              <p class="mt-1 text-sm text-slate-500">从活动列表中选择要绑定给当前商品的拼团活动</p>
            </div>
            <span class="rounded-full bg-slate-100 px-3 py-1 text-xs font-medium text-slate-600">
              {{ activityList.length }} 个活动
            </span>
          </div>

          <div class="space-y-4">
            <label
              v-for="activity in activityList"
              :key="activity.activityId"
              class="block cursor-pointer rounded-2xl border p-4 transition"
              :class="selectedActivityId === activity.activityId ? 'border-rose-300 bg-rose-50' : 'border-slate-200 hover:border-slate-300 hover:bg-slate-50'"
            >
              <div class="flex items-start gap-3">
                <input
                  v-model="selectedActivityId"
                  type="radio"
                  class="mt-1"
                  :value="activity.activityId"
                />
                <div class="min-w-0 flex-1">
                  <div class="flex flex-wrap items-center justify-between gap-3">
                    <div>
                      <div class="text-base font-semibold text-slate-900">{{ activity.activityName }}</div>
                      <div class="mt-1 text-xs text-slate-500">activityId：{{ activity.activityId }}</div>
                    </div>
                    <span
                      class="rounded-full px-3 py-1 text-xs font-medium"
                      :class="activity.status === '生效中' ? 'bg-rose-100 text-rose-600' : activity.status === '已绑定' ? 'bg-amber-100 text-amber-700' : 'bg-slate-100 text-slate-600'"
                    >
                      {{ activity.status }}
                    </span>
                  </div>

                  <div class="mt-4 grid gap-3 md:grid-cols-4">
                    <div class="rounded-xl bg-white/80 p-3">
                      <p class="text-xs text-slate-500">discountId</p>
                      <p class="mt-1 text-sm font-medium text-slate-800">{{ activity.discountId }}</p>
                    </div>
                    <div class="rounded-xl bg-white/80 p-3">
                      <p class="text-xs text-slate-500">成团人数</p>
                      <p class="mt-1 text-sm font-medium text-slate-800">{{ activity.targetCount }} 人</p>
                    </div>
                    <div class="rounded-xl bg-white/80 p-3">
                      <p class="text-xs text-slate-500">参与上限</p>
                      <p class="mt-1 text-sm font-medium text-slate-800">{{ activity.takeLimitCount }} 次</p>
                    </div>
                    <div class="rounded-xl bg-white/80 p-3">
                      <p class="text-xs text-slate-500">有效时长</p>
                      <p class="mt-1 text-sm font-medium text-slate-800">{{ activity.validTime }} 小时</p>
                    </div>
                  </div>
                </div>
              </div>
            </label>
          </div>

          <div class="mt-6 rounded-2xl bg-slate-50 p-4 text-sm text-slate-600">
            <p class="font-medium text-slate-800">当前选中活动</p>
            <p class="mt-2">
              {{ selectedActivity ? `${selectedActivity.activityName}（${selectedActivity.activityId}）` : '你还没有选择活动' }}
            </p>
          </div>

          <div class="mt-6 flex flex-wrap gap-3">
            <button
              class="rounded-2xl border border-slate-200 bg-white px-5 py-2.5 text-sm font-medium text-slate-700 transition hover:border-slate-300 hover:bg-slate-50"
              @click="router.push(resolveBackPath())"
            >
              返回商品页
            </button>
            <button
              class="rounded-2xl bg-gradient-to-r from-rose-500 to-red-500 px-5 py-2.5 text-sm font-semibold text-white shadow-lg shadow-rose-200 transition hover:scale-[1.01] disabled:cursor-not-allowed disabled:opacity-60"
              :disabled="submitting || !selectedActivityId"
              @click="submitBinding"
            >
              {{ submitting ? '提交中...' : '确认绑定活动' }}
            </button>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>
