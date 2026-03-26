<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '../../store/auth'

const router = useRouter()
const authStore = useAuthStore()

const loginTypeLabel = computed(() => (authStore.loginType === 'wechat' ? '微信登录' : '账号登录'))

async function logout() {
  try {
    await ElMessageBox.confirm('确认退出当前登录状态吗？', '退出登录', {
      confirmButtonText: '退出登录',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }

  authStore.clear()
  await router.replace('/login')
}
</script>

<template>
  <div class="min-h-screen px-4 py-5">
    <section class="sirmem-surface-card p-5">
      <p class="sirmem-kicker text-sm">个人中心</p>
      <h1 class="sirmem-section-title mt-1 text-2xl">我的信息</h1>
      <p class="mt-2 text-sm text-slate-500">查看当前登录信息，并支持退出登录</p>
    </section>

    <section class="sirmem-surface-card mt-5 p-5">
      <div class="space-y-4">
        <div class="sirmem-surface-muted p-4">
          <p class="text-xs uppercase tracking-wide text-slate-400">userId</p>
          <p class="mt-2 break-all text-base font-semibold text-slate-900">{{ authStore.userId || '-' }}</p>
        </div>

        <div class="sirmem-surface-muted p-4">
          <p class="text-xs uppercase tracking-wide text-slate-400">username</p>
          <p class="mt-2 break-all text-base font-semibold text-slate-900">{{ authStore.username || '-' }}</p>
        </div>

        <div class="sirmem-surface-muted p-4">
          <p class="text-xs uppercase tracking-wide text-slate-400">login type</p>
          <p class="mt-2 text-base font-semibold text-slate-900">{{ loginTypeLabel }}</p>
        </div>
      </div>

      <el-button class="!mt-6 w-full" type="danger" size="large" @click="logout">退出登录</el-button>
    </section>
  </div>
</template>
