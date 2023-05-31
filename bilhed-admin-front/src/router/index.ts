import { createRouter, createWebHashHistory } from 'vue-router'
import ParticipantView from '@/views/ParticipantView.vue'
import RegisteredView from '@/views/RegisteredView.vue'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/registered' },
    {
      path: '/registered',
      name: 'registered',
      component: RegisteredView
    },{
      path: '/participants',
      name: 'persons',
      component: ParticipantView
    }
  ]
})

export default router
