<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const menus = [
  { label: '首页', path: '/merchant/dashboard', activePrefix: '/merchant/dashboard' },
  { label: '商品管理', path: '/merchant/goods', activePrefix: '/merchant/goods' },
  { label: '折扣规则', path: '/merchant/discount', activePrefix: '/merchant/discount' },
  { label: '拼团活动', path: '/merchant/activity', activePrefix: '/merchant/activity' },
  { label: '商品绑定', path: '/merchant/binding', activePrefix: '/merchant/binding' },
]

const isActive = (path: string, activePrefix: string) => {
  if (route.path === path) return true
  return route.path.startsWith(activePrefix)
}
</script>

<template>
  <aside class="hidden w-64 shrink-0 border-r border-slate-200 bg-white lg:block">
    <div class="border-b border-slate-100 px-6 py-5">
      <div class="text-lg font-bold text-slate-900">group-buy-market</div>
      <div class="mt-1 text-sm text-slate-500">商家后台</div>
    </div>

    <nav class="space-y-2 p-4">
      <button
        v-for="item in menus"
        :key="item.path"
        class="flex w-full items-center rounded-2xl px-4 py-3 text-left text-sm font-medium transition"
        :class="isActive(item.path, item.activePrefix)
          ? 'bg-rose-50 text-rose-600'
          : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'"
        @click="router.push(item.path)"
      >
        {{ item.label }}
      </button>
    </nav>
  </aside>
</template>
