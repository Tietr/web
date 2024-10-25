import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import 'element-plus/dist/index.css'
import axios from "axios";

const app = createApp(App)
// 后端端口
axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.headers.post['Content-Type'] = 'charset=utf-8';
axios.defaults.headers.get['Content-Type'] = 'charset=utf-8';

app.use(createPinia())
app.use(router)

app.mount('#app')
