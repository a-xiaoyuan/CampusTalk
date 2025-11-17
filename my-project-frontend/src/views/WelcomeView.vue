<!-- 
  欢迎页面视图组件
  作为应用的入口页面，包含登录、注册、重置密码等子页面
  
  @author 系统
  @version 1.0
  @since 2024
-->

<script setup>
/**
 * 欢迎页面脚本区域
 * 使用Vue 3的组合式API语法
 * 当前组件作为容器组件，主要处理布局和路由嵌套
 */
</script>

<template>
  <!-- 
    页面主容器
    设置全屏显示，使用flex布局实现左右分栏
  -->
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
    
    <!-- 
      左侧背景图片区域
      占据剩余空间，显示背景图片
    -->
    <div style="flex: 1;background-color: black">
      <!-- 
        Element Plus图片组件
        显示背景图片，cover模式确保图片覆盖整个区域
      -->
      <el-image style="width: 100%;height: 100%" fit="cover"
          src="https://i02piccdn.sogoucdn.com/a697cb97322ce62b"/>
    </div>
    
    <!-- 
      页面标题文字区域
      绝对定位在左下角，显示欢迎文字
    -->
    <div class="welcome-title">
      <!-- 主标题：欢迎语 -->
      <div style="font-size: 30px;font-weight: bold">欢迎来到我们的学习平台</div>
      <!-- 副标题：平台描述 -->
      <div style="margin-top: 10px">在这里可以学习各种技术</div>
    </div>
    
    <!-- 
      右侧卡片区域
      固定宽度，用于显示登录/注册/重置密码表单
    -->
    <div class="right-card">
      <!-- 
        子路由视图出口
        根据当前路由显示对应的子页面组件
      -->
      <router-view v-slot="{Component}">
        <!-- 
          Element Plus淡入过渡动画
          提供平滑的页面切换效果
        -->
        <transition name="el-fade-in-linear">
          <!-- 动态组件，渲染当前激活的子路由组件 -->
          <component :is="Component"/>
        </transition>
      </router-view>
    </div>
  </div>
</template>

<style scoped>
/**
 * 欢迎页面标题样式
 * 绝对定位在页面左下角，白色文字带黑色阴影
 */
.welcome-title{
  position: absolute;         /* 绝对定位，脱离文档流 */
  bottom: 30px;               /* 距离底部30px */
  left: 30px;                 /* 距离左侧30px */
  color: white;               /* 白色字体颜色 */
  text-shadow: 0 0 10px black;/* 黑色文字阴影，增强可读性 */
}

/**
 * 右侧卡片样式
 * 固定宽度，用于显示表单内容
 */
.right-card{
  width: 400px;               /* 固定宽度400px */
  z-index: 1;                 /* 层级为1，确保在背景图片之上 */
  background-color: var(--el-bg-color);    /* 使用Element Plus主题背景色 */
}
</style>