import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { Vuecloak } from 'vuecloak'

import 'bootstrap/dist/css/bootstrap.min.css'
import './assets/main.css'
import axios from 'axios'

const app = createApp(App)

axios.defaults.baseURL = '/admin'
axios.interceptors.request.use(async config => {
  await app.config.globalProperties.$keycloak.updateToken()
  config.headers.Authorization = `Bearer ${app.config.globalProperties.$keycloak.token}`
  return config
})

app.use(Vuecloak, {
  init: {
    onLoad: 'login-required',
    checkLoginIframe: false
  },
  config: {
    url: 'https://auth.breizhcamp.org/auth/',
    realm: 'BreizhCamp',
    clientId: 'bilhed-admin-front',
  }
})
app.use(router)
app.mount('#app')