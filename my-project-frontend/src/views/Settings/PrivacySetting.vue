<script setup>

import Card from "@/components/Card.vue";
import {Lock, Setting, Switch} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {get, post} from "@/net/index.js";
import {ElMessage} from "element-plus";
const form=reactive( {
  password: '',
  new_password: '',
  new_password_repeat: ''
})
const validatePassword = (rule, value, callback) => {
  if(value===''){
    callback(new Error('请再次输入密码'))}
  else if(value!==form.new_password){
    callback(new Error('两次输入的密码不一致'))}
  else{
    callback()}
}
const rules = {
  password: [
    { required: true, message: '请输入旧密码', trigger: 'blur' },
    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
  ],
  new_password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
  ],
  new_password_repeat: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { validator: validatePassword, trigger: ['blur', 'change']}
  ]
}
const formRef=ref()
const valid=ref(false)
const OnValidate=(prop,isValid)=>{
  valid.value=isValid
}
function resetPassword(){
  formRef.value.validate(valid=>{
    if(valid){
      post('api/user/change-password',form,()=>{
        ElMessage.success('修改密码成功')
        formRef.value.resetFields()
      })
    }
  })
}
const saving=ref(true)
const privacy=reactive({
  phone:false,
  email:false,
  wx:false,
  qq:false,
  gender:false
})
get('api/user/privacy',data=>{
  privacy.phone=data.phone
  privacy.email=data.email
  privacy.gender=data.gender
  privacy.wx=data.wx
  privacy.qq=data.qq
  saving.value=false
})
function savePrivacy(type,status){
  saving.value=true
  post('api/user/save-privacy', {
    type: type,
    status: status
  },()=>{
    ElMessage.success('隐私设置成功')
    saving.value=false
  })
}
</script>

<template>
  <div style="max-width: 600px;margin: auto">
    <div style="margin-top: 20px">
      <card :icon="Setting" title="隐私设置" desc="在这里可以设置哪些内容可以被其他人看到，各位请注意隐私" v-loading="saving">
        <div class="checkbox-list">
          <el-checkbox @change="savePrivacy('phone',privacy.phone)"
                       v-model="privacy.phone">公开我的手机号</el-checkbox>
          <el-checkbox @change="savePrivacy('email',privacy.email)"
                       v-model="privacy.email">公开我的邮箱</el-checkbox>
          <el-checkbox @change="savePrivacy('wx',privacy.wx)"
                       v-model="privacy.wx">公开我的微信号</el-checkbox>
          <el-checkbox @change="savePrivacy('qq',privacy.qq)"
                       v-model="privacy.qq">公开我的QQ号</el-checkbox>
          <el-checkbox @change="savePrivacy('gender',privacy.gender)"
                       v-model="privacy.gender">公开我的性别</el-checkbox>
        </div>
      </card>
      <card style="margin: 20px 0" :icon="Setting" tile="修改密码"
      desc="在这里修改你的密码，请务必牢记密码">
        <el-form :rules="rules" :model="form" ref="formRef" @validate="OnValidate" label-width="100" style="margin: 20px">
          <el-form-item label="旧密码" prop="password">
            <el-input type="password" :prefix-icon="Lock" v-model="form.password" placeholder="请输入旧密码" maxlength="16"></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="new_password">
            <el-input type="password" :prefix-icon="Lock" v-model="form.new_password" placeholder="请输入新密码" maxlength="16"></el-input>
          </el-form-item>
          <el-form-item label="确认新密码" prop="new_password_repeat">
            <el-input type="password" :prefix-icon="Lock" v-model="form.new_password_repeat" placeholder="确认你的密码" maxlength="16"></el-input>
          </el-form-item>
          <div style="text-align: center">
            <el-button @click="resetPassword" :icon="Switch" type="primary">修改密码</el-button>
          </div>
        </el-form>
      </card>
    </div>
  </div>
</template>

<style scoped>
.checkbox-list{
  margin: 10px 0 0 10px;
  display: flex;
  flex-direction: column;
}
</style>