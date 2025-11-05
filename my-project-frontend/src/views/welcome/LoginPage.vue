<script setup>
import {Lock, User} from "@element-plus/icons-vue";
import {reactive} from "vue";
const form=reactive({
  username:'',
  password:'',
  remember:false
})
</script>

<template>
  <div style="text-align:center;margin: 0 20px">
    <div style="margin-top:150px ">
      <div style="font-size: 25px;font-weight: bold">登录</div>
      <div style="font-size: 14px;color: grey">在进入系统之前请先登录</div>
    </div>
    <div style="margin-top: 50px">
      <el-form v-model="form">
        <el-form-item>
          <el-input v-model="form.username" maxlength="10" type="text" placeholder="用户名/邮箱">
            <template #prefix>
              <el-icon><User/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item >
          <el-input v-model="form.password" maxlength="20" placeholder="密码">
            <template #prefix>
              <el-icon><Lock/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-row>
          <el-col :span="12" style="text-align: left" >
            <el-form-item>
              <el-checkbox v-model="form.remember">记住密码</el-checkbox>
            </el-form-item>
          </el-col >
          <el-col :span="12" style="text-align: right">
            <el-link>忘记密码？</el-link>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div style="margin-top: 30px">
      <el-button type="success" style="width: 270px" plain>登录</el-button>
    </div>
    <el-divider>
      <span style="font-size: 13px;color:grey" >没有账号？</span>
    </el-divider>
    <el-button type="primary" style="width: 270px" plain>注册</el-button>
  </div>
</template>

<style scoped>

</style>