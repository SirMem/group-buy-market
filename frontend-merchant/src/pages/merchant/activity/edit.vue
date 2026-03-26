<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { activityService, STATUS_LABEL_TO_INT, GROUP_TYPE_STR_TO_INT } from '../../../services/activity'
import { discountService, type MerchantDiscountItem } from '../../../services/discount'

const route = useRoute()
const router = useRouter()

const activityId = computed(() => String(route.query.activityId || ''))
const from = computed(() => String(route.query.from || 'activity'))
const isEdit = computed(() => Boolean(activityId.value))
const loading = ref(false)
const saving = ref(false)
const discountOptions = ref<MerchantDiscountItem[]>([])

const form = reactive({
  activityId: '',
  activityName: '',
  activityDesc: '',
  groupType: 'TEAM',
  targetCount: 2,
  takeLimitCount: 1,
  validTime: 24,
  startTime: '',
  endTime: '',
  discountId: '',
  status: '草稿',
})

const formErrors = reactive({
  activityId: '',
  activityName: '',
  targetCount: '',
  takeLimitCount: '',
  validTime: '',
  startTime: '',
  endTime: '',
  discountId: '',
})

const pageDescription = computed(() => {
  return isEdit.value
    ? '修改当前拼团活动的规则、时间和折扣关联信息。'
    : '创建一个新的拼团活动，并配置它的成团规则与折扣关联。'
})

const selectedDiscount = computed(() => {
  return discountOptions.value.find((item) => item.discountId === form.discountId) || null
})

const resetErrors = () => {
  Object.keys(formErrors).forEach((key) => {
    formErrors[key as keyof typeof formErrors] = ''
  })
}

const validateForm = () => {
  resetErrors()
  let valid = true

  if (!form.activityId.trim()) {
    formErrors.activityId = '请输入活动编号'
    valid = false
  }

  if (!form.activityName.trim()) {
    formErrors.activityName = '请输入活动名称'
    valid = false
  }

  if (!Number.isFinite(form.targetCount) || form.targetCount < 2) {
    formErrors.targetCount = '成团人数至少为 2'
    valid = false
  }

  if (!Number.isFinite(form.takeLimitCount) || form.takeLimitCount < 1) {
    formErrors.takeLimitCount = '参与上限至少为 1'
    valid = false
  }

  if (!Number.isFinite(form.validTime) || form.validTime <= 0) {
    formErrors.validTime = '有效时长必须大于 0'
    valid = false
  }

  if (!form.startTime) {
    formErrors.startTime = '请选择开始时间'
    valid = false
  }

  if (!form.endTime) {
    formErrors.endTime = '请选择结束时间'
    valid = false
  }

  if (form.startTime && form.endTime && form.endTime <= form.startTime) {
    formErrors.endTime = '结束时间必须晚于开始时间'
    valid = false
  }

  if (!form.discountId) {
    formErrors.discountId = '请选择关联折扣规则'
    valid = false
  }

  return valid
}

const loadPageData = async () => {
  loading.value = true
  try {
    const [discounts, detail] = await Promise.all([
      discountService.queryList(),
      isEdit.value ? activityService.queryDetail({ activityId: activityId.value }) : Promise.resolve(null),
    ])

    discountOptions.value = Array.isArray(discounts) ? discounts : []

    if (detail) {
      form.activityId = detail.activityId || ''
      form.activityName = detail.activityName || ''
      form.activityDesc = detail.activityName ? `${detail.activityName} 的活动描述待补充` : ''
      form.groupType = detail.groupType || 'TEAM'
      form.targetCount = detail.targetCount || 2
      form.takeLimitCount = detail.takeLimitCount || 1
      form.validTime = detail.validTime || 24
      form.discountId = detail.discountId || ''
      form.status = detail.status || '草稿'
      form.startTime = detail.startTime || ''
      form.endTime = detail.endTime || ''
    }
  } catch (error: any) {
    ElMessage.error(error?.message || '加载活动编辑页数据失败')
  } finally {
    loading.value = false
  }
}

const resolveBackPath = () => {
  if (from.value === 'discount') return '/merchant/discount'
  if (from.value === 'dashboard') return '/merchant/dashboard'
  return '/merchant/activity'
}

const submitForm = async (nextAction: 'list' | 'binding' = 'list') => {
  if (!validateForm()) {
    ElMessage.warning('请先完善活动表单信息')
    return
  }

  saving.value = true
  try {
    const payload = {
      activityId: form.activityId,
      activityName: form.activityName,
      discountId: form.discountId,
      groupType: GROUP_TYPE_STR_TO_INT[form.groupType] ?? 1,
      takeLimitCount: form.takeLimitCount,
      target: form.targetCount,
      validTime: form.validTime,
      status: STATUS_LABEL_TO_INT[form.status] ?? 0,
      startTime: form.startTime ? new Date(form.startTime).toISOString() : undefined,
      endTime: form.endTime ? new Date(form.endTime).toISOString() : undefined,
    }
    if (isEdit.value) {
      await activityService.update(payload)
      ElMessage.success('活动更新成功')
    } else {
      await activityService.create(payload)
      ElMessage.success('活动创建成功')
    }

    if (nextAction === 'binding') {
      router.push('/merchant/binding?from=activity')
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
  loadPageData()
})
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-800">
    <div class="mx-auto max-w-6xl px-6 py-8">
      <div class="mb-6 flex flex-wrap items-center justify-between gap-4">
        <div>
          <p class="mb-2 text-sm font-medium text-rose-500">Merchant / Activity / Edit</p>
          <h1 class="text-3xl font-bold tracking-tight text-slate-900">
            {{ isEdit ? '编辑拼团活动' : '新建拼团活动' }}
          </h1>
          <p class="mt-2 text-sm text-slate-500">{{ pageDescription }}</p>
        </div>

        <button
          class="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 shadow-sm transition hover:border-slate-300 hover:bg-slate-50"
          @click="router.push(resolveBackPath())"
        >
          返回活动列表
        </button>
      </div>

      <div class="mb-6 grid gap-4 md:grid-cols-3">
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">当前模式</p>
          <p class="mt-3 text-2xl font-bold text-slate-900">{{ isEdit ? '编辑模式' : '创建模式' }}</p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">已选折扣</p>
          <p class="mt-3 text-base font-semibold text-rose-500">
            {{ selectedDiscount ? selectedDiscount.discountName : '暂未选择' }}
          </p>
        </div>
        <div class="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-slate-100">
          <p class="text-sm text-slate-500">下一步建议</p>
          <p class="mt-3 text-base font-medium text-slate-700">保存后进入商品活动绑定</p>
        </div>
      </div>

      <div v-if="loading" class="rounded-3xl bg-white px-6 py-10 text-sm text-slate-500 shadow-sm ring-1 ring-slate-100">
        正在加载活动编辑页数据...
      </div>

      <div v-else class="space-y-6">
        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="mb-6">
            <h2 class="text-lg font-semibold text-slate-900">基础信息</h2>
            <p class="mt-1 text-sm text-slate-500">活动的基本标识和描述信息</p>
          </div>

          <div class="grid gap-5 md:grid-cols-2">
            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">活动编号 activityId</label>
              <input v-model="form.activityId" type="text" placeholder="请输入活动编号"
                class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
                :class="formErrors.activityId ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'" />
              <p v-if="formErrors.activityId" class="mt-2 text-xs text-red-500">{{ formErrors.activityId }}</p>
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">活动名称</label>
              <input v-model="form.activityName" type="text" placeholder="请输入活动名称"
                class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
                :class="formErrors.activityName ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'" />
              <p v-if="formErrors.activityName" class="mt-2 text-xs text-red-500">{{ formErrors.activityName }}</p>
            </div>

            <div class="md:col-span-2">
              <label class="mb-2 block text-sm font-medium text-slate-700">活动描述</label>
              <textarea v-model="form.activityDesc" rows="4" placeholder="请输入活动描述"
                class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-rose-300 focus:bg-white" />
            </div>
          </div>
        </section>

        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="mb-6">
            <h2 class="text-lg font-semibold text-slate-900">拼团规则</h2>
            <p class="mt-1 text-sm text-slate-500">配置成团人数、参与限制和活动有效期</p>
          </div>

          <div class="grid gap-5 md:grid-cols-2 xl:grid-cols-4">
            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">groupType</label>
              <select v-model="form.groupType"
                class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-rose-300 focus:bg-white">
                <option value="TEAM">TEAM</option>
                <option value="LADDER">LADDER</option>
              </select>
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">成团人数</label>
              <input v-model.number="form.targetCount" type="number" min="2"
                class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
                :class="formErrors.targetCount ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'" />
              <p v-if="formErrors.targetCount" class="mt-2 text-xs text-red-500">{{ formErrors.targetCount }}</p>
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">参与上限</label>
              <input v-model.number="form.takeLimitCount" type="number" min="1"
                class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
                :class="formErrors.takeLimitCount ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'" />
              <p v-if="formErrors.takeLimitCount" class="mt-2 text-xs text-red-500">{{ formErrors.takeLimitCount }}</p>
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">有效时长（小时）</label>
              <input v-model.number="form.validTime" type="number" min="1"
                class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
                :class="formErrors.validTime ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'" />
              <p v-if="formErrors.validTime" class="mt-2 text-xs text-red-500">{{ formErrors.validTime }}</p>
            </div>
          </div>
        </section>

        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="mb-6">
            <h2 class="text-lg font-semibold text-slate-900">时间设置</h2>
            <p class="mt-1 text-sm text-slate-500">配置活动的开始和结束时间</p>
          </div>

          <div class="grid gap-5 md:grid-cols-2">
            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">开始时间</label>
              <input v-model="form.startTime" type="datetime-local"
                class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
                :class="formErrors.startTime ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'" />
              <p v-if="formErrors.startTime" class="mt-2 text-xs text-red-500">{{ formErrors.startTime }}</p>
            </div>

            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">结束时间</label>
              <input v-model="form.endTime" type="datetime-local"
                class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
                :class="formErrors.endTime ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'" />
              <p v-if="formErrors.endTime" class="mt-2 text-xs text-red-500">{{ formErrors.endTime }}</p>
            </div>
          </div>
        </section>

        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="mb-6">
            <h2 class="text-lg font-semibold text-slate-900">折扣关联</h2>
            <p class="mt-1 text-sm text-slate-500">为活动选择一个折扣规则，作为拼团成功后的优惠配置</p>
          </div>

          <div>
            <label class="mb-2 block text-sm font-medium text-slate-700">关联折扣规则</label>
            <select v-model="form.discountId"
              class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition"
              :class="formErrors.discountId ? 'border-red-300 bg-red-50' : 'border-slate-200 bg-slate-50 focus:border-rose-300 focus:bg-white'">
              <option value="">请选择折扣规则</option>
              <option v-for="discount in discountOptions" :key="discount.discountId" :value="discount.discountId">
                {{ discount.discountName }}（{{ discount.discountId }}）
              </option>
            </select>
            <p v-if="formErrors.discountId" class="mt-2 text-xs text-red-500">{{ formErrors.discountId }}</p>
          </div>

          <div v-if="selectedDiscount" class="mt-4 rounded-2xl bg-slate-50 p-4 text-sm text-slate-600">
            <p class="font-medium text-slate-800">当前折扣说明</p>
            <p class="mt-2">{{ selectedDiscount.discountDesc }}</p>
            <p class="mt-2 text-xs text-slate-500">类型：{{ selectedDiscount.discountType }} ｜ 状态：{{ selectedDiscount.status }}</p>
          </div>
        </section>

        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="mb-6">
            <h2 class="text-lg font-semibold text-slate-900">状态设置</h2>
            <p class="mt-1 text-sm text-slate-500">控制活动在后台中的当前状态</p>
          </div>

          <div class="grid gap-5 md:grid-cols-2">
            <div>
              <label class="mb-2 block text-sm font-medium text-slate-700">活动状态</label>
              <select v-model="form.status"
                class="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-rose-300 focus:bg-white">
                <option value="草稿">草稿</option>
                <option value="已绑定">已绑定</option>
                <option value="生效中">生效中</option>
              </select>
            </div>
          </div>
        </section>

        <section class="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
          <div class="flex flex-wrap items-center justify-between gap-3">
            <div>
              <h2 class="text-lg font-semibold text-slate-900">提交动作</h2>
              <p class="mt-1 text-sm text-slate-500">保存后可返回活动列表，也可以继续进入商品活动绑定流程</p>
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
                @click="submitForm('binding')"
              >
                {{ saving ? '提交中...' : '保存并去商品绑定' }}
              </button>
              <button
                class="rounded-2xl bg-gradient-to-r from-rose-500 to-red-500 px-5 py-2.5 text-sm font-semibold text-white shadow-lg shadow-rose-200 transition hover:scale-[1.01] disabled:cursor-not-allowed disabled:opacity-60"
                :disabled="saving"
                @click="submitForm('list')"
              >
                {{ saving ? '提交中...' : isEdit ? '保存修改' : '创建活动' }}
              </button>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>
