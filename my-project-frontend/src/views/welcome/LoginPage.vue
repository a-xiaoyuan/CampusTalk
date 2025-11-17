<!-- 
  登录页面组件
  用户登录界面，包含用户名密码输入、表单验证和登录功能
  
  @author 系统
  @version 1.0
  @since 2024
-->

<script setup>
/**
 * 导入Element Plus图标组件
 * 用于表单输入框的前缀图标
 */
import {Lock, User} from "@element-plus/icons-vue";

/**
 * 导入Vue响应式相关函数
 * reactive: 创建响应式对象
 * ref: 创建响应式引用
 */
import {reactive, ref} from "vue";

/**
 * 导入自定义网络请求方法和路由实例
 * login: 登录接口调用函数
 * router: Vue Router实例，用于页面跳转
 */
import {login} from "@/net/index.js";
import router from "@/router/index.js";

/**
 * 表单引用对象
 * 用于访问表单实例，进行表单验证操作
 */
const formRef=ref()

/**
 * 响应式表单数据对象
 * 包含登录表单的所有字段数据
 */
const form=reactive({
  username:'',      // 用户名或邮箱
  password:'',      // 用户密码
  remember:false    // 是否记住登录状态（持久化令牌）
})

/**
 * 表单验证规则配置
 * 定义各字段的验证规则和错误提示信息
 */
const rule={
  username:[
    // 用户名必填验证：用户名不能为空
    {required:true,message:'请输入用户名'}
  ],
  password:[
    // 密码必填验证：密码不能为空
    {required:true,message:'请输入密码'}
  ]
}

/**
 * 用户登录函数
 * 处理登录表单提交，包含表单验证和登录请求
 */
function userLogin(){
  // 调用表单验证方法
  formRef.value.validate(( valid)=>{
    // 如果表单验证通过
    if(valid){
      // 调用网络请求模块的登录函数
      // 参数：用户名、密码、记住登录状态、登录成功回调
      login(form.username,form.password,form.remember,()=>
        // 登录成功后跳转到主页面
        router.push('/index'))
    }
  })
}
</script>

<template>
  <!-- 
    登录页面主容器
    居中布局，设置左右边距
  -->
  <div style="text-align:center;margin: 0 20px">
    
    <!-- 页面标题区域 -->
    <div style="margin-top:150px ">
      <!-- 主标题：登录 -->
      <div style="font-size: 25px;font-weight: bold">登录</div>
      <!-- 副标题：登录提示信息 -->
      <div style="font-size: 14px;color: grey">在进入系统之前请先登录</div>
    </div>
    
    <!-- 登录表单区域 -->
    <div style="margin-top: 50px" >
      <!-- 
        Element Plus表单组件
        绑定表单数据和验证规则
      -->
      <el-form :model="form" :rules="rule" ref="formRef">
        
        <!-- 用户名输入框表单项 -->
        <el-form-item prop="username">
          <!-- 
            Element Plus输入框组件
            绑定用户名数据，设置最大长度和占位符
          -->
          <el-input v-model="form.username" maxlength="10" type="text" placeholder="用户名/邮箱">
            <!-- 输入框前缀图标：用户图标 -->
            <template #prefix>
              <el-icon><User/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <!-- 密码输入框表单项 -->
        <el-form-item  prop="password">
          <!-- 
            Element Plus密码输入框组件
            绑定密码数据，设置最大长度和占位符
          -->
          <el-input v-model="form.password" maxlength="20" type="password" placeholder="密码">
            <!-- 输入框前缀图标：锁图标 -->
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
              <!-- 记住密码复选框 -->
              <el-checkbox v-model="form.remember">记住密码</el-checkbox>
            </el-form-item>
          </el-col >
          
          <!-- 右侧：忘记密码链接 -->
          <el-col :span="12" style="text-align: right">
            <!-- 忘记密码链接，点击跳转到重置密码页面 -->
            <el-link @click="router.push('/reset')" >忘记密码？</el-link>
          </el-col>
        </el-row>
      </el-form>
    </div>
    
    <!-- 登录按钮区域 -->
    <div style="margin-top: 30px">
      <!-- 
        登录按钮
        点击触发用户登录函数
      -->
      <el-button @click="userLogin" type="success" style="width: 270px" plain>登录</el-button>
    </div>
    
    <!-- 分割线区域 -->
    <el-divider>
      <!-- 分割线中间文字 -->
      <span style="font-size: 13px;color:grey" >没有账号？</span>
    </el-divider>
    
    <!-- 注册按钮 -->
    <el-button @click="router.push('/register')"  type="primary" style="width: 270px" plain>注册</el-button>
  </div>
</template>

<style scoped>
/**
 * 登录页面样式区域
 * 使用scoped作用域限制，样式只对当前组件生效
 * 当前组件使用内联样式，此处暂无特殊样式需求
 */
</style>