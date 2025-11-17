<!-- 
  重置密码页面组件
  用户密码重置界面，包含邮箱验证和密码设置两个步骤
  
  @author 系统
  @version 1.0
  @since 2024
-->

<script setup>
/**
 * 导入Vue相关函数和Element Plus组件
 */
import {computed, reactive, ref} from "vue";
import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import {get, post} from "@/net/index.js";
import router from "@/router/index.js";

/**
 * 验证码冷却时间（秒）
 * 发送验证码后需要等待的时间
 */
const coldTime = ref(0)

/**
 * 当前激活的步骤索引
 * 0: 邮箱验证步骤
 * 1: 设置新密码步骤
 */
const active = ref(0)

/**
 * 重置密码按钮加载状态
 * 防止重复提交
 */
const loading = ref(false)

/**
 * 表单引用对象
 * 用于访问表单实例，进行表单验证操作
 */
const formRef = ref()

/**
 * 响应式表单数据对象
 * 包含重置密码表单的所有字段数据
 */
const form = reactive({
  email: '',           // 邮箱地址
  code: '',           // 邮箱验证码
  password: '',       // 新密码
  password_repeat: '' // 重复新密码
})

/**
 * 密码重复验证函数
 * 验证两次输入的密码是否一致
 */
const validatePassword = (rule, value, callback) => {
  if (value === "") {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

/**
 * 表单验证规则配置
 * 定义各字段的验证规则和错误提示信息
 */
const rules = {
  email: [
    // 邮箱验证：必填，邮箱格式验证
    {required: true, message: '请输入邮箱', trigger: 'blur'},
    {type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change']}
  ],
  code: [
    // 验证码验证：必填
    {required: true, message: '请输入验证码', trigger: 'blur'}
  ],
  password: [
    // 密码验证：必填，长度6-20个字符
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: ['blur', 'change']}
  ],
  password_repeat: [
    // 重复密码验证：自定义验证器，检查两次密码是否一致
    {validator: validatePassword, trigger: ['blur', 'change']}
  ]
}

/**
 * 计算属性：邮箱格式是否有效
 * 使用正则表达式验证邮箱格式
 */
const isEmailValid = computed(() => /^[\w.-]+@[\w.-]+\.\w+$/.test(form.email))

/**
 * 请求验证码函数
 * 向服务器请求发送邮箱验证码
 */
function askCode() {
  // 检查邮箱格式是否有效
  if (isEmailValid.value) {
    // 设置冷却时间为60秒
    coldTime.value = 60
    
    // 发送GET请求获取验证码
    get(`/api/auth/ask-code?email=${form.email}&type=reset`,
        // 成功回调：显示成功消息并开始倒计时
        () => {
          ElMessage.success(`验证码已发送至邮箱:${form.email}，请查收`)
          // 创建定时器，每秒减少冷却时间
          const timer = setInterval(() => {
            coldTime.value--
            // 当冷却时间结束时清除定时器
            if (coldTime.value <= 0) {
              clearInterval(timer)
            }
          }, 1000)
        },
        // 失败回调：显示错误消息并重置冷却时间
        (message) => {
          ElMessage.error(message)
          coldTime.value = 0
        }
    )
  } else {
    // 邮箱格式无效时提示错误
    ElMessage.error('请输入正确的邮箱地址')
  }
}

/**
 * 验证验证码并进入下一步
 * 验证邮箱验证码的正确性
 */
function confirmReset() {
  // 调用表单验证方法
  formRef.value.validate((valid) => {
    // 如果表单验证通过
    if (valid) {
      // 发送POST请求验证验证码
      post('/api/auth/reset-confirm', {
        email: form.email,
        code: form.code
      }, 
      // 成功回调：显示成功消息并进入下一步
      () => {
        ElMessage.success('验证码验证成功')
        active.value++
      }, 
      // 失败回调：显示错误消息
      (message) => {
        ElMessage.error('验证码错误：' + message)
      })
    }
  })
}

/**
 * 重置密码函数
 * 提交新密码到服务器进行重置
 */
function doReset() {
  // 调用表单验证方法
  formRef.value.validate((valid) => {
    // 如果表单验证通过
    if (valid) {
      // 设置加载状态为true，防止重复提交
      loading.value = true
      
      // 发送POST请求重置密码
      post('/api/auth/reset-password', {...form}, 
      // 成功回调：显示成功消息并跳转到登录页面
      () => {
        ElMessage.success("密码重置成功")
        router.push('/')
      }, 
      // 失败回调：显示错误消息
      (message) => {
        ElMessage.error('密码重置失败：' + message)
      }, 
      // 完成回调：重置加载状态
      () => {
        loading.value = false
      })
    }
  })
}
</script>

<template>
  <!-- 重置密码页面主容器 -->
  <div>
    
    <!-- 步骤指示器区域 -->
    <div style="text-align: center">
      <!-- Element Plus步骤组件 -->
      <el-steps style="margin-top: 30px" :active="active">
        <el-step title="验证电子邮箱"/>
        <el-step title="设置新密码"/>
      </el-steps>
    </div>
    
    <!-- 第一步：邮箱验证步骤 -->
    <div style="margin: 0 20px" v-if="active === 0">
      
      <!-- 页面标题区域 -->
      <div style="margin-top: 80px">
        <!-- 主标题：重置密码 -->
        <div style="font-size: 25px;font-weight: bold">重置密码</div>
        <!-- 副标题：提示信息 -->
        <div style="font-size: 14px;color: grey">请输入您的电子邮箱</div>
      </div>
      
      <!-- 邮箱验证表单区域 -->
      <div style="margin-top: 50px">
        <!-- Element Plus表单组件 -->
        <el-form :model="form" :rules="rules"  ref="formRef">
          
          <!-- 邮箱输入框 -->
          <el-form-item prop="email">
            <el-input v-model="form.email" type="email" placeholder="电子邮箱">
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
                <el-button @click="askCode" :disabled="!isEmailValid || coldTime>0" type="success">
                  {{coldTime > 0 ? '请稍后 ' + coldTime + ' 秒' : '获取验证码'}}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 下一步按钮区域 -->
      <div style="margin-top: 80px">
        <el-button @click="confirmReset" type="warning" style="width: 270px" plain>下一步</el-button>
      </div>
    </div>
    
    <!-- 第二步：设置新密码步骤 -->
    <div style="margin: 0 20px" v-if="active === 1">
      <div>
        
        <!-- 页面标题区域 -->
        <div style="margin-top: 80px">
          <!-- 主标题：重置密码 -->
          <div style="font-size: 25px;font-weight: bold">重置密码</div>
          <!-- 副标题：提示信息 -->
          <div style="font-size: 14px;color: grey">请设置您的新密码</div>
        </div>
        
        <!-- 密码设置表单区域 -->
        <div>
          <!-- Element Plus表单组件 -->
          <el-form :model="form" :rules="rules" ref="formRef">
            
            <!-- 新密码输入框 -->
            <el-form-item prop="password">
              <el-input v-model="form.password" maxlength="20" type="password" placeholder="密码">
                <template #prefix>
                  <el-icon><Lock/></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <!-- 重复新密码输入框 -->
            <el-form-item prop="password_repeat">
              <el-input v-model="form.password_repeat" maxlength="20" type="password" placeholder="重复密码">
                <template #prefix>
                  <el-icon><Lock/></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 重置密码按钮区域 -->
        <div style="margin-top: 80px">
          <el-button @click="doReset" type="danger" style="width: 270px" plain :loading="loading">
            重置密码
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/**
 * 重置密码页面样式区域
 * 使用scoped作用域限制，样式只对当前组件生效
 * 当前组件使用内联样式，此处暂无特殊样式需求
 */
</style>