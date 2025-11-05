<!-- 欢迎页面视图组件 -->
<script setup>
// 组合式API设置脚本区域
// 当前组件不需要额外的JavaScript逻辑
</script>

<template>
  <!-- 页面容器，设置全屏显示 -->
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
    <!-- 左侧背景图片区域 -->
    <div style="flex: 1;background-color: black">
      <!-- 背景图片展示 -->
      <el-image style="width: 100%;height: 100%" fit="cover"
          src="https://th.bing.com/th/id/OIP.LltGYcnaHVAihJNBIB39egAAAA?w=150&h=108&c=7&bgcl=b3303c&r=0&o=6&dpr=1.5&pid=13.1"/>
    </div>
    
    <!-- 页面标题文字区域 -->
    <div class="welcome-title">
      <!-- 主标题 -->
      <div style="font-size: 30px;font-weight: bold">欢迎来到我们的学习平台</div>
      <!-- 副标题 -->
      <div style="margin-top: 10px">在这里可以学习各种技术</div>
    </div>
    
    <!-- 右侧卡片区域，用于显示子路由内容 -->
    <div class="right-card">
      <!-- 子路由视图出口 -->
      <router-view/>
    </div>
  </div>
</template>

<style scoped>
/* 欢迎页面标题样式 */
.welcome-title{
  position: absolute;         /* 绝对定位 */
  bottom: 30px;               /* 距离底部30px */
  left: 30px;                 /* 距离左侧30px */
  color: white;               /* 白色字体 */
  text-shadow: 0 0 10px black;/* 黑色文字阴影 */
}

/* 右侧卡片样式 */
.right-card{
  width: 400px;               /* 固定宽度400px */
  z-index: 1;                 /* 层级为1 */
  background-color: white;    /* 白色背景 */
}
</style>