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
  <div class="main-content">
    <el-container style="height: 100%">
      <el-header class="main-content-header">
        <el-image class="logo" src="https://element-plus.org/images/element-plus-logo.svg"></el-image>
        <div style="flex:1" class="user-info" >
          <div class="profile">
            <div>用户名</div>
            <div>电子邮箱</div>
          </div>
          <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
        </div>
      </el-header>
      <el-container>
          <el-aside>Aside</el-aside>
          <el-main>Main</el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style lang="less" scoped>
/**
 * 主页面样式区域
 * 当前页面样式为空，可根据需要添加样式
 */
.main-content{
    width: 100vw;
    height: 100vh;
}
.main-content-header{
    border-bottom: solid 1px var(--el-border-color);
    height: 55px;
    display: flex;
    align-items: center;
    box-sizing: border-box;

    .logo{
      height: 32px;
    }
    .user-info{
      display: flex;
      align-items: center;
      justify-content: flex-end;
    }
    .profile {
      text-align: right;
      margin-right: 20px;
    }
    :first-child{
      font-size: 18px;
      font-weight: bold;
      line-height: 20px;
    }
    :last-child{
      font-size: 10px;
      color: gray;
    }

}
</style>