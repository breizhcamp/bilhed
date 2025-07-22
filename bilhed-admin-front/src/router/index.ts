import AttendeeView from '@/views/AttendeeView.vue';
import ConfigurationView from "@/views/ConfigurationView.vue";
import ParticipantView from '@/views/ParticipantView.vue'
import RegisteredView from '@/views/RegisteredView.vue'
import {createRouter, createWebHashHistory} from 'vue-router'
import PersonView from "@/views/PersonView.vue";
import GroupView from "@/views/GroupView.vue";

const routes = [
  { path: '/', redirect: '/registered' },
  {
    path: '/registered',
    name: 'registered',
    component: RegisteredView
  },{
    path: '/participants',
    name: 'participants',
    component: ParticipantView
  },{
    path: '/attendees',
    name: 'attendees',
    component: AttendeeView
  }, {
    path: '/configuration',
    name: 'configuration',
    component: ConfigurationView
  }, {
    path: '/person/:id',
    name: 'person',
    component: PersonView
  }, {
    path: '/group/:id',
    name: 'group',
    component: GroupView
  }
];


const initRouter = () => {
  const history = createWebHashHistory(import.meta.env.BASE_URL)
  return createRouter({ history, routes })
}

export { initRouter }
