<script setup>
import {computed, reactive, ref} from "vue";
import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import {get, post} from "@/net/index.js";
import router from "@/router/index.js";

const coldTime = ref(0)
const active = ref(0)
const loading = ref(false)
const formRef = ref()
const form = reactive({
  email: '',
  code: '',
  password: '',
  password_repeat: ''
})

const validatePassword = (rule, value, callback) => {
  if (value === "") {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  email: [
    {required: true, message: '请输入邮箱', trigger: 'blur'},
    {type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change']}
  ],
  code: [
    {required: true, message: '请输入验证码', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: ['blur', 'change']}
  ],
  password_repeat: [
    {validator: validatePassword, trigger: ['blur', 'change']}
  ]
}

const isEmailValid = computed(() => /^[\w.-]+@[\w.-]+\.\w+$/.test(form.email))

// 获取验证码
function askCode() {
  if (isEmailValid.value) {
    coldTime.value = 60
    get(`/api/auth/ask-code?email=${form.email}&type=reset`,
        () => {
          ElMessage.success(`验证码已发送至邮箱:${form.email}，请查收`)
          const timer = setInterval(() => {
            coldTime.value--
            if (coldTime.value <= 0) {
              clearInterval(timer)
            }
          }, 1000)
        },
        (message) => {
          ElMessage.error(message)
          coldTime.value = 0
        }
    )
  } else {
    ElMessage.error('请输入正确的邮箱地址')
  }
}

// 验证验证码并进入下一步
function confirmReset() {
  formRef.value.validate((valid) => {
    if (valid) {
      post('/api/auth/reset-confirm', {
        email: form.email,
        code: form.code
      }, () => {
        ElMessage.success('验证码验证成功')
        active.value++
      }, (message) => {
        ElMessage.error('验证码错误：' + message)
      })
    }
  })
}

// 重置密码
function doReset() {
  formRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      post('/api/auth/reset-password', {...form}, () => {
        ElMessage.success("密码重置成功")
        router.push('/')
      }, (message) => {
        ElMessage.error('密码重置失败：' + message)
      }, () => {
        loading.value = false
      })
    }
  })
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
        <el-form :model="form" :rules="rules"  ref="formRef">
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
                <el-button @click="askCode" :disabled="!isEmailValid || coldTime>0" type="success">
                  {{coldTime > 0 ? '请稍后 ' + coldTime + ' 秒' : '获取验证码'}}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </div>
      <div style="margin-top: 80px">
        <el-button @click="confirmReset" type="warning" style="width: 270px" plain>下一步</el-button>
      </div>
    </div>
    <div style="margin: 0 20px" v-if="active === 1">
      <div>
        <div style="margin-top: 80px">
          <div style="font-size: 25px;font-weight: bold">重置密码</div>
          <div style="font-size: 14px;color: grey">请设置您的新密码</div>
        </div>
        <div>
          <el-form :model="form" :rules="rules" ref="formRef">
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
          <el-button @click="doReset" type="danger" style="width: 270px" plain :loading="loading">
            重置密码
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>