import { mount } from '@vue/test-utils'
import { createMemoryHistory, createRouter } from 'vue-router'
import { describe, expect, it } from 'vitest'
import App from './App.vue'

describe('Sirmem visual foundation shell', () => {
  it('renders the reusable shell classes for routed pages', async () => {
    const router = createRouter({
      history: createMemoryHistory(),
      routes: [
        {
          path: '/',
          component: { template: '<div data-testid="route-view">page</div>' },
        },
      ],
    })

    router.push('/')
    await router.isReady()

    const wrapper = mount(App, {
      global: {
        plugins: [router],
        stubs: {
          BottomNav: { template: '<nav data-testid="bottom-nav">nav</nav>' },
        },
      },
    })

    expect(wrapper.classes()).toContain('sirmem-app-shell')
    expect(wrapper.find('main').classes()).toContain('sirmem-page-shell')
    expect(wrapper.find('[data-testid="route-view"]').exists()).toBe(true)
    expect(wrapper.find('[data-testid="bottom-nav"]').exists()).toBe(false)
  })
})
