// Vue路由配置文件
import {createRouter,createWebHistory} from "vue-router";
import {unauthorized} from "@/net/index.js";  // 导入Vue Router相关函数

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
              },
              {
                  path:'register',                                 // 子路由路径
                  name:'welcome-register',                     // 子路由名称
                  component:()=>import('@/views/Welcome/RegisterPage.vue')  // 子路由组件
              }
          ]
      },{
          path:'/index',
          name:'index',
          component:()=>import('@/views/IndexView.vue')
      }
  ]
});

// 修复路由守卫逻辑
router.beforeEach((to,from,next)=>{
    const isUnauthorized=unauthorized()
    
    // 修复后的正确逻辑：
    // 1. 未授权用户访问受保护页面（/index）→ 跳转到登录页面（/）
    // 2. 已授权用户访问登录页面（/）→ 跳转到首页（/index）
    // 3. 其他情况正常导航
    
    if(to.fullPath.startsWith('/index') && isUnauthorized){
        // 未授权用户访问受保护页面，跳转到登录页面
        next('/')
    }else if(to.fullPath === '/' && !isUnauthorized){
        // 已授权用户访问登录页面，跳转到首页
        next('/index')
    }else {
        // 其他情况正常导航
        next()
    }
})

// 导出路由实例供主程序使用
export default router;