import ConfirmedView from '@/views/ConfirmedView.vue';
import SmsView from '@/views/SmsView.vue';
import TicketEndView from '@/views/TicketEndView.vue';
import { createRouter, createWebHashHistory } from 'vue-router'
import RegisterView from '../views/RegisterView.vue'
import SuccessView from '@/views/SuccessView.vue'
import ReleasedView from '@/views/ReleasedView.vue'
import ClosedView from '@/views/ClosedView.vue'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
    //   path: '/',
    //   name: 'register',
    //   component: RegisterView
    // }, {
      path: '/',
      name: 'closed',
      component: ClosedView
    }, {
      path: '/:id',
      name: 'sms',
      component: SmsView
    },{
      path: '/:id/success',
      name: 'success',
      component: SuccessView
    }, {
      path: '/confirmed',
      name: 'confirmed',
      component: ConfirmedView
    }, {
      path: '/released',
      name: 'released',
      component: ReleasedView
    }, {
      path: '/ticket',
      name: 'ticket',
      component: TicketEndView
    }, {
      path: '/data-usage',
      name: 'data-usage',
      component: () => import('../views/DataUsage.vue')
    }
  ]
})

export default router
