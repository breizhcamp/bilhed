import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import 'bootstrap/dist/css/bootstrap.min.css'
import './assets/main.css'
import axios from 'axios'

axios.defaults.baseURL = '/api'

const app = createApp(App)
app.use(router)
app.mount('#app')