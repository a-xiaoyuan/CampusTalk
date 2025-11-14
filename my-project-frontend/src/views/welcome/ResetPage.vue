<script setup>
import {reactive, ref} from "vue";
import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";

const active = ref(0)
const form = reactive({
  email: '',
  code: '',
  password: '',
  password_repeat: ''
})
const loading = ref(false)
const codeLoading = ref(false)

// 验证邮箱格式
const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

// 获取验证码
const sendVerificationCode = async () => {
  if (!form.email) {
    ElMessage.warning('请输入电子邮箱')
    return
  }
  
  if (!validateEmail(form.email)) {
    ElMessage.error('请输入正确的电子邮箱格式')
    return
  }
  
  codeLoading.value = true
  try {
    // 这里应该调用后端API发送验证码
    // await axios.post('/api/auth/send-reset-code', { email: form.email })
    
    // 模拟API调用
    setTimeout(() => {
      ElMessage.success('验证码已发送到您的邮箱')
      codeLoading.value = false
    }, 1000)
  } catch (error) {
    ElMessage.error('发送验证码失败，请重试')
    codeLoading.value = false
  }
}

// 下一步
const nextStep = () => {
  if (!form.email) {
    ElMessage.warning('请输入电子邮箱')
    return
  }
  
  if (!validateEmail(form.email)) {
    ElMessage.error('请输入正确的电子邮箱格式')
    return
  }
  
  if (!form.code) {
    ElMessage.warning('请输入验证码')
    return
  }
  
  if (form.code.length !== 6) {
    ElMessage.warning('验证码应为6位数字')
    return
  }
  
  active.value = 1
}

// 重置密码
const resetPassword = async () => {
  if (!form.password) {
    ElMessage.warning('请输入新密码')
    return
  }
  
  if (form.password.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    return
  }
  
  if (form.password !== form.password_repeat) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  loading.value = true
  try {
    // 这里应该调用后端API重置密码
    // await axios.post('/api/auth/reset-password', {
    //   email: form.email,
    //   code: form.code,
    //   password: form.password
    // })
    
    // 模拟API调用
    setTimeout(() => {
      ElMessage.success('密码重置成功')
      loading.value = false
      // 可以跳转到登录页面
      // router.push('/login')
    }, 1000)
  } catch (error) {
    ElMessage.error('密码重置失败，请重试')
    loading.value = false
  }
}
</script>

<template>
  <div>
    <div style="text-align: center">
      <el-steps style="margin-top: 30px" :active="active">
        <el-step title="验证电子邮箱"/>
        <el-step title="设置新密码"/>
      </el-steps>
    </div>
    <div style="margin: 0 20px" v-if="active === 0">
      <div style="margin-top: 80px">
        <div style="font-size: 25px;font-weight: bold">重置密码</div>
        <div style="font-size: 14px;color: grey">请输入您的电子邮箱</div>
      </div>
      <div style="margin-top: 50px">
        <el-form :model="form">
          <el-form-item prop="email">
            <el-input v-model="form.email" type="email" placeholder="电子邮箱">
              <template #prefix>
                <el-icon><Message/></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code">
            <el-row :gutter="10" style="width: 100%">
              <el-col :span="17">
                <el-input v-model="form.code" maxlength="6" type="text" placeholder="请输入验证码">
                  <template #prefix>
                    <el-icon><EditPen/></el-icon>
                  </template>
                </el-input>
              </el-col>
              <el-col :span="5">
                <el-button @click="sendVerificationCode" type="success" :loading="codeLoading">
                  获取验证码
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </div>
      <div style="margin-top: 80px">
        <el-button @click="nextStep" type="warning" style="width: 270px" plain>下一步</el-button>
      </div>
    </div>
    <div style="margin: 0 20px" v-if="active === 1">
      <div>
        <div style="margin-top: 80px">
          <div style="font-size: 25px;font-weight: bold">重置密码</div>
          <div style="font-size: 14px;color: grey">请设置您的新密码</div>
        </div>
        <div>
          <el-form :model="form">
            <el-form-item prop="password">
              <el-input v-model="form.password" maxlength="20" type="password" placeholder="密码">
                <template #prefix>
                  <el-icon><Lock/></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password_repeat">
              <el-input v-model="form.password_repeat" maxlength="20" type="password" placeholder="重复密码">
                <template #prefix>
                  <el-icon><Lock/></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin-top: 80px">
          <el-button @click="resetPassword" type="danger" style="width: 270px" plain :loading="loading">
            重置密码
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>