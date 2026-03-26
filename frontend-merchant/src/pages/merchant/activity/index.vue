<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { activityService, type MerchantActivityItem } from '../../../services/activity'

const router = useRouter()
const loading = ref(false)
const keyword = ref('')
const statusFilter = ref<'全部' | '草稿' | '已绑定' | '生效中'>('全部')
const activityList = ref<MerchantActivityItem[]>([])

const loadActivityList = async () => {
  loading.value = true
  try {
    const list = await activityService.queryList({
      keyword: keyword.value,
      status: statusFilter.value,
    })
    activityList.value = Array.isArray(list) ? list : []
  } catch (error: any) {
    ElMessage.error(error?.message || '加载活动列表失败')
  } finally {
    loading.value = false
  }
}

const filteredActivityList = computed(() => {
  return activityList.value.filter((item) => {
    const matchKeyword =
      !keyword.value ||
      item.activityName.includes(keyword.value) ||
      item.activityId.includes(keyword.value)

    const matchStatus = statusFilter.value === '全部' || item.status === statusFilter.value
    return matchKeyword && matchStatus
  })
})

const goCreate = () => {
  router.push('/merchant/activity/edit?from=activity')
}

const goEdit = (activityId: string) => {
  router.push(`/merchant/activity/edit?activityId=${activityId}&from=activity`)
}

const goBinding = () => {
  router.push('/merchant/binding?from=activity')
}

const statusClassMap: Record<string, string> = {
  草稿: 'bg-slate-100 text-slate-600',
  已绑定: 'bg-amber-100 text-amber-700',
  生效中: 'bg-rose-100 text-rose-600',
}

onMounted(() => {
  loadActivityList()
})
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-800">
    <div class="mx-auto max-w-7xl px-6 py-8">
      <div class="mb-6 flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <p class="mb-2 text-sm font-medium text-rose-500">Merchant / Activity</p>
          <h1 class="text-3xl font-bold tracking-tight text-slate-900">拼团活动管理</h1>
          <p class="mt-2 text-sm text-slate-500">
            管理拼团活动、查看状态，并从这里进入活动编辑和商品绑定流程。
          </p>
        </div>

        <div class="flex flex-wrap gap-3">
          <button
            class="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 shadow-sm transition hover:border-slate-300 hover:bg-slate-50"
            @click="goBinding"
          >
            前往商品绑定
          </button>
          <button
            class="rounded-2xl bg-gradient-to-r from-rose-500 to-red-500 px-5 py-2 text-sm font-semibold text-white shadow-lg shadow-rose-200 transition hover:scale-[1.01]"
            @click="goCreate"
          >
            + 新建活动
          </button>
        </div>
      </div>

      <div class="mb-6 grid gap-4 md:grid-cols-4">
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">活动总数</p>
          <p class="mt-3 text-3xl font-bold text-slate-900">{{ activityList.length }}</p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">生效中活动</p>
          <p class="mt-3 text-3xl font-bold text-rose-500">
            {{ activityList.filter(item => item.status === '生效中').length }}
          </p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">已绑定活动</p>
          <p class="mt-3 text-3xl font-bold text-amber-500">
            {{ activityList.filter(item => item.status === '已绑定').length }}
          </p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">草稿活动</p>
          <p class="mt-3 text-3xl font-bold text-slate-700">
            {{ activityList.filter(item => item.status === '草稿').length }}
          </p>
        </div>
      </div>

      <div class="mb-6 rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
        <div class="grid gap-4 lg:grid-cols-[1.4fr_1fr_auto_auto]">
          <div>
            <label class="mb-2 block text-sm font-medium text-slate-700">关键词搜索</label>
            <input
              v-model="keyword"
              type="text"
              placeholder="输入活动名或 activityId"
              class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-rose-300 focus:bg-white"
            />
          </div>

          <div>
            <label class="mb-2 block text-sm font-medium text-slate-700">活动状态</label>
            <select
              v-model="statusFilter"
              class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-rose-300 focus:bg-white"
            >
              <option>全部</option>
              <option>草稿</option>
              <option>已绑定</option>
              <option>生效中</option>
            </select>
          </div>

          <div class="flex items-end">
            <button
              class="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-medium text-slate-700 transition hover:border-slate-300 hover:bg-slate-50 lg:w-auto"
              @click="keyword = ''; statusFilter = '全部'; loadActivityList()"
            >
              重置筛选
            </button>
          </div>

          <div class="flex items-end">
            <button
              class="w-full rounded-2xl border border-slate-200 bg-slate-900 px-4 py-3 text-sm font-medium text-white transition hover:bg-slate-800 lg:w-auto"
              @click="loadActivityList"
            >
              搜索 / 刷新
            </button>
          </div>
        </div>
      </div>

      <div class="grid gap-5 xl:grid-cols-2">
        <div
          v-for="activity in filteredActivityList"
          :key="activity.activityId"
          class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100"
        >
          <div class="mb-4 flex items-start justify-between gap-4">
            <div>
              <div class="text-lg font-semibold text-slate-900">{{ activity.activityName }}</div>
              <div class="mt-1 text-sm text-slate-500">activityId：{{ activity.activityId }}</div>
            </div>
            <span
              class="rounded-full px-3 py-1 text-xs font-medium"
              :class="statusClassMap[activity.status]"
            >
              {{ activity.status }}
            </span>
          </div>

          <div class="grid gap-3 md:grid-cols-2 xl:grid-cols-2">
            <div class="rounded-2xl bg-slate-50 p-4">
              <p class="text-xs text-slate-500">discountId</p>
              <p class="mt-2 text-sm font-medium text-slate-800">{{ activity.discountId }}</p>
            </div>
            <div class="rounded-2xl bg-slate-50 p-4">
              <p class="text-xs text-slate-500">groupType</p>
              <p class="mt-2 text-sm font-medium text-slate-800">{{ activity.groupType }}</p>
            </div>
            <div class="rounded-2xl bg-slate-50 p-4">
              <p class="text-xs text-slate-500">成团人数</p>
              <p class="mt-2 text-sm font-medium text-slate-800">{{ activity.targetCount }} 人</p>
            </div>
            <div class="rounded-2xl bg-slate-50 p-4">
              <p class="text-xs text-slate-500">参与上限</p>
              <p class="mt-2 text-sm font-medium text-slate-800">{{ activity.takeLimitCount }} 次</p>
            </div>
            <div class="rounded-2xl bg-slate-50 p-4 md:col-span-2 xl:col-span-2">
              <p class="text-xs text-slate-500">有效时长</p>
              <p class="mt-2 text-sm font-medium text-slate-800">{{ activity.validTime }} 小时</p>
            </div>
          </div>

          <div class="mt-5 flex flex-wrap gap-3">
            <button
              class="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:border-slate-300 hover:bg-slate-50"
              @click="goEdit(activity.activityId)"
            >
              编辑活动
            </button>
            <button
              class="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-2 text-sm font-medium text-rose-600 transition hover:bg-rose-100"
              @click="goBinding"
            >
              前往绑定
            </button>
          </div>
        </div>

        <div
          v-if="!loading && !filteredActivityList.length"
          class="rounded-3xl bg-white p-10 text-center text-sm text-slate-400 shadow-sm ring-1 ring-slate-100 xl:col-span-2"
        >
          当前没有符合条件的活动
        </div>
      </div>
    </div>
  </div>
</template>
