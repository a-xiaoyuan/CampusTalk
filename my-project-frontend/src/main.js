// Vue项目主入口文件
import { createApp } from 'vue'           // 导入Vue创建应用函数
import App from './App.vue'               // 导入根组件
import router from  "@/router";           // 导入路由配置
import axios from 'axios'                 // 导入axios HTTP库

// 设置axios的默认请求基础URL
axios.defaults.baseURL='http://localhost:8080'

// 创建Vue应用实例
const app=createApp(App)

// 使用路由配置
app.use(router)

// 挂载应用到id为'app'的DOM元素上
app.mount('#app')