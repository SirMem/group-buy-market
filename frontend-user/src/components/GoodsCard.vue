<script setup lang="ts">
import { computed } from 'vue'
import type { MarketGoodsCardDTO } from '../services/goods'

const props = defineProps<{
  goods: MarketGoodsCardDTO
}>()

const teamCountText = computed(() => props.goods.teamStatistic?.allTeamCount ?? 0)
const teamUserText = computed(() => props.goods.teamStatistic?.allTeamUserCount ?? 0)
const availableStockText = computed(() => props.goods.inventory?.availableStock ?? 0)
</script>

<template>
  <div class="sirmem-surface-card overflow-hidden p-4 transition hover:-translate-y-0.5 hover:shadow-md">
    <div class="sirmem-placeholder-media mb-4" :data-has-image="false">
      <div class="sirmem-placeholder-media__content">
        <span class="sirmem-placeholder-media__badge">SM</span>
      </div>
    </div>

    <div class="flex items-start justify-between gap-3">
      <div class="min-w-0 flex-1">
        <h3 class="truncate text-base font-semibold text-slate-900">{{ goods.goodsName }}</h3>
        <p class="mt-1 text-xs text-slate-400">goodsId：{{ goods.goodsId }}</p>
      </div>
      <span class="sirmem-tag-soft">拼团</span>
    </div>

    <div class="mt-4 flex items-end gap-2">
      <span class="text-2xl font-bold text-rose-500">¥{{ goods.payPrice }}</span>
      <span class="text-sm text-slate-400 line-through">¥{{ goods.originalPrice }}</span>
      <span class="text-sm font-medium text-emerald-600">省 ¥{{ goods.deductionPrice }}</span>
    </div>

    <div class="sirmem-surface-muted mt-4 grid grid-cols-3 gap-3 p-3 text-center text-sm text-slate-500">
      <div>
        <p class="text-base font-semibold text-slate-900">{{ teamCountText }}</p>
        <p class="mt-1">开团数</p>
      </div>
      <div>
        <p class="text-base font-semibold text-slate-900">{{ teamUserText }}</p>
        <p class="mt-1">参团人数</p>
      </div>
      <div>
        <p class="text-base font-semibold text-slate-900">{{ availableStockText }}</p>
        <p class="mt-1">可用库存</p>
      </div>
    </div>
  </div>
</template>
