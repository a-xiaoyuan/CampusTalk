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
import {get, logout} from "@/net";
import {useStore} from "@/store/index.js";
import {ref} from "vue";
import {
  Bell,
  ChatDotSquare, Collection, DataLine,
  Document, Files,
  Location, Lock, Monitor,
  Notification, Operation,
  Position,
  School,
  Umbrella, User
} from "@element-plus/icons-vue";
const store=useStore()
const loading=ref(true)
get('api/user/info',(data)=>{
  store.user=data
  loading.value=false

})
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
  <div class="main-content" v-loading='loading' element-loading-text="正在进入，请稍后...">
    <el-container style="height: 100%" v-if="!loading">
      <el-header class="main-content-header">
        <el-image class="logo" src="https://element-plus.org/images/element-plus-logo.svg"></el-image>
        <div style="flex:1" class="user-info" >
          <div class="profile">
            <div>{{store.user.username}}</div>
            <div>{{store.user.email}}</div>
          </div>
          <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
        </div>
      </el-header>
      <el-container>
          <el-aside width="230px">
            <el-scrollbar style="height: calc(100vh - 55px)">
            <el-menu default-active="1-1" style="min-height: calc(100vh - 55px)">
              <el-sub-menu index="1">
                <template #title>
                  <el-icon><Location/></el-icon>
                  <span><b>校园论坛</b></span>
                </template>
                <el-menu-item index="1-1">
                  <template #title>
                    <el-icon><ChatDotSquare/></el-icon>
                    帖子广场
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Bell/></el-icon>
                    失物招领
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Notification/></el-icon>
                    校园活动
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Umbrella/></el-icon>
                    表白墙
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><School/></el-icon>
                    海文考研
                    <el-tag style="margin-left: 10px" size="small">合作机构</el-tag>
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="2">
                <template #title>
                  <el-icon><Position/></el-icon>
                  <span><b>探索与发现</b></span>
                </template>
                <el-menu-item>
                  <template #title>
                    <el-icon><Document/></el-icon>
                    成绩查询
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Files/></el-icon>
                    班级课程表
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Monitor/></el-icon>
                    教务通知
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Collection/></el-icon>
                    在线图书馆
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><DataLine/></el-icon>
                    预约教室
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="3">
                <template #title>
                  <el-icon><Operation/></el-icon>
                  <span><b>个人设置</b></span>
                </template>
                <el-menu-item>
                  <template #title>
                    <el-icon><User/></el-icon>
                    个人信息设置
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Lock/></el-icon>
                    账号安全设置
                  </template>
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
            </el-scrollbar>
          </el-aside>
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