/**
 * Vue应用主入口文件
 * 负责初始化Vue应用、配置全局依赖和挂载到DOM
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */

// 导入Vue创建应用函数
import { createApp } from 'vue'

// 导入Pinia状态管理
import { createPinia } from 'pinia'

// 导入根组件
import App from './App.vue'

// 导入路由配置
import router from  "@/router";

// 导入axios HTTP库
import axios from 'axios'

// 导入Element Plus暗黑主题样式
import 'element-plus/theme-chalk/dark/css-vars.css'

/**
 * 配置axios默认设置
 * 设置所有HTTP请求的基础URL，指向后端API服务器
 */
axios.defaults.baseURL='http://localhost:5371'  // 后端服务器地址

/**
 * 创建Vue应用实例
 * 使用根组件App.vue作为应用的起点
 */
const app=createApp(App)

// 创建Pinia实例
const pinia = createPinia()

/**
 * 注册插件
 * 启用Vue Router进行单页面应用的路由管理
 * 启用Pinia进行状态管理
 */
app.use(router)
app.use(pinia)

/**
 * 挂载应用到DOM
 * 将Vue应用挂载到HTML中id为'app'的元素上
 */
app.mount('#app')