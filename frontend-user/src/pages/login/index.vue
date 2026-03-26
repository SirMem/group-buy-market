<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { checkLogin, getWeixinTicket, login, register } from '../../services/auth'
import { useAuthStore } from '../../store/auth'

type LoginTab = 'wechat' | 'account'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref<LoginTab>('wechat')
const loading = ref(false)
const isRegisterMode = ref(false)

const wechatLoading = ref(false)
const wechatPolling = ref(false)
const ticket = ref('')
const pollCount = ref(0)
const maxPollCount = 60
let pollTimer: number | null = null

const form = reactive({
  username: '',
  password: '',
})

const qrcodeUrl = computed(() => {
  if (!ticket.value) return ''
  return `https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=${encodeURIComponent(ticket.value)}`
})

function stopPolling() {
  wechatPolling.value = false
  if (pollTimer !== null) {
    window.clearTimeout(pollTimer)
    pollTimer = null
  }
}

async function pollWechatLogin() {
  if (!ticket.value) return

  try {
    const response = await checkLogin(ticket.value)

    if ((response.code === '0000' || response.code === 200) && response.data) {
      authStore.setWechatAuth(response.data)
      stopPolling()
      ElMessage.success('微信登录成功')
      await router.replace('/')
      return
    }

    pollCount.value += 1
    if (pollCount.value >= maxPollCount) {
      stopPolling()
      ElMessage.warning('登录超时，请刷新重试')
      return
    }

    pollTimer = window.setTimeout(() => {
      void pollWechatLogin()
    }, 2000)
  } catch (error: any) {
    stopPolling()
    const message = error?.response?.data?.info || error?.message || '微信登录检测失败'
    ElMessage.error(message)
  }
}

async function loadWechatQrcode() {
  stopPolling()
  wechatLoading.value = true
  pollCount.value = 0
  ticket.value = ''

  try {
    const response = await getWeixinTicket()
    if (!((response.code === '0000' || response.code === 200) && response.data)) {
      ElMessage.error(response.info || '获取微信二维码失败')
      return
    }

    ticket.value = response.data
    wechatPolling.value = true
    pollTimer = window.setTimeout(() => {
      void pollWechatLogin()
    }, 2000)
  } catch (error: any) {
    const message = error?.response?.data?.info || error?.message || '获取微信二维码失败'
    ElMessage.error(message)
  } finally {
    wechatLoading.value = false
  }
}

async function submitAccount() {
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
    const response = isRegisterMode.value
      ? await register({ username: form.username.trim(), password: form.password })
      : await login({ username: form.username.trim(), password: form.password })

    if (!(response.code === 200 && response.data?.token)) {
      ElMessage.error(response.info || (isRegisterMode.value ? '注册失败' : '登录失败'))
      return
    }

    authStore.setAccountAuth({
      token: response.data.token,
      username: response.data.username || form.username.trim(),
    })

    ElMessage.success(isRegisterMode.value ? '注册成功，已自动登录' : '登录成功')
    await router.replace('/')
  } catch (error: any) {
    const message = error?.response?.data?.info || error?.message || (isRegisterMode.value ? '注册失败' : '登录失败')
    ElMessage.error(message)
  } finally {
    loading.value = false
  }
}

watch(activeTab, (tab) => {
  if (tab === 'wechat' && !ticket.value && !wechatLoading.value) {
    void loadWechatQrcode()
    return
  }

  if (tab !== 'wechat') {
    stopPolling()
  } else if (ticket.value && !wechatPolling.value) {
    wechatPolling.value = true
    pollTimer = window.setTimeout(() => {
      void pollWechatLogin()
    }, 2000)
  }
})

onMounted(() => {
  if (activeTab.value === 'wechat') {
    void loadWechatQrcode()
  }
})

onBeforeUnmount(() => {
  stopPolling()
})
</script>

<template>
  <div class="flex min-h-screen items-center justify-center bg-slate-100 px-4 py-10">
    <div class="w-full max-w-md rounded-3xl bg-white p-6 shadow-xl ring-1 ring-slate-200">
      <div class="mb-8 text-center">
        <p class="text-sm font-medium text-rose-500">frontend-user</p>
        <h1 class="mt-2 text-3xl font-bold text-slate-900">拼团商城</h1>
        <p class="mt-2 text-sm text-slate-500">支持微信扫码登录与账号登录</p>
      </div>

      <div class="mb-6 flex rounded-2xl bg-slate-100 p-1">
        <button
          type="button"
          class="flex-1 rounded-xl px-4 py-2 text-sm font-medium transition"
          :class="activeTab === 'wechat' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'"
          @click="activeTab = 'wechat'"
        >
          微信扫码
        </button>
        <button
          type="button"
          class="flex-1 rounded-xl px-4 py-2 text-sm font-medium transition"
          :class="activeTab === 'account' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'"
          @click="activeTab = 'account'"
        >
          账号登录
        </button>
      </div>

      <div v-if="activeTab === 'wechat'" class="space-y-4">
        <div class="flex min-h-[280px] flex-col items-center justify-center rounded-3xl border border-dashed border-slate-200 bg-slate-50 p-6 text-center">
          <el-skeleton v-if="wechatLoading" animated class="w-full">
            <template #template>
              <div class="mx-auto h-52 w-52 rounded-2xl bg-slate-200" />
            </template>
          </el-skeleton>

          <template v-else-if="qrcodeUrl">
            <img :src="qrcodeUrl" alt="微信登录二维码" class="h-56 w-56 rounded-2xl border border-slate-200 bg-white p-3" />
            <p class="mt-4 text-sm text-slate-600">请使用微信扫码关注测试平台后完成登录</p>
            <p class="mt-2 text-xs text-slate-400">
              {{ wechatPolling ? `正在检测登录状态（${pollCount}/${maxPollCount}）` : '已暂停检测' }}
            </p>
          </template>

          <template v-else>
            <p class="text-sm text-slate-500">二维码加载失败，请刷新重试</p>
          </template>
        </div>

        <el-button class="w-full" plain size="large" :loading="wechatLoading" @click="loadWechatQrcode">
          刷新二维码
        </el-button>
      </div>

      <div v-else>
        <el-form label-position="top" @submit.prevent="submitAccount">
          <el-form-item label="用户名">
            <el-input v-model="form.username" placeholder="请输入用户名" size="large" clearable />
          </el-form-item>

          <el-form-item label="密码">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="请输入密码"
              size="large"
              clearable
              @keyup.enter="submitAccount"
            />
          </el-form-item>

          <el-button class="!mt-2 w-full" type="primary" size="large" :loading="loading" @click="submitAccount">
            {{ isRegisterMode ? '注册并登录' : '立即登录' }}
          </el-button>
        </el-form>

        <div class="mt-4 text-center text-sm text-slate-500">
          <span>{{ isRegisterMode ? '已经有账号了？' : '还没有账号？' }}</span>
          <button
            type="button"
            class="ml-1 font-semibold text-rose-500 hover:text-rose-600"
            @click="isRegisterMode = !isRegisterMode"
          >
            {{ isRegisterMode ? '去登录' : '去注册' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
