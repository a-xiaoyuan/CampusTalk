<script setup>

</script>

<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
    <div style="flex: 1;background-color: black">
      <el-image style="width: 100%;height: 100%" fit="cover"
          src="https://th.bing.com/th/id/OIP.L68XlkffJLxCNGDaGCC_rgHaE8?w=150&h=108&c=7&bgcl=f75af5&r=0&o=6&dpr=1.5&pid=13.1"/>
    </div>
    <div class="welcome-title">
      <div style="font-size: 30px;font-weight: bold">欢迎来到我们的学习平台</div>
      <div style="margin-top: 10px">在这里可以学习各种技术</div>
    </div>
    <div class="right-card">

    </div>
  </div>
</template>

<style scoped>
.welcome-title{
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 10px black;
}
.right-card{
  width: 400px;
  z-index: 1;
  background-color: white;
}
</style>