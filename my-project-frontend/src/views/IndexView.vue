<script setup>
import router from "@/router";
import {logout} from "@/net";

function userLogout(){
    // 调用注销接口
    logout(()=>
        // 注销成功后，跳转到登录页面
        router.push('/'))
}
</script>

<template>
  <div>
    <!-- 注销按钮 -->
    <el-button @click="userLogout">退出登录</el-button>
  </div>
</template>

<style scoped>

</style>