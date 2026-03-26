<script setup lang="ts">
import { computed } from 'vue'
import { ORDER_STATUS_MAP } from '../config/constants'
import type { OrderInfoDTO } from '../services/order'

const props = defineProps<{
  order: OrderInfoDTO
  loading?: boolean
}>()

const emit = defineEmits<{
  repay: [order: OrderInfoDTO]
  confirmPay: [order: OrderInfoDTO]
  refund: [order: OrderInfoDTO]
}>()

const statusInfo = computed(() => ORDER_STATUS_MAP[props.order.status] || { label: props.order.status, type: 'info' as const })
const canRepay = computed(() => props.order.status === 'PAY_WAIT' && !!props.order.payUrl)
const canConfirmPay = computed(() => props.order.status === 'PAY_WAIT')
const canRefund = computed(() => ['CREATE', 'PAY_WAIT', 'PAY_SUCCESS', 'WAIT_REFUND'].includes(props.order.status))
</script>

<template>
  <div class="sirmem-surface-card p-4">
    <div class="flex items-start justify-between gap-3">
      <div class="min-w-0 flex-1">
        <h3 class="truncate text-base font-semibold text-slate-900">{{ order.productName }}</h3>
        <p class="mt-1 text-xs text-slate-400">订单号：{{ order.orderId }}</p>
      </div>
      <el-tag :type="statusInfo.type" round>{{ statusInfo.label }}</el-tag>
    </div>

    <div class="sirmem-surface-muted mt-4 grid grid-cols-2 gap-3 p-3 text-sm text-slate-500">
      <div>
        <p>订单金额</p>
        <p class="mt-1 font-medium text-slate-900">¥{{ order.totalAmount }}</p>
      </div>
      <div>
        <p>实际支付</p>
        <p class="mt-1 font-medium text-slate-900">¥{{ order.payAmount }}</p>
      </div>
      <div>
        <p>营销优惠</p>
        <p class="mt-1 font-medium text-emerald-600">¥{{ order.marketDeductionAmount || 0 }}</p>
      </div>
      <div>
        <p>下单时间</p>
        <p class="mt-1 font-medium text-slate-900">{{ String(order.orderTime || '').replace('T', ' ').slice(0, 16) }}</p>
      </div>
    </div>

    <div class="mt-4 flex flex-wrap gap-2">
      <el-button v-if="canRepay" size="small" :loading="loading" @click="emit('repay', order)">去支付</el-button>
      <el-button v-if="canConfirmPay" size="small" type="primary" plain :loading="loading" @click="emit('confirmPay', order)">
        已支付？点击确认
      </el-button>
      <el-button v-if="canRefund" size="small" type="danger" plain :loading="loading" @click="emit('refund', order)">退款</el-button>
    </div>
  </div>
</template>
