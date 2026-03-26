<script setup lang="ts">
import { computed } from 'vue'
import type { TeamDTO } from '../services/goods'

const props = defineProps<{
  team: TeamDTO
}>()

const percentage = computed(() => {
  if (!props.team.targetCount) return 0
  return Math.min(100, Math.round(((props.team.completeCount || 0) / props.team.targetCount) * 100))
})
</script>

<template>
  <div class="rounded-2xl bg-slate-50 p-4 ring-1 ring-slate-100">
    <div class="flex items-center justify-between gap-4">
      <div>
        <p class="text-sm font-semibold text-slate-900">{{ team.userId }} 的团</p>
        <p class="mt-1 text-xs text-slate-400">teamId：{{ team.teamId }}</p>
      </div>
      <div class="text-right">
        <p class="text-sm font-semibold text-amber-500">{{ team.validTimeCountdown || '已结束' }}</p>
        <p class="mt-1 text-xs text-slate-400">锁单 {{ team.lockCount || 0 }} 人</p>
      </div>
    </div>

    <el-progress class="mt-4" :percentage="percentage" :stroke-width="10" :show-text="false" color="#f43f5e" />

    <div class="mt-3 flex items-center justify-between text-sm">
      <span class="text-slate-500">进度 {{ team.completeCount || 0 }}/{{ team.targetCount || 0 }}</span>
      <span class="font-medium text-slate-900">还差 {{ Math.max((team.targetCount || 0) - (team.completeCount || 0), 0) }} 人</span>
    </div>
  </div>
</template>
