<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { skuService } from '../../../services/sku'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const goodsId = computed(() => String(route.query.goodsId || ''))
const from = computed(() => String(route.query.from || 'goods'))
const isEdit = computed(() => Boolean(goodsId.value))

const form = reactive({
  goodsId: '',
  name: '',
  price: 0,
  totalStock: 0,
  source: 'meituan',
  channel: 'app',
})

const formErrors = reactive({
  goodsId: '',
  name: '',
  price: '',
  totalStock: '',
})

const previewPrice = computed(() => `¥ ${(Number(form.price || 0) / 100).toFixed(2)}`)
const pageDescription = computed(() => {
  return isEdit.value
    ? '修改当前商品的基础信息，更新后可继续去绑定拼团活动。'
    : '创建新的拼团商品基础数据，保存后可直接进入绑定活动流程。'
})

const resetErrors = () => {
  formErrors.goodsId = ''
  formErrors.name = ''
  formErrors.price = ''
  formErrors.totalStock = ''
}

const validateForm = () => {
  resetErrors()
  let valid = true

  if (!form.goodsId.trim()) {
    formErrors.goodsId = '请输入商品编号'
    valid = false
  }

  if (!form.name.trim()) {
    formErrors.name = '请输入商品名称'
    valid = false
  }

  if (!Number.isFinite(form.price) || form.price <= 0) {
    formErrors.price = '商品价格必须大于 0'
    valid = false
  }

  if (!Number.isFinite(form.totalStock) || form.totalStock < 0) {
    formErrors.totalStock = '库存不能小于 0'
    valid = false
  }

  return valid
}

const loadDetail = async () => {
  if (!goodsId.value) return
  loading.value = true
  try {
    const detail = await skuService.queryDetail(goodsId.value)
    if (!detail) {
      ElMessage.warning('未找到对应商品，已返回列表页')
      router.push('/merchant/goods')
      return
    }

    form.goodsId = detail.goodsId || ''
    form.name = detail.name
    form.price = detail.price
    form.totalStock = detail.totalStock
    form.source = detail.source
    form.channel = detail.channel
  } catch (error: any) {
    ElMessage.error(error?.message || '加载商品详情失败')
  } finally {
    loading.value = false
  }
}

const submitForm = async (nextAction: 'list' | 'binding' = 'list') => {
  if (!validateForm()) {
    ElMessage.warning('请先完善表单信息')
    return
  }

  saving.value = true
  try {
    if (isEdit.value) {
      await skuService.update({ ...form })
      ElMessage.success('商品更新成功')
    } else {
      const { goodsId: _id, ...createBody } = form
      await skuService.create(createBody)
      ElMessage.success('商品创建成功')
    }

    if (nextAction === 'binding') {
      router.push(`/merchant/binding?goodsId=${form.goodsId}&from=goods`)
      return
    }

    router.push(resolveBackPath())
  } catch (error: any) {
    ElMessage.error(error?.message || '提交失败')
  } finally {
    saving.value = false
  }
}

const resolveBackPath = () => {
  if (from.value === 'activity') return '/merchant/activity'
  if (from.value === 'dashboard') return '/merchant/dashboard'
  return '/merchant/goods'
}

const goBack = () => {
  router.push(resolveBackPath())
}

onMounted(() => {
  loadDetail()
})
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-800">
    <div class="mx-auto max-w-5xl px-6 py-8">
      <div class="mb-6 flex flex-wrap items-center justify-between gap-4">
        <div>
          <p class="mb-2 text-sm font-medium text-rose-500">Merchant / Goods / Edit</p>
          <h1 class="text-3xl font-bold tracking-tight text-slate-900">
            {{ isEdit ? '编辑商品' : '新建商品' }}
          </h1>
          <p class="mt-2 text-sm text-slate-500">
            {{ pageDescription }}
          </p>
        </div>

        <button
          class="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 shadow-sm transition hover:border-slate-300 hover:bg-slate-50"
          @click="goBack"
        >
          返回商品列表
        </button>
      </div>

      <div class="mb-6 grid gap-4 md:grid-cols-3">
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">当前模式</p>
          <p class="mt-3 text-2xl font-bold text-slate-900">{{ isEdit ? '编辑模式' : '创建模式' }}</p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">预览价格</p>
          <p class="mt-3 text-2xl font-bold text-rose-500">{{ previewPrice }}</p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">下一步建议</p>
          <p class="mt-3 text-base font-medium text-slate-700">保存后进入商品活动绑定</p>
        </div>
      </div>

      <div v-if="loading" class="rounded-3xl bg-white px-6 py-10 text-sm text-slate-500 shadow-sm ring-1 ring-slate-100">
        正在加载商品信息...
      </div>

      <div v-else class="space-y-6">
        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="mb-6 flex items-center justify-between">
            <div>
              <h2 class="text-lg font-semibold text-slate-900">基础信息</h2>
              <p class="mt-1 text-sm text-slate-500">填写商品最核心的结构化数据</p>
            </div>
            <span class="rounded-full bg-rose-50 px-3 py-1 text-xs font-medium text-rose-500">
              {{ isEdit ? '编辑模式' : '创建模式' }}
            </span>
          </div>

          <div class="grid gap-5 md:grid-cols-2">
            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">商品编号 goodsId</label>
              <input
                v-model="form.goodsId"
                type="text"
                placeholder="请输入商品编号"
                class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
                :class="formErrors.goodsId ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'"
              />
              <p v-if="formErrors.goodsId" class="mt-2 text-xs text-red-500">{{ formErrors.goodsId }}</p>
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">商品名称</label>
              <input
                v-model="form.name"
                type="text"
                placeholder="请输入商品名称"
                class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
                :class="formErrors.name ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'"
              />
              <p v-if="formErrors.name" class="mt-2 text-xs text-red-500">{{ formErrors.name }}</p>
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">商品价格（分）</label>
              <input
                v-model.number="form.price"
                type="number"
                min="0"
                placeholder="请输入商品价格"
                class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
                :class="formErrors.price ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'"
              />
              <p class="mt-2 text-xs text-slate-400">面向后端传值时默认按“分”为单位，当前预览价格：{{ previewPrice }}</p>
              <p v-if="formErrors.price" class="mt-1 text-xs text-red-500">{{ formErrors.price }}</p>
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">总库存</label>
              <input
                v-model.number="form.totalStock"
                type="number"
                min="0"
                placeholder="请输入库存"
                class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
                :class="formErrors.totalStock ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'"
              />
              <p v-if="formErrors.totalStock" class="mt-2 text-xs text-red-500">{{ formErrors.totalStock }}</p>
            </div>
          </div>
        </section>

        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="mb-6">
            <h2 class="text-lg font-semibold text-slate-900">来源渠道信息</h2>
            <p class="mt-1 text-sm text-slate-500">用于区分商品来源和投放渠道</p>
          </div>

          <div class="grid gap-5 md:grid-cols-2">
            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">source</label>
              <select
                v-model="form.source"
                class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-rose-300 focus:bg-white"
              >
                <option value="meituan">meituan</option>
                <option value="douyin">douyin</option>
                <option value="xiaohongshu">xiaohongshu</option>
              </select>
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">channel</label>
              <select
                v-model="form.channel"
                class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-rose-300 focus:bg-white"
              >
                <option value="app">app</option>
                <option value="miniapp">miniapp</option>
                <option value="h5">h5</option>
              </select>
            </div>
          </div>
        </section>

        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="mb-4">
            <h2 class="text-lg font-semibold text-slate-900">操作建议</h2>
            <p class="mt-1 text-sm text-slate-500">第一版商家后台推荐路径：创建/编辑商品 → 去绑定活动 → 进入后续运营配置</p>
          </div>

          <div class="rounded-2xl bg-slate-50 p-4 text-sm text-slate-600">
            <p>建议规则：</p>
            <ul class="mt-2 list-disc space-y-1 pl-5">
              <li>goodsId 在商家端应保持唯一，避免后续绑定关系冲突</li>
              <li>价格统一以“分”为单位，避免前后端精度问题</li>
              <li>source + channel 组合应与后续活动投放场景保持一致</li>
            </ul>
          </div>
        </section>

        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="flex flex-wrap items-center justify-between gap-3">
            <div>
              <h2 class="text-lg font-semibold text-slate-900">提交动作</h2>
              <p class="mt-1 text-sm text-slate-500">你可以先保存返回列表，也可以保存后直接进入绑定活动页</p>
            </div>

            <div class="flex flex-wrap gap-3">
              <button
                class="rounded-2xl border border-slate-200 bg-white px-5 py-2.5 text-sm font-medium text-slate-700 transition hover:border-slate-300 hover:bg-slate-50"
                @click="goBack"
              >
                取消
              </button>
              <button
                class="rounded-2xl border border-rose-200 bg-rose-50 px-5 py-2.5 text-sm font-medium text-rose-600 transition hover:bg-rose-100 disabled:cursor-not-allowed disabled:opacity-60"
                :disabled="saving"
                @click="submitForm('binding')"
              >
                {{ saving ? '提交中...' : '保存并去绑定活动' }}
              </button>
              <button
                class="rounded-2xl bg-gradient-to-r from-rose-500 to-red-500 px-5 py-2.5 text-sm font-semibold text-white shadow-lg shadow-rose-200 transition hover:scale-[1.01] disabled:cursor-not-allowed disabled:opacity-60"
                :disabled="saving"
                @click="submitForm('list')"
              >
                {{ saving ? '提交中...' : isEdit ? '保存修改' : '创建商品' }}
              </button>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>
