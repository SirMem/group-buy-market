<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { discountService, LABEL_TO_MARKET_PLAN } from '../../../services/discount'

const route = useRoute()
const router = useRouter()

const discountId = computed(() => String(route.query.discountId || ''))
const from = computed(() => String(route.query.from || 'discount'))
const isEdit = computed(() => Boolean(discountId.value))
const loading = ref(false)
const saving = ref(false)

const form = reactive({
  discountId: '',
  discountName: '',
  discountType: '直减',
  discountDesc: '',
  marketExpr: '',
  status: '草稿',
})

const formErrors = reactive({
  discountId: '',
  discountName: '',
  discountType: '',
  discountDesc: '',
})

const pageDescription = computed(() => {
  return isEdit.value
    ? '修改当前折扣规则的名称、类型和说明，用于活动编辑页继续关联。'
    : '创建新的折扣规则，供后续拼团活动配置时选择和关联。'
})

const typeDescriptionMap: Record<string, string> = {
  满减: '适合订单金额达到阈值后减固定金额的营销场景。',
  直减: '适合拼团成功后直接减固定金额的营销场景。',
  折扣: '适合按百分比折扣结算的营销场景。',
}

const currentTypeDescription = computed(() => typeDescriptionMap[form.discountType] || '请选择折扣类型')

const resetErrors = () => {
  formErrors.discountId = ''
  formErrors.discountName = ''
  formErrors.discountType = ''
  formErrors.discountDesc = ''
}

const validateForm = () => {
  resetErrors()
  let valid = true

  if (!form.discountId.trim()) {
    formErrors.discountId = '请输入折扣编号'
    valid = false
  }

  if (!form.discountName.trim()) {
    formErrors.discountName = '请输入折扣名称'
    valid = false
  }

  if (!form.discountType) {
    formErrors.discountType = '请选择折扣类型'
    valid = false
  }

  if (!form.discountDesc.trim()) {
    formErrors.discountDesc = '请输入规则说明'
    valid = false
  }

  return valid
}

const loadDetail = async () => {
  if (!discountId.value) return
  loading.value = true
  try {
    const detail = await discountService.queryDetail({ discountId: discountId.value })
    if (!detail) {
      ElMessage.warning('未找到对应折扣规则，已返回列表页')
      router.push('/merchant/discount')
      return
    }

    form.discountId = detail.discountId || ''
    form.discountName = detail.discountName || ''
    form.discountType = detail.discountType || '直减'
    form.discountDesc = detail.discountDesc || ''
    form.marketExpr = detail.marketExpr || ''
    form.status = detail.status || '草稿'
  } catch (error: any) {
    ElMessage.error(error?.message || '加载折扣详情失败')
  } finally {
    loading.value = false
  }
}

const resolveBackPath = () => {
  if (from.value === 'activity') return '/merchant/activity'
  if (from.value === 'dashboard') return '/merchant/dashboard'
  return '/merchant/discount'
}

const submitForm = async (nextAction: 'list' | 'activity' = 'list') => {
  if (!validateForm()) {
    ElMessage.warning('请先完善折扣规则表单信息')
    return
  }

  saving.value = true
  try {
    const payload = {
      discountId: form.discountId,
      discountName: form.discountName,
      discountDesc: form.discountDesc,
      discountType: 0,
      marketPlan: LABEL_TO_MARKET_PLAN[form.discountType] || 'ZJ',
      marketExpr: form.marketExpr,
    }
    if (isEdit.value) {
      await discountService.update(payload)
      ElMessage.success('折扣规则更新成功')
    } else {
      await discountService.create(payload)
      ElMessage.success('折扣规则创建成功')
    }

    if (nextAction === 'activity') {
      router.push('/merchant/activity/edit?from=discount')
      return
    }

    router.push(resolveBackPath())
  } catch (error: any) {
    ElMessage.error(error?.message || '提交失败')
  } finally {
    saving.value = false
  }
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
          <p class="mb-2 text-sm font-medium text-rose-500">Merchant / Discount / Edit</p>
          <h1 class="text-3xl font-bold tracking-tight text-slate-900">
            {{ isEdit ? '编辑折扣规则' : '新建折扣规则' }}
          </h1>
          <p class="mt-2 text-sm text-slate-500">{{ pageDescription }}</p>
        </div>

        <button
          class="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 shadow-sm transition hover:border-slate-300 hover:bg-slate-50"
          @click="router.push(resolveBackPath())"
        >
          返回折扣列表
        </button>
      </div>

      <div class="mb-6 grid gap-4 md:grid-cols-3">
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">当前模式</p>
          <p class="mt-3 text-2xl font-bold text-slate-900">{{ isEdit ? '编辑模式' : '创建模式' }}</p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">当前规则类型</p>
          <p class="mt-3 text-2xl font-bold text-rose-500">{{ form.discountType }}</p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">下一步建议</p>
          <p class="mt-3 text-base font-medium text-slate-700">保存后进入活动编辑页</p>
        </div>
      </div>

      <div v-if="loading" class="rounded-3xl bg-white px-6 py-10 text-sm text-slate-500 shadow-sm ring-1 ring-slate-100">
        正在加载折扣规则数据...
      </div>

      <div v-else class="space-y-6">
        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="mb-6">
            <h2 class="text-lg font-semibold text-slate-900">基础信息</h2>
            <p class="mt-1 text-sm text-slate-500">配置折扣规则的编号、名称等基本标识</p>
          </div>

          <div class="grid gap-5 md:grid-cols-2">
            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">折扣编号 discountId</label>
              <input
                v-model="form.discountId"
                type="text"
                placeholder="请输入折扣编号"
                class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
                :class="formErrors.discountId ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'"
              />
              <p v-if="formErrors.discountId" class="mt-2 text-xs text-red-500">{{ formErrors.discountId }}</p>
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">折扣名称</label>
              <input
                v-model="form.discountName"
                type="text"
                placeholder="请输入折扣名称"
                class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
                :class="formErrors.discountName ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'"
              />
              <p v-if="formErrors.discountName" class="mt-2 text-xs text-red-500">{{ formErrors.discountName }}</p>
            </div>
          </div>
        </section>

        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="mb-6">
            <h2 class="text-lg font-semibold text-slate-900">规则类型</h2>
            <p class="mt-1 text-sm text-slate-500">选择折扣规则的营销类型</p>
          </div>

          <div class="grid gap-4 md:grid-cols-3">
            <label
              v-for="type in ['满减', '直减', '折扣']"
              :key="type"
              class="block cursor-pointer rounded-2xl border p-4 transition"
              :class="form.discountType === type ? 'border-rose-300 bg-rose-50' : 'border-slate-200 hover:border-slate-300 hover:bg-slate-50'"
            >
              <div class="flex items-start gap-3">
                <input v-model="form.discountType" type="radio" class="mt-1" :value="type" />
                <div>
                  <div class="text-sm font-semibold text-slate-900">{{ type }}</div>
                  <div class="mt-1 text-xs text-slate-500">{{ typeDescriptionMap[type] }}</div>
                </div>
              </div>
            </label>
          </div>

          <div class="mt-4 rounded-2xl bg-slate-50 p-4 text-sm text-slate-600">
            <p class="font-medium text-slate-800">当前类型说明</p>
            <p class="mt-2">{{ currentTypeDescription }}</p>
          </div>
        </section>

        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="mb-6">
            <h2 class="text-lg font-semibold text-slate-900">规则说明</h2>
            <p class="mt-1 text-sm text-slate-500">用自然语言描述该折扣规则会如何生效</p>
          </div>

          <div>
            <label class="mb-2 block text-sm font-medium text-slate-700">规则说明</label>
            <textarea
              v-model="form.discountDesc"
              rows="5"
              placeholder="例如：成团后订单立减 20 元"
              class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
              :class="formErrors.discountDesc ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'"
            />
            <p v-if="formErrors.discountDesc" class="mt-2 text-xs text-red-500">{{ formErrors.discountDesc }}</p>
          </div>

          <div class="mt-5">
            <label class="mb-2 block text-sm font-medium text-slate-700">优惠表达式 marketExpr</label>
            <input
              v-model="form.marketExpr"
              type="text"
              placeholder="直减填数字如 20，满减填如 99,30，折扣填如 0.8"
              class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-rose-300 focus:bg-white"
            />
            <p class="mt-1.5 text-xs text-slate-400">直减(ZJ): 20 → 立减20元 ｜ 满减(MJ): 99,30 → 满99减30 ｜ 折扣(N): 0.8 → 打8折</p>
          </div>
        </section>

        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="mb-6">
            <h2 class="text-lg font-semibold text-slate-900">状态设置</h2>
            <p class="mt-1 text-sm text-slate-500">控制折扣规则在后台中的当前状态</p>
          </div>

          <div class="grid gap-5 md:grid-cols-2">
            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">规则状态</label>
              <select
                v-model="form.status"
                class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-rose-300 focus:bg-white"
              >
                <option value="草稿">草稿</option>
                <option value="生效中">生效中</option>
              </select>
            </div>
          </div>
        </section>

        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="flex flex-wrap items-center justify-between gap-3">
            <div>
              <h2 class="text-lg font-semibold text-slate-900">提交动作</h2>
              <p class="mt-1 text-sm text-slate-500">保存后可返回折扣列表，也可以继续进入活动编辑页</p>
            </div>

            <div class="flex flex-wrap gap-3">
              <button
                class="rounded-2xl border border-slate-200 bg-white px-5 py-2.5 text-sm font-medium text-slate-700 transition hover:border-slate-300 hover:bg-slate-50"
                @click="router.push(resolveBackPath())"
              >
                取消
              </button>
              <button
                class="rounded-2xl border border-rose-200 bg-rose-50 px-5 py-2.5 text-sm font-medium text-rose-600 transition hover:bg-rose-100 disabled:cursor-not-allowed disabled:opacity-60"
                :disabled="saving"
                @click="submitForm('activity')"
              >
                {{ saving ? '提交中...' : '保存并去活动编辑' }}
              </button>
              <button
                class="rounded-2xl bg-gradient-to-r from-rose-500 to-red-500 px-5 py-2.5 text-sm font-semibold text-white shadow-lg shadow-rose-200 transition hover:scale-[1.01] disabled:cursor-not-allowed disabled:opacity-60"
                :disabled="saving"
                @click="submitForm('list')"
              >
                {{ saving ? '提交中...' : isEdit ? '保存修改' : '创建规则' }}
              </button>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>
