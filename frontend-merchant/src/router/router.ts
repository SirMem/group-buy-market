import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../pages/login/index.vue'
import MerchantDashboardPage from '../pages/merchant/dashboard/index.vue'
import MerchantGoodsPage from '../pages/merchant/goods/index.vue'
import MerchantGoodsEditPage from '../pages/merchant/goods/edit.vue'
import MerchantDiscountPage from '../pages/merchant/discount/index.vue'
import MerchantDiscountEditPage from '../pages/merchant/discount/edit.vue'
import MerchantActivityPage from '../pages/merchant/activity/index.vue'
import MerchantActivityEditPage from '../pages/merchant/activity/edit.vue'
import MerchantBindingPage from '../pages/merchant/binding/index.vue'
import { getStoredAuth } from './pinia'

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
    meta: { title: '登录', hideSidebar: true },
  },
  {
    path: '/merchant/dashboard',
    name: 'merchant-dashboard',
    component: MerchantDashboardPage,
    meta: { title: '商家后台首页', requiresAuth: true },
  },
  {
    path: '/merchant/goods',
    name: 'merchant-goods',
    component: MerchantGoodsPage,
    meta: { title: '商品管理', requiresAuth: true },
  },
  {
    path: '/merchant/goods/edit',
    name: 'merchant-goods-edit',
    component: MerchantGoodsEditPage,
    meta: { title: '新建/编辑商品', requiresAuth: true },
  },
  {
    path: '/merchant/discount',
    name: 'merchant-discount',
    component: MerchantDiscountPage,
    meta: { title: '折扣规则管理', requiresAuth: true },
  },
  {
    path: '/merchant/discount/edit',
    name: 'merchant-discount-edit',
    component: MerchantDiscountEditPage,
    meta: { title: '新建/编辑折扣规则', requiresAuth: true },
  },
  {
    path: '/merchant/activity',
    name: 'merchant-activity',
    component: MerchantActivityPage,
    meta: { title: '拼团活动管理', requiresAuth: true },
  },
  {
    path: '/merchant/activity/edit',
    name: 'merchant-activity-edit',
    component: MerchantActivityEditPage,
    meta: { title: '新建/编辑拼团活动', requiresAuth: true },
  },
  {
    path: '/merchant/binding',
    name: 'merchant-binding',
    component: MerchantBindingPage,
    meta: { title: '商品活动绑定', requiresAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const auth = getStoredAuth()
  document.title = `${to.meta?.title || 'group-buy-market'} - group-buy-market`

  if (to.path === '/login' && auth.token) {
    next({ path: '/merchant/dashboard', replace: true })
    return
  }

  if (to.meta?.requiresAuth && !auth.token) {
    next({ path: '/login', replace: true })
    return
  }

  next()
})

export default router
