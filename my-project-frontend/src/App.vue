<script setup>
/**
 * 主应用程序入口组件
 * 负责应用的整体布局、主题切换和路由管理
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */

// 导入VueUse的暗黑模式相关工具
import {useDark, useToggle} from "@vueuse/core";

/**
 * 配置暗黑模式
 * 设置HTML元素的类名切换，实现主题切换功能
 */
useDark({
  selector: 'html',        // 选择HTML元素
  attribute: 'class',      // 修改class属性
  valueDark: 'dark',      // 暗黑模式类名
  valueLight: 'light'     // 明亮模式类名
})

/**
 * 监听暗黑模式变化
 * 当主题切换时自动更新应用状态
 */
useDark({
  onChanged(dark){useToggle( dark)}  // 主题变化时触发切换
})
</script>

<template>
  <!-- 
    路由出口组件
    根据当前路由路径渲染对应的页面组件
  -->
  <router-view/>
</template>

<style scoped>
/* 
  作用域样式
  只对当前组件生效，不会影响其他组件
  用于定义App组件的特定样式
*/
</style>