import { createRouter, createWebHistory } from 'vue-router'
import ParticipantView from '@/views/ParticipantView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'persons',
      component: ParticipantView
    }
  ]
})

export default router
