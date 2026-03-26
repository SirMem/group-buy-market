<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { discountService, type MerchantDiscountItem } from '../../../services/discount'

const router = useRouter()
const loading = ref(false)
const keyword = ref('')
const statusFilter = ref<'全部' | '草稿' | '生效中'>('全部')
const discountList = ref<MerchantDiscountItem[]>([])

const loadDiscountList = async () => {
  loading.value = true
  try {
    const list = await discountService.queryList({
      keyword: keyword.value,
      status: statusFilter.value,
    })
    discountList.value = Array.isArray(list) ? list : []
  } catch (error: any) {
    ElMessage.error(error?.message || '加载折扣列表失败')
  } finally {
    loading.value = false
  }
}

const filteredDiscountList = computed(() => {
  return discountList.value.filter((item) => {
    const matchKeyword =
      !keyword.value ||
      item.discountName.includes(keyword.value) ||
      item.discountId.includes(keyword.value)

    const matchStatus = statusFilter.value === '全部' || item.status === statusFilter.value
    return matchKeyword && matchStatus
  })
})

const goCreate = () => {
  router.push('/merchant/discount/edit?from=discount')
}

const goEdit = (discountId: string) => {
  router.push(`/merchant/discount/edit?discountId=${discountId}&from=discount`)
}

const goActivity = () => {
  router.push('/merchant/activity?from=discount')
}

const statusClassMap: Record<string, string> = {
  草稿: 'bg-slate-100 text-slate-600',
  生效中: 'bg-rose-100 text-rose-600',
}

const typeClassMap: Record<string, string> = {
  满减: 'bg-amber-100 text-amber-700',
  直减: 'bg-rose-100 text-rose-600',
  折扣: 'bg-indigo-100 text-indigo-600',
}

onMounted(() => {
  loadDiscountList()
})
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-800">
    <div class="mx-auto max-w-7xl px-6 py-8">
      <div class="mb-6 flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <p class="mb-2 text-sm font-medium text-rose-500">Merchant / Discount</p>
          <h1 class="text-3xl font-bold tracking-tight text-slate-900">折扣规则管理</h1>
          <p class="mt-2 text-sm text-slate-500">
            管理拼团活动使用的折扣规则，从这里进入折扣编辑和活动配置流程。
          </p>
        </div>

        <div class="flex flex-wrap gap-3">
          <button
            class="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 shadow-sm transition hover:border-slate-300 hover:bg-slate-50"
            @click="goActivity"
          >
            前往活动管理
          </button>
          <button
            class="rounded-2xl bg-gradient-to-r from-rose-500 to-red-500 px-5 py-2 text-sm font-semibold text-white shadow-lg shadow-rose-200 transition hover:scale-[1.01]"
            @click="goCreate"
          >
            + 新建折扣规则
          </button>
        </div>
      </div>

      <div class="mb-6 grid gap-4 md:grid-cols-4">
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">规则总数</p>
          <p class="mt-3 text-3xl font-bold text-slate-900">{{ discountList.length }}</p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">生效中规则</p>
          <p class="mt-3 text-3xl font-bold text-rose-500">
            {{ discountList.filter(item => item.status === '生效中').length }}
          </p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">草稿规则</p>
          <p class="mt-3 text-3xl font-bold text-slate-700">
            {{ discountList.filter(item => item.status === '草稿').length }}
          </p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">规则类型数</p>
          <p class="mt-3 text-3xl font-bold text-amber-500">3</p>
        </div>
      </div>

      <div class="mb-6 rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
        <div class="grid gap-4 lg:grid-cols-[1.4fr_1fr_auto_auto]">
          <div>
            <label class="mb-2 block text-sm font-medium text-slate-700">关键词搜索</label>
            <input
              v-model="keyword"
              type="text"
              placeholder="输入折扣名或 discountId"
              class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-rose-300 focus:bg-white"
            />
          </div>

          <div>
            <label class="mb-2 block text-sm font-medium text-slate-700">规则状态</label>
            <select
              v-model="statusFilter"
              class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-rose-300 focus:bg-white"
            >
              <option>全部</option>
              <option>草稿</option>
              <option>生效中</option>
            </select>
          </div>

          <div class="flex items-end">
            <button
              class="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-medium text-slate-700 transition hover:border-slate-300 hover:bg-slate-50 lg:w-auto"
              @click="keyword = ''; statusFilter = '全部'; loadDiscountList()"
            >
              重置筛选
            </button>
          </div>

          <div class="flex items-end">
            <button
              class="w-full rounded-2xl border border-slate-200 bg-slate-900 px-4 py-3 text-sm font-medium text-white transition hover:bg-slate-800 lg:w-auto"
              @click="loadDiscountList"
            >
              搜索 / 刷新
            </button>
          </div>
        </div>
      </div>

      <div class="grid gap-5 xl:grid-cols-2">
        <div
          v-for="discount in filteredDiscountList"
          :key="discount.discountId"
          class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100"
        >
          <div class="mb-4 flex items-start justify-between gap-4">
            <div>
              <div class="text-lg font-semibold text-slate-900">{{ discount.discountName }}</div>
              <div class="mt-1 text-sm text-slate-500">discountId：{{ discount.discountId }}</div>
            </div>
            <div class="flex flex-wrap gap-2">
              <span
                class="rounded-full px-3 py-1 text-xs font-medium"
                :class="typeClassMap[discount.discountType]"
              >
                {{ discount.discountType }}
              </span>
              <span
                class="rounded-full px-3 py-1 text-xs font-medium"
                :class="statusClassMap[discount.status]"
              >
                {{ discount.status }}
              </span>
            </div>
          </div>

          <div class="rounded-2xl bg-slate-50 p-4">
            <p class="text-xs text-slate-500">规则说明</p>
            <p class="mt-2 text-sm text-slate-700 leading-6">{{ discount.discountDesc }}</p>
          </div>

          <div class="mt-5 flex flex-wrap gap-3">
            <button
              class="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:border-slate-300 hover:bg-slate-50"
              @click="goEdit(discount.discountId)"
            >
              编辑规则
            </button>
            <button
              class="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-2 text-sm font-medium text-rose-600 transition hover:bg-rose-100"
              @click="goActivity"
            >
              前往活动管理
            </button>
          </div>
        </div>

        <div
          v-if="!loading && !filteredDiscountList.length"
          class="rounded-3xl bg-white p-10 text-center text-sm text-slate-400 shadow-sm ring-1 ring-slate-100 xl:col-span-2"
        >
          当前没有符合条件的折扣规则
        </div>
      </div>
    </div>
  </div>
</template>
