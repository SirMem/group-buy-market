<script setup lang="ts">
import { computed, h } from 'vue'
import { useRoute, useRouter, type RouteLocationRaw } from 'vue-router'

const router = useRouter()
const route = useRoute()

const HomeIcon = () =>
  h(
    'svg',
    {
      viewBox: '0 0 24 24',
      fill: 'none',
      xmlns: 'http://www.w3.org/2000/svg',
      'aria-hidden': 'true',
    },
    [
      h('path', {
        d: 'M4.75 10.25L12 4.5L19.25 10.25V18C19.25 18.9665 18.4665 19.75 17.5 19.75H6.5C5.5335 19.75 4.75 18.9665 4.75 18V10.25Z',
        'stroke-width': '1.8',
        stroke: 'currentColor',
        'stroke-linejoin': 'round',
      }),
      h('path', {
        d: 'M9.25 19.75V13.5H14.75V19.75',
        'stroke-width': '1.8',
        stroke: 'currentColor',
        'stroke-linejoin': 'round',
        'stroke-linecap': 'round',
      }),
    ],
  )

const OrdersIcon = () =>
  h(
    'svg',
    {
      viewBox: '0 0 24 24',
      fill: 'none',
      xmlns: 'http://www.w3.org/2000/svg',
      'aria-hidden': 'true',
    },
    [
      h('rect', {
        x: '5',
        y: '4.75',
        width: '14',
        height: '14.5',
        rx: '3',
        'stroke-width': '1.8',
        stroke: 'currentColor',
      }),
      h('path', {
        d: 'M8.25 9H15.75',
        'stroke-width': '1.8',
        stroke: 'currentColor',
        'stroke-linecap': 'round',
      }),
      h('path', {
        d: 'M8.25 12.5H15.75',
        'stroke-width': '1.8',
        stroke: 'currentColor',
        'stroke-linecap': 'round',
      }),
      h('path', {
        d: 'M8.25 16H12.75',
        'stroke-width': '1.8',
        stroke: 'currentColor',
        'stroke-linecap': 'round',
      }),
    ],
  )

const ProfileIcon = () =>
  h(
    'svg',
    {
      viewBox: '0 0 24 24',
      fill: 'none',
      xmlns: 'http://www.w3.org/2000/svg',
      'aria-hidden': 'true',
    },
    [
      h('circle', {
        cx: '12',
        cy: '8.25',
        r: '3.25',
        'stroke-width': '1.8',
        stroke: 'currentColor',
      }),
      h('path', {
        d: 'M5.5 18.5C6.8 15.9 9.05 14.5 12 14.5C14.95 14.5 17.2 15.9 18.5 18.5',
        'stroke-width': '1.8',
        stroke: 'currentColor',
        'stroke-linecap': 'round',
      }),
    ],
  )

const tabs: Array<{ label: string; path: string; icon: () => ReturnType<typeof h> }> = [
  { label: '首页', path: '/', icon: HomeIcon },
  { label: '订单', path: '/orders', icon: OrdersIcon },
  { label: '我的', path: '/profile', icon: ProfileIcon },
]

const activePath = computed(() => route.path)

function isActive(path: string) {
  return activePath.value === path
}

function go(path: RouteLocationRaw) {
  if (route.path === path) return
  router.push(path)
}
</script>

<template>
  <nav class="sirmem-bottom-nav" aria-label="主导航">
    <div class="sirmem-bottom-nav__rail">
      <button
        v-for="tab in tabs"
        :key="tab.path"
        type="button"
        class="sirmem-bottom-nav__item"
        :class="{ 'is-active': isActive(tab.path) }"
        :aria-current="isActive(tab.path) ? 'page' : undefined"
        @click="go(tab.path)"
      >
        <span class="sirmem-bottom-nav__icon" aria-hidden="true">
          <component :is="tab.icon" />
        </span>
        <span class="sirmem-bottom-nav__label">{{ tab.label }}</span>
      </button>
    </div>
  </nav>
</template>
