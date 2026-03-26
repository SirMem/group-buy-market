<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import BottomNav from './components/BottomNav.vue'

const route = useRoute()
const showTabBar = computed(() => route.meta.tabbar === true)
</script>

<template>
  <div class="sirmem-app-shell min-h-screen text-[var(--text-main)]">
    <main class="sirmem-page-shell">
      <router-view />
    </main>
    <BottomNav v-if="showTabBar" />
    <div v-if="showTabBar" class="h-[var(--bottom-nav-height)]" />
  </div>
</template>
