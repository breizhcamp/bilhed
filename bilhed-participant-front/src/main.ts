import dayjs from 'dayjs';
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.min.css'
import './assets/main.css'
import axios from 'axios'

import 'dayjs/locale/fr'

axios.defaults.baseURL = '/api'
dayjs.locale('fr')

const app = createApp(App)
app.use(router)
app.mount('#app')
