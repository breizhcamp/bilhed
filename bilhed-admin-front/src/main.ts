import { getToken, vueKeycloak } from '@josempgon/vue-keycloak';
import { createApp } from 'vue'
import App from './App.vue'
import { initRouter } from './router'

import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import './assets/main.css'
import axios from 'axios'
import Vue3Toastify, { type ToastContainerOptions } from 'vue3-toastify';

const app = createApp(App)

axios.defaults.baseURL = '/admin'
axios.interceptors.request.use(async config => {
  const token = await getToken()
  config.headers.Authorization = `Bearer ${token}`
  return config
})

await vueKeycloak.install(app, {
  config: {
    url: 'https://auth.breizhcamp.org/auth/',
    realm: 'BreizhCamp',
    clientId: 'bilhed-admin-front',
  }
})

app.use(initRouter())
app.use(Vue3Toastify, {
  autoClose: 3000,
} as ToastContainerOptions)
app.mount('#app')