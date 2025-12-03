import { createApp } from 'vue';
import App from './App.vue';
import axios from 'axios';

var app = createApp(App);
app.mount('#app');

axios.defaults.withCredentials = true;
app.config.globalProperties.$axios = axios;