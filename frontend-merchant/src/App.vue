<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import SideBar from './components/SideBar.vue'

const route = useRoute()

const hideSidebar = computed(() => route.meta?.hideSidebar)
</script>

<template>
  <div class="min-h-screen bg-slate-100 text-slate-800">
    <div class="flex min-h-screen">
      <SideBar v-if="!hideSidebar" />
      <main class="min-w-0 flex-1">
        <router-view />
      </main>
    </div>
  </div>
</template>
