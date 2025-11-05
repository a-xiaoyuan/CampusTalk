// Vue路由配置文件
import {createRouter,createWebHistory} from "vue-router";  // 导入Vue Router相关函数

// 创建路由实例
const router = createRouter({
  // 设置路由历史模式为HTML5 History模式
  history: createWebHistory(import.meta.env.BASE_URL),
  // 定义路由表
  routes:[
      {
          path:'/',                    // 路由路径
          name:'welcome',              // 路由名称
          component:()=>import('@/views/WelcomeView.vue'),  // 路由组件
          children:[                   // 子路由配置
              {
                  path:'/',                                 // 子路由路径
                  name:'welcome-login',                     // 子路由名称
                  component:()=>import('@/views/Welcome/LoginPage.vue')  // 子路由组件
              }
          ]
      }
  ]
});

// 导出路由实例供主程序使用
export default router;