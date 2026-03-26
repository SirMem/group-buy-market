import { createRouter, createWebHistory } from 'vue-router'
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
    redirect: '/merchant/dashboard',
  },
  {
    path: '/merchant/dashboard',
    name: 'merchant-dashboard',
    component: MerchantDashboardPage,
    meta: { title: '商家后台首页' },
  },
  {
    path: '/merchant/goods',
    name: 'merchant-goods',
    component: MerchantGoodsPage,
    meta: { title: '商品管理' },
  },
  {
    path: '/merchant/goods/edit',
    name: 'merchant-goods-edit',
    component: MerchantGoodsEditPage,
    meta: { title: '新建/编辑商品' },
  },
  {
    path: '/merchant/discount',
    name: 'merchant-discount',
    component: MerchantDiscountPage,
    meta: { title: '折扣规则管理' },
  },
  {
    path: '/merchant/discount/edit',
    name: 'merchant-discount-edit',
    component: MerchantDiscountEditPage,
    meta: { title: '新建/编辑折扣规则' },
  },
  {
    path: '/merchant/activity',
    name: 'merchant-activity',
    component: MerchantActivityPage,
    meta: { title: '拼团活动管理' },
  },
  {
    path: '/merchant/activity/edit',
    name: 'merchant-activity-edit',
    component: MerchantActivityEditPage,
    meta: { title: '新建/编辑拼团活动' },
  },
  {
    path: '/merchant/binding',
    name: 'merchant-binding',
    component: MerchantBindingPage,
    meta: { title: '商品活动绑定' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const auth = getStoredAuth()
  document.title = `${to.meta?.title || 'group-buy-market'} - group-buy-market`

  if (to.meta?.requiresAuth && !auth.token) {
    next({ path: '/', replace: true })
    return
  }

  next()
})

export default router
