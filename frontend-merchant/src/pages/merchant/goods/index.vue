<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { skuService, type MerchantSkuItem } from '../../../services/sku'

const router = useRouter()
const loading = ref(false)
const keyword = ref('')
const activityFilter = ref<'全部' | '未绑定' | '已绑定' | '生效中'>('全部')
const goodsList = ref<MerchantSkuItem[]>([])

const loadGoodsList = async () => {
  loading.value = true
  try {
    const list = await skuService.queryList({
      keyword: keyword.value,
      activityStatus: activityFilter.value,
    })
    goodsList.value = Array.isArray(list) ? list : []
  } catch (error: any) {
    ElMessage.error(error?.message || '加载商品列表失败')
  } finally {
    loading.value = false
  }
}

const filteredGoodsList = computed(() => goodsList.value)

const formatPrice = (price: number) => `¥ ${(price / 100).toFixed(2)}`

const statusClassMap: Record<string, string> = {
  未绑定: 'bg-slate-100 text-slate-600',
  已绑定: 'bg-amber-100 text-amber-700',
  生效中: 'bg-rose-100 text-rose-600',
}

const goCreate = () => {
  router.push('/merchant/goods/edit?from=goods')
}

const goEdit = (goodsId: string) => {
  router.push(`/merchant/goods/edit?goodsId=${goodsId}&from=goods`)
}

const goBinding = (goodsId?: string) => {
  const path = goodsId ? `/merchant/binding?goodsId=${goodsId}&from=goods` : '/merchant/binding?from=goods'
  router.push(path)
}

const handleDelete = async (goodsId: string) => {
  try {
    await ElMessageBox.confirm(
      `确认删除商品 ${goodsId} 吗？`,
      '删除确认',
      {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )

    await skuService.remove(goodsId)
    goodsList.value = goodsList.value.filter((item) => item.goodsId !== goodsId)
    ElMessage.success('商品已删除')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadGoodsList()
})
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-800">
    <div class="mx-auto max-w-7xl px-6 py-8">
      <div class="mb-6 flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <p class="mb-2 text-sm font-medium text-rose-500">Merchant / Goods</p>
          <h1 class="text-3xl font-bold tracking-tight text-slate-900">商品管理</h1>
          <p class="mt-2 text-sm text-slate-500">
            管理拼团商品、查看绑定状态，并为后续活动配置准备商品基础数据。
          </p>
        </div>

        <div class="flex flex-wrap gap-3">
          <button
            class="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 shadow-sm transition hover:border-slate-300 hover:bg-slate-50"
            @click="goBinding()"
          >
            前往商品绑定
          </button>
          <button
            class="rounded-2xl bg-gradient-to-r from-rose-500 to-red-500 px-5 py-2 text-sm font-semibold text-white shadow-lg shadow-rose-200 transition hover:scale-[1.01]"
            @click="goCreate"
          >
            + 新建商品
          </button>
        </div>
      </div>

      <div class="mb-6 grid gap-4 md:grid-cols-4">
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">商品总数</p>
          <p class="mt-3 text-3xl font-bold text-slate-900">{{ goodsList.length }}</p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">生效中商品</p>
          <p class="mt-3 text-3xl font-bold text-rose-500">
            {{ goodsList.filter(item => item.activityStatus === '生效中').length }}
          </p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">已绑定商品</p>
          <p class="mt-3 text-3xl font-bold text-amber-500">
            {{ goodsList.filter(item => item.activityStatus === '已绑定').length }}
          </p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">未绑定商品</p>
          <p class="mt-3 text-3xl font-bold text-slate-700">
            {{ goodsList.filter(item => item.activityStatus === '未绑定').length }}
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
              placeholder="输入商品名或 goodsId"
              class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-rose-300 focus:bg-white"
            />
          </div>

          <div>
            <label class="mb-2 block text-sm font-medium text-slate-700">活动状态</label>
            <select
              v-model="activityFilter"
              class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-rose-300 focus:bg-white"
            >
              <option>全部</option>
              <option>未绑定</option>
              <option>已绑定</option>
              <option>生效中</option>
            </select>
          </div>

          <div class="flex items-end">
            <button
              class="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-medium text-slate-700 transition hover:border-slate-300 hover:bg-slate-50 lg:w-auto"
              @click="keyword = ''; activityFilter = '全部'; loadGoodsList()"
            >
              重置筛选
            </button>
          </div>

          <div class="flex items-end">
            <button
              class="w-full rounded-2xl border border-slate-200 bg-slate-900 px-4 py-3 text-sm font-medium text-white transition hover:bg-slate-800 lg:w-auto"
              @click="loadGoodsList"
            >
              搜索 / 刷新
            </button>
          </div>
        </div>
      </div>

      <div class="overflow-hidden rounded-3xl bg-white shadow-sm ring-1 ring-slate-100">
        <div class="flex items-center justify-between border-b border-slate-100 px-6 py-4">
          <div>
            <h2 class="text-lg font-semibold text-slate-900">商品列表</h2>
            <p class="mt-1 text-sm text-slate-500">当前用于拼团活动配置的商品基础数据</p>
          </div>
          <span class="rounded-full bg-slate-100 px-3 py-1 text-xs font-medium text-slate-600">
            {{ filteredGoodsList.length }} 条结果
          </span>
        </div>

        <div v-if="loading" class="px-6 py-10 text-sm text-slate-500">正在加载商品列表...</div>

        <div v-else class="overflow-x-auto">
          <table class="min-w-full border-collapse text-left text-sm">
            <thead class="bg-slate-50 text-slate-500">
              <tr>
                <th class="px-6 py-4 font-medium">商品信息</th>
                <th class="px-6 py-4 font-medium">原价</th>
                <th class="px-6 py-4 font-medium">库存</th>
                <th class="px-6 py-4 font-medium">来源渠道</th>
                <th class="px-6 py-4 font-medium">活动状态</th>
                <th class="px-6 py-4 font-medium">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="item in filteredGoodsList"
                :key="item.goodsId"
                class="border-t border-slate-100 transition hover:bg-slate-50/80"
              >
                <td class="px-6 py-5">
                  <div class="font-medium text-slate-900">{{ item.name }}</div>
                  <div class="mt-1 text-xs text-slate-500">goodsId：{{ item.goodsId }}</div>
                </td>
                <td class="px-6 py-5 font-medium text-slate-700">{{ formatPrice(item.price) }}</td>
                <td class="px-6 py-5 text-slate-700">{{ item.totalStock }}</td>
                <td class="px-6 py-5">
                  <div class="text-slate-700">{{ item.source }}</div>
                  <div class="mt-1 text-xs text-slate-500">{{ item.channel }}</div>
                </td>
                <td class="px-6 py-5">
                  <span
                    class="inline-flex rounded-full px-3 py-1 text-xs font-medium"
                    :class="statusClassMap[item.activityStatus || '未绑定']"
                  >
                    {{ item.activityStatus }}
                  </span>
                </td>
                <td class="px-6 py-5">
                  <div class="flex flex-wrap gap-2">
                    <button
                      class="rounded-xl border border-slate-200 px-3 py-1.5 text-xs font-medium text-slate-600 transition hover:border-slate-300 hover:bg-slate-50"
                      @click="goEdit(item.goodsId)"
                    >
                      编辑
                    </button>
                    <button
                      class="rounded-xl border border-rose-200 px-3 py-1.5 text-xs font-medium text-rose-500 transition hover:bg-rose-50"
                      @click="goBinding(item.goodsId)"
                    >
                      前往绑定
                    </button>
                    <button
                      class="rounded-xl border border-slate-200 px-3 py-1.5 text-xs font-medium text-slate-400 transition hover:bg-slate-50"
                      @click="handleDelete(item.goodsId)"
                    >
                      删除
                    </button>
                  </div>
                </td>
              </tr>

              <tr v-if="!filteredGoodsList.length">
                <td colspan="6" class="px-6 py-12 text-center text-sm text-slate-400">
                  当前没有符合条件的商品
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
