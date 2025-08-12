import { getToken, vueKeycloak } from '@josempgon/vue-keycloak';
import { createApp } from 'vue'
import App from './App.vue'
import { initRouter } from './router'
import dayjs from "dayjs";

import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import './assets/main.css'
import axios from 'axios'
import Vue3Toastify, { type ToastContainerOptions } from 'vue3-toastify';

import 'dayjs/locale/fr'

const app = createApp(App)
dayjs.locale('fr')

axios.defaults.baseURL = '/admin'
axios.interceptors.request.use(async config => {
  const token = await getToken()
  config.headers.Authorization = `Bearer ${token}`
  return config
})

await vueKeycloak.install(app, {
  config: {
    url: 'https://lemur-15.cloud-iam.com/auth/',
    realm: 'breizhcamp',
    clientId: 'bilhed',
  }
})

app.use(initRouter())
app.use(Vue3Toastify, {
  autoClose: 3000,
} as ToastContainerOptions)
app.mount('#app')