import { getToken, vueKeycloak } from '@josempgon/vue-keycloak';
import { createApp } from 'vue'
import App from './App.vue'
import { initRouter } from './router'

import 'bootstrap/dist/css/bootstrap.min.css'
import './assets/main.css'
import axios from 'axios'

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
app.mount('#app')