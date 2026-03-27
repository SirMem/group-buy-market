<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { login } from '../../services/auth'
import { useAuthStore } from '../../router/pinia'

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({
  username: '',
  password: '',
})

const loading = ref(false)

const handleLogin = async () => {
  if (!form.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }

  if (!form.password.trim()) {
    ElMessage.warning('请输入密码')
    return
  }

  loading.value = true
  try {
    const response = await login({
      username: form.username.trim(),
      password: form.password,
    })

    const auth = response?.data || {}
    const token = auth.token || auth.jwtToken || ''

    if (!token) {
      throw new Error('登录成功，但未收到有效 token')
    }

    authStore.setAuth({
      token,
      user: {
        id: auth.id,
        username: auth.username,
        name: auth.username,
        role: auth.role,
        userStatus: auth.userStatus,
      },
    })

    ElMessage.success('登录成功')
    router.replace('/merchant/dashboard')
  } catch (error: any) {
    ElMessage.error(error?.message || '登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="flex min-h-screen items-center justify-center bg-gradient-to-br from-slate-100 via-white to-rose-50 px-4">
    <div class="w-full max-w-md rounded-3xl border border-white/70 bg-white/90 p-8 shadow-[0_24px_80px_-24px_rgba(15,23,42,0.35)] backdrop-blur">
      <div class="mb-8 text-center">
        <div class="text-2xl font-bold text-slate-900">group-buy-market</div>
        <div class="mt-2 text-sm text-slate-500">商家后台登录</div>
      </div>

      <el-form label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" clearable />
        </el-form-item>

        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-button class="mt-2 w-full" type="primary" size="large" :loading="loading" @click="handleLogin">
          登录
        </el-button>
      </el-form>
    </div>
  </div>
</template>
