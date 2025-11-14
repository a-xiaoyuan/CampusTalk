<!-- 登录页面组件 -->
<script setup>
// 导入Element Plus图标组件
import {Lock, User} from "@element-plus/icons-vue";
// 导入Vue响应式相关函数
import {reactive, ref} from "vue";
// 导入自定义网络请求方法
import {login} from "@/net/index.js";
import router from "@/router/index.js";

// 创建表单引用，用于表单验证
const formRef=ref()

// 创建响应式表单数据对象
const form=reactive({
  username:'',      // 用户名
  password:'',      // 密码
  remember:false    // 是否记住登录状态
})

// 表单验证规则
const rule={
  username:[
    // 用户名必填验证规则
    {required:true,message:'请输入用户名'}
  ],
  password:[
    // 密码必填验证规则
    {required:true,message:'请输入密码'}
  ]
}

/**
 * 用户登录函数
 */
function userLogin(){
  // 表单验证
  formRef.value.validate(( valid)=>{
    // 如果验证通过
    if(valid){
      // 调用登录接口
      login(form.username,form.password,form.remember,()=>router.push('/index'))
    }
  })
}
</script>

<template>
  <!-- 登录页面容器 -->
  <div style="text-align:center;margin: 0 20px">
    <!-- 页面标题区域 -->
    <div style="margin-top:150px ">
      <!-- 主标题 -->
      <div style="font-size: 25px;font-weight: bold">登录</div>
      <!-- 副标题 -->
      <div style="font-size: 14px;color: grey">在进入系统之前请先登录</div>
    </div>
    
    <!-- 登录表单区域 -->
    <div style="margin-top: 50px" >
      <!-- 登录表单 -->
      <el-form :model="form" :rules="rule" ref="formRef">
        <!-- 用户名输入框 -->
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="10" type="text" placeholder="用户名/邮箱">
            <!-- 输入框前缀图标 -->
            <template #prefix>
              <el-icon><User/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <!-- 密码输入框 -->
        <el-form-item  prop="password">
          <el-input v-model="form.password" maxlength="20" type="password" placeholder="密码">
            <!-- 输入框前缀图标 -->
            <template #prefix>
              <el-icon><Lock/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <!-- 记住密码和忘记密码行 -->
        <el-row>
          <!-- 左侧：记住密码复选框 -->
          <el-col :span="12" style="text-align: left" >
            <el-form-item>
              <el-checkbox v-model="form.remember">记住密码</el-checkbox>
            </el-form-item>
          </el-col >
          
          <!-- 右侧：忘记密码链接 -->
          <el-col :span="12" style="text-align: right">
            <el-link @click="router.push('/reset')" >忘记密码？</el-link>
          </el-col>
        </el-row>
      </el-form>
    </div>
    
    <!-- 登录按钮区域 -->
    <div style="margin-top: 30px">
      <!-- 登录按钮 -->
      <el-button @click="userLogin" type="success" style="width: 270px" plain>登录</el-button>
    </div>
    
    <!-- 分割线 -->
    <el-divider>
      <span style="font-size: 13px;color:grey" >没有账号？</span>
    </el-divider>
    
    <!-- 注册按钮 -->
    <el-button @click="router.push('/register')"  type="primary" style="width: 270px" plain>注册</el-button>
  </div>
</template>

<style scoped>
/* 样式作用域限制，只对当前组件生效 */
/* 当前组件暂无特殊样式需求 */
</style>