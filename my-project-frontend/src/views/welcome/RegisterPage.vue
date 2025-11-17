<!-- 
  注册页面组件
  用户注册界面，包含用户名、密码、邮箱验证等表单字段和验证逻辑
  
  @author 系统
  @version 1.0
  @since 2024
-->

<script setup>
/**
 * 导入Vue相关函数和Element Plus组件
 */
import {computed, reactive, ref} from "vue";
import {EditPen, Lock, Message, User} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {ElMessage} from "element-plus";
import {get, post} from "@/net";

/**
 * 验证码冷却时间（秒）
 * 发送验证码后需要等待的时间
 */
const coldTime=ref(0)

/**
 * 表单引用对象
 * 用于访问表单实例，进行表单验证操作
 */
const formRef=ref()

/**
 * 响应式表单数据对象
 * 包含注册表单的所有字段数据
 */
const form=reactive({
    username:'',           // 用户名
    password:'',           // 密码
    password_repeat:'',     // 重复密码
    email:'',              // 邮箱地址
    code:''                // 邮箱验证码
})

/**
 * 用户名验证函数
 * 验证用户名格式：不能为空且不能包含特殊字符
 */
const validateUsername=(rule,value,callback)=>{
    if(value === ""){
        callback(new Error('请输入用户名'))
    }else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test( value)){
        callback(new Error('用户名不能包含特殊字符'))
    }else{
        callback()
    }
}

/**
 * 密码重复验证函数
 * 验证两次输入的密码是否一致
 */
const validatePassword=(rule,value,callback)=>{
    if(value === ""){
        callback(new Error('请再次输入密码'))
    }else if(value!==form.password){
        callback(new Error('两次输入的密码不一致'))
    }else{
        callback()
    }
}

/**
 * 表单验证规则配置
 * 定义各字段的验证规则和错误提示信息
 */
const rule={
    username:[
        // 用户名验证：自定义验证器，触发时机为失去焦点和内容变化
        {validator:validateUsername,trigger:['blur','change']}
    ],
    password:[
        // 密码验证：必填，长度6-20个字符
        {required:true,message:'请输入密码',trigger:'blur'},
        {min:6,max:20,message:'密码长度在6-20个字符之间',trigger:['blur','change']}
    ],
    password_repeat:[
        // 重复密码验证：自定义验证器，检查两次密码是否一致
        {validator:validatePassword,trigger:['blur','change']}
    ],
    email:[
        // 邮箱验证：必填，邮箱格式验证
        {required:true,message:'请输入邮箱',trigger:'blur'},
        {type:'email',message:'请输入正确的邮箱地址',trigger:['blur','change']}
    ],
    code:[
        // 验证码验证：必填
        {required:true,message:'请输入验证码',trigger:'blur'}
    ]
}

/**
 * 请求验证码函数
 * 向服务器请求发送邮箱验证码
 */
function askCode(){
  // 检查邮箱格式是否有效
  if(isEmailValid){
    // 设置冷却时间为60秒
    coldTime.value=60
    
    // 发送GET请求获取验证码
    get(`/api/auth/ask-code?email=${form.email}&type=register`,
        // 成功回调：显示成功消息并开始倒计时
        () => {
          ElMessage.success(`验证码已发送至邮箱:${form.email}，请查收`)
          // 每秒减少冷却时间
          setInterval(()=>coldTime.value--,1000)
        },
        // 失败回调：显示错误消息并重置冷却时间
        (message) => {
          ElMessage.error(message)
          coldTime.value=0
        }
    )
  }else {
    // 邮箱格式无效时提示错误
    ElMessage.error('请输入正确的邮箱地址')
  }
}

/**
 * 计算属性：邮箱格式是否有效
 * 使用正则表达式验证邮箱格式
 */
const isEmailValid=computed( ()=>/^[\w.-]+@[\w.-]+\.\w+$/.test(form.email))

/**
 * 用户注册函数
 * 处理注册表单提交，包含表单验证和注册请求
 */
function register(){
  // 调用表单验证方法
  formRef.value.validate((valid)=>{
    // 如果表单验证通过
    if(valid){
      // 发送POST请求进行用户注册
      post('api/auth/register',{...form},()=>{
        // 注册成功回调：显示成功消息并跳转到登录页面
        ElMessage.success("注册成功，欢迎加入我们")
        router.push('/')
      })
    }else{
      // 表单验证失败时提示警告
      ElMessage.warning('请检查输入信息')
    }

  })
}
</script>

<template>
  <!-- 注册页面主容器 -->
  <div style="text-align: center;margin:0 20px">
    
    <!-- 页面标题区域 -->
    <div style="margin-top:100px">
      <!-- 主标题：注册新用户 -->
      <div style="font-size: 25px;font-weight: bold">注册新用户</div>
      <!-- 副标题：注册提示信息 -->
      <div style="font-size: 14px;color: grey">欢迎注册我们的学习平台，请在下方填写相关信息</div>
    </div>
    
    <!-- 注册表单区域 -->
    <div style="margin-top:50px">
      <!-- Element Plus表单组件 -->
      <el-form :model="form" :rules="rule"  label-width="0" ref="formRef">
        
        <!-- 用户名输入框 -->
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="10" type="text" placeholder="用户名">
            <template #prefix>
              <el-icon><User/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <!-- 密码输入框 -->
        <el-form-item prop="password">
          <el-input v-model="form.password" maxlength="20" type="password" placeholder="密码">
            <template #prefix>
              <el-icon><Lock/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <!-- 重复密码输入框 -->
        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" maxlength="20" type="password" placeholder="重复密码">
            <template #prefix>
              <el-icon><Lock/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <!-- 邮箱输入框 -->
        <el-form-item prop="email">
          <el-input v-model="form.email" maxlength="20" type="text" placeholder="邮箱">
            <template #prefix>
              <el-icon><Message/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <!-- 验证码输入区域 -->
        <el-form-item prop="code">
          <el-row :gutter="10" style="width: 100%">
            <!-- 验证码输入框 -->
            <el-col :span="17">
              <el-input v-model="form.code" maxlength="6" type="text" placeholder="请输入验证码">
                <template #prefix>
                  <el-icon><EditPen/></el-icon>
                </template>
              </el-input>
            </el-col>
            <!-- 获取验证码按钮 -->
            <el-col :span="5">
              <el-button @click="askCode" :disabled="!isEmailValid || coldTime" type="success">
                {{coldTime > 0 ? '请稍后 ' + coldTime + ' 秒' : '获取验证码'}}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 注册按钮区域 -->
    <div style="margin-top:80px">
      <el-button @click="register" type="warning" style="width: 270px" plain>立即注册</el-button>
    </div>
    
    <!-- 登录链接区域 -->
    <div style="margin-top:20px">
      <span style="font-size: 14px;line-height: 15px;color: gray">已有账号？</span>
      <el-link style="translate: 0 -1px" @click="router.push('/')">立即登录</el-link>
    </div>
  </div>
</template>

<style scoped>
/**
 * 注册页面样式区域
 * 使用scoped作用域限制，样式只对当前组件生效
 * 当前组件使用内联样式，此处暂无特殊样式需求
 */
</style>