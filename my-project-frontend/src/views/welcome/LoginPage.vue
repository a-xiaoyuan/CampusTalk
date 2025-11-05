<script setup>
import {Lock, User} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {login} from "@/net/index.js";
import {ref} from "vue";
const formRef=ref()
const form=reactive({
  username:'',
  password:'',
  remember:false
})
const rule={
  username:[
    {required:true,message:'请输入用户名'}
  ],
  password:[
    {required:true,message:'请输入密码'}
  ]
}
function userLogin(){
  formRef.value.validate(( valid)=>{
    if(valid){
      login(form.username,form.password,form.remember,()=>{})
    }
  })
}
</script>

<template>
  <div style="text-align:center;margin: 0 20px">
    <div style="margin-top:150px ">
      <div style="font-size: 25px;font-weight: bold">登录</div>
      <div style="font-size: 14px;color: grey">在进入系统之前请先登录</div>
    </div>
    <div style="margin-top: 50px" >
      <el-form :model="form" :rules="rule" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="10" type="text" placeholder="用户名/邮箱">
            <template #prefix>
              <el-icon><User/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item  prop="password">
          <el-input v-model="form.password" maxlength="20" type="password" placeholder="密码">
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
      <el-button @click="userLogin" type="success" style="width: 270px" plain>登录</el-button>
    </div>
    <el-divider>
      <span style="font-size: 13px;color:grey" >没有账号？</span>
    </el-divider>
    <el-button type="primary" style="width: 270px" plain>注册</el-button>
  </div>
</template>

<style scoped>

</style>