import AttendeeView from '@/views/AttendeeView.vue';
import ConfigurationView from "@/views/ConfigurationView.vue";
import ParticipantView from '@/views/ParticipantView.vue'
import RegisteredView from '@/views/RegisteredView.vue'
import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/registered' },
  {
    path: '/registered',
    name: 'registered',
    component: RegisteredView
  },{
    path: '/participants',
    name: 'persons',
    component: ParticipantView
  },{
    path: '/attendees',
    name: 'attendees',
    component: AttendeeView
  }, {
    path: '/configuration',
    name: 'configuration',
    component: ConfigurationView
  }
];


const initRouter = () => {
  const history = createWebHashHistory(import.meta.env.BASE_URL)
  return createRouter({ history, routes })
}

export { initRouter }
