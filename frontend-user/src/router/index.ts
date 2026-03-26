import { createRouter, createWebHistory } from 'vue-router'
import { getStoredAuth } from '../store/auth'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('../pages/home/index.vue'),
    meta: { title: '首页', tabbar: true, requiresAuth: true },
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../pages/login/index.vue'),
    meta: { title: '登录', tabbar: false },
  },
  {
    path: '/goods/:goodsId',
    name: 'goods-detail',
    component: () => import('../pages/goods/detail.vue'),
    meta: { title: '商品详情', tabbar: false, requiresAuth: true },
  },
  {
    path: '/orders',
    name: 'orders',
    component: () => import('../pages/orders/index.vue'),
    meta: { title: '我的订单', tabbar: true, requiresAuth: true },
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('../pages/profile/index.vue'),
    meta: { title: '我的', tabbar: true, requiresAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta?.title || '拼团商城'} - 拼团商城`

  const auth = getStoredAuth()

  if (to.meta?.requiresAuth && !auth?.token) {
    next({ path: '/login', replace: true })
    return
  }

  if (to.path === '/login' && auth?.token) {
    next({ path: '/', replace: true })
    return
  }

  next()
})

export default router
