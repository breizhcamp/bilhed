import ConfirmedView from '@/views/ConfirmedView.vue';
import SmsView from '@/views/SmsView.vue';
import { createRouter, createWebHistory } from 'vue-router'
import RegisterView from '../views/RegisterView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'register',
      component: RegisterView
    },
    {
      path: '/:id',
      name: 'sms',
      component: SmsView
    },
    {
      path: '/confirmed',
      name: 'confirmed',
      component: ConfirmedView
    },
    {
      path: '/data-usage',
      name: 'data-usage',
      component: () => import('../views/DataUsage.vue')
    }
  ]
})

export default router
