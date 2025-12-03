/**
 * Vue路由配置文件
 * 定义应用的路由结构、导航守卫和权限控制
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */

// 导入Vue Router相关函数
import {createRouter,createWebHistory} from "vue-router";

// 导入网络请求工具中的授权检查函数
import {unauthorized} from "@/net/index.js";

/**
 * 创建路由实例
 * 配置路由模式和路由表
 */
const router = createRouter({
  // 设置路由历史模式为HTML5 History模式（去除URL中的#号）
  history: createWebHistory(import.meta.env.BASE_URL),
  
  // 定义路由表，包含所有页面路由配置
  routes:[
      {
          path:'/',                    // 路由路径：根路径
          name:'welcome',              // 路由名称：欢迎页面
          component:()=>import('@/views/WelcomeView.vue'),  // 路由组件：欢迎页面容器
          children:[                   // 子路由配置：欢迎页面的嵌套路由
              {
                  path:'/',                                 // 子路由路径：默认子路由
                  name:'welcome-login',                     // 子路由名称：登录页面
                  component:()=>import('@/views/Welcome/LoginPage.vue')  // 子路由组件：登录页面
              },
              {
                  path:'register',                                 // 子路由路径：注册页面
                  name:'welcome-register',                     // 子路由名称：注册页面
                  component:()=>import('@/views/Welcome/RegisterPage.vue')  // 子路由组件：注册页面
              },
              {
                  path:'reset',                                 // 子路由路径：重置密码页面
                  name:'welcome-reset',                     // 子路由名称：重置密码页面
                  component:()=>import('@/views/Welcome/ResetPage.vue')  // 子路由组件：重置密码页面
              }
          ]
      },{
          path:'/index',              // 路由路径：首页
          name:'index',               // 路由名称：首页
          component:()=>import('@/views/IndexView.vue'),  // 路由组件：首页
          children:[
              {
                  path: 'user-setting',
                  name: 'user-setting',
                  component: () => import('@/views/Settings/UserSetting.vue'),
          },
              {
                  path: 'privacy-setting',
                  name: 'privacy-setting',
                  component: () => import('@/views/Settings/PrivacySetting.vue'),
              }
          ]
      }
  ]
});

/**
 * 全局前置路由守卫
 * 在每次路由跳转前执行，用于权限控制和导航拦截
 * 
 * @param {Object} to - 目标路由对象
 * @param {Object} from - 来源路由对象
 * @param {Function} next - 导航继续函数
 */
router.beforeEach((to,from,next)=>{
    // 检查当前用户是否未授权（未登录）
    const isUnauthorized=unauthorized()
    
    /**
     * 路由守卫逻辑说明：
     * 1. 未授权用户访问受保护页面（/index）→ 跳转到登录页面（/）
     * 2. 已授权用户访问登录页面（/）→ 跳转到首页（/index）
     * 3. 其他情况正常导航
     */
    
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