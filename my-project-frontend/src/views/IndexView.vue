<!-- 
  主页面视图组件
  用户登录后的主界面，目前仅包含退出登录功能
  
  @author 系统
  @version 1.0
  @since 2024
-->

<script setup>
/**
 * 导入路由实例和网络请求模块
 */
import router from "@/router";
import {logout} from "@/net";

/**
 * 用户注销函数
 * 调用后端注销接口，成功后跳转到登录页面
 */
function userLogout(){
    // 调用网络请求模块的注销函数
    logout(()=>
        // 注销成功回调：跳转到根路径（登录页面）
        router.push('/'))
}
</script>

<template>
  <!-- 主页面容器 -->
  <div>
    <!-- 
      Element Plus按钮组件
      点击触发用户注销操作
    -->
    <el-button @click="userLogout">退出登录</el-button>
  </div>
</template>

<style scoped>
/**
 * 主页面样式区域
 * 当前页面样式为空，可根据需要添加样式
 */
</style>