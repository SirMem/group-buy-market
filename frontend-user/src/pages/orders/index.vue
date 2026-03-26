<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import OrderCard from '../../components/OrderCard.vue'
import { activePayNotify, queryUserOrderList, refundOrder, type OrderInfoDTO } from '../../services/order'
import { useAuthStore } from '../../store/auth'

const authStore = useAuthStore()

const loading = ref(false)
const loadingMore = ref(false)
const actionLoadingOrderId = ref('')
const hasMore = ref(false)
const lastId = ref<number | null>(null)
const orders = ref<OrderInfoDTO[]>([])

async function loadOrders(reset = false) {
  if (!authStore.userId) return
  if (reset) {
    loading.value = true
    lastId.value = null
    orders.value = []
  } else {
    loadingMore.value = true
  }

  try {
    const response = await queryUserOrderList({
      userId: authStore.userId,
      lastId: reset ? null : lastId.value,
      pageSize: 10,
    })

    if (!(response.code === '0000' || response.code === 200) || !response.data) {
      ElMessage.error(response.info || '订单加载失败')
      return
    }

    const data = response.data
    orders.value = reset ? (data.orderList || []) : [...orders.value, ...(data.orderList || [])]
    hasMore.value = Boolean(data.hasMore)
    lastId.value = data.lastId ?? null
  } catch (error: any) {
    const message = error?.response?.data?.info || error?.message || '订单加载失败'
    ElMessage.error(message)
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

function repayOrder(order: OrderInfoDTO) {
  if (!order.payUrl) {
    ElMessage.warning('当前订单没有可用支付链接')
    return
  }
  window.open(order.payUrl, '_blank', 'noopener,noreferrer')
}

async function confirmPaid(order: OrderInfoDTO) {
  actionLoadingOrderId.value = order.orderId
  try {
    const response = await activePayNotify(order.orderId)
    if (!(response.code === '0000' || response.code === 200)) {
      ElMessage.error(response.info || '支付状态确认失败')
      return
    }

    ElMessage.success(response.data || '支付状态已更新')
    await loadOrders(true)
  } catch (error: any) {
    const message = error?.response?.data?.info || error?.message || '支付状态确认失败'
    ElMessage.error(message)
  } finally {
    actionLoadingOrderId.value = ''
  }
}

async function refundCurrentOrder(order: OrderInfoDTO) {
  try {
    await ElMessageBox.confirm(`确认要退款订单 ${order.orderId} 吗？`, '退款确认', {
      confirmButtonText: '确认退款',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }

  actionLoadingOrderId.value = order.orderId
  try {
    const response = await refundOrder({
      userId: authStore.userId,
      orderId: order.orderId,
    })

    if (!(response.code === '0000' || response.code === 200) || !response.data) {
      ElMessage.error(response.info || '退款失败')
      return
    }

    if (!response.data.success) {
      ElMessage.error(response.data.message || '退款失败')
      return
    }

    ElMessage.success(response.data.message || '退款成功')
    await loadOrders(true)
  } catch (error: any) {
    const message = error?.response?.data?.data?.message || error?.response?.data?.info || error?.message || '退款失败'
    ElMessage.error(message)
  } finally {
    actionLoadingOrderId.value = ''
  }
}

onMounted(() => {
  void loadOrders(true)
})
</script>

<template>
  <div class="min-h-screen px-4 py-5">
    <section class="sirmem-surface-card p-5">
      <div class="flex items-center justify-between gap-3">
        <div>
          <p class="sirmem-kicker text-sm">订单中心</p>
          <h1 class="sirmem-section-title mt-1 text-2xl">我的订单</h1>
          <p class="mt-2 text-sm text-slate-500">支持查询订单、确认支付状态、退款</p>
        </div>
        <el-button plain @click="loadOrders(true)">刷新</el-button>
      </div>
    </section>

    <section class="mt-5 space-y-4">
      <div v-if="loading" class="sirmem-surface-card p-10 shadow-sm">
        <el-skeleton animated :rows="6" />
      </div>

      <template v-else>
        <OrderCard
          v-for="order in orders"
          :key="order.orderId"
          :order="order"
          :loading="actionLoadingOrderId === order.orderId"
          @repay="repayOrder"
          @confirm-pay="confirmPaid"
          @refund="refundCurrentOrder"
        />

        <div v-if="orders.length === 0" class="sirmem-empty-shell sirmem-empty-state">
          <span class="sirmem-empty-state__icon">◎</span>
          <div>
            <p class="sirmem-empty-state__title">暂无订单</p>
            <p class="sirmem-empty-state__description">当前还没有可展示的订单记录，后续订单会统一沿用 Sirmem 风格展示。</p>
          </div>
        </div>

        <div v-else class="pb-2 text-center">
          <el-button v-if="hasMore" :loading="loadingMore" plain size="large" @click="loadOrders(false)">
            加载更多
          </el-button>
          <p v-else class="text-sm text-slate-400">已加载全部订单</p>
        </div>
      </template>
    </section>
  </div>
</template>
