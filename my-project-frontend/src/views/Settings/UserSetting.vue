<script setup>


import Card from "@/components/Card.vue";
import {Message, Refresh, Select, User} from "@element-plus/icons-vue";
import {useStore} from "@/store/index.js";
import {computed, reactive, ref} from "vue";
import {get, post} from "@/net/index.js";
import {ElMessage} from "element-plus";
const store=useStore()
const baseFromRef = ref()
const emailFromRef = ref()
const desc = ref('')
const registerTime = computed(() => new Date(store.user.registerTime).toLocaleString())
const baseFrom=reactive({
  username:'',
  gender:1,
  phone:'',
  qq:'',
  wx:'',
  desc:''
})
const loading =reactive(
    {
      form:true,
      base:false
    }
)
const emailFrom=reactive({
  email:'',
  code:''
})
const validateUsername = (rule,value,callback)=>{
  if(value === ""){
    callback(new Error('请输入用户名'))
  }else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test( value)){
    callback(new Error('用户名不能包含特殊字符'))
  }else{
    callback()
  }
}
const rules = {
    username: [
      {trigger: ['blur', 'change'], validator: validateUsername},
      {min: 2, max: 10, message: '用户名长度在 2 到 10 个字符',trigger: ['blur', 'change']}
    ],email:[
      {required:true,message:'请输入邮箱',trigger:'blur'},
      {type:'email',message:'请输入正确的邮箱地址',trigger:['blur','change']}
    ],
    code:[
      {required:true,message:'请输入验证码',trigger:'blur'}
    ],
    desc:[
      {required:true,message:'请输入个人简介',trigger:'blur'},
      {min: 10, max: 200, message: '个人简介长度在 10 到 200 个字符',trigger: ['blur', 'change']}
    ]
  }
function saveDetails(){
  baseFromRef.value.validate(isValid =>{
    if(isValid){
      loading.base = true
      post('/api/user/save-details',baseFrom,()=>{
        ElMessage.success('用户信息保存成功');
        store.user.username = baseFrom.username
        desc.value = baseFrom.desc
        loading.base = false
      },(message)=>{
        ElMessage.error(message)
        loading.base = false
      })
    }
  })
}
get('/api/user/details',data=>{
  baseFrom.username = store.user.username
  baseFrom.gender = data.gender
  baseFrom.phone = data.phone
  baseFrom.qq = data.qq
  baseFrom.wx = data.wx
  baseFrom.desc =desc.value= data.desc
  loading.base = false
  loading.form = false
},(message)=>{
  ElMessage.error('获取用户信息失败: ' + message)
  loading.base = false
  loading.form = false
})
</script>

<template>
  <div style="display: flex">
    <div class="settings-left">
      <card :icon="User" title="账号信息设置" desc="在这里设置您的个人信息，您可以在隐私设置中展示您的个人信息" v-loading="loading.form">
        <el-form :model="baseFrom" :rules="rules" ref="baseFrom" label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="baseFrom.username" maxlength="10"/>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="baseFrom.gender">
              <el-radio :label="0">男</el-radio>
              <el-radio :label="1">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="baseFrom.phone" maxlength="11"/>
          </el-form-item>
          <el-form-item label="QQ号" prop="qq">
            <el-input v-model="baseFrom.qq" maxlength="13"/>
          </el-form-item>
          <el-form-item label="微信号" prop="wx">
            <el-input v-model="baseFrom.wx" maxlength="20"/>
          </el-form-item>
          <el-form-item label="个人简介" prop="desc">
            <el-input v-model="baseFrom.desc" type="textarea" :rows="6" maxlength="200"/>
          </el-form-item>
          <div>
            <el-button @click="saveDetails" :loading="loading.base" type="primary" plain>
              <el-icon><Select /></el-icon>
              保存
            </el-button>
          </div>
        </el-form>
      </card>
      <card style="margin-top: 10px" :icon="Message" title="电子邮件设置" desc="您可以在这里修改默认绑定的电子邮件">
        <el-form :model="emailFrom" :rules="rules" ref="emailFrom" label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="emailFrom.email"/>
          </el-form-item>
          <el-form-item prop="code">
            <el-row style="width :100% " :gutter="10">
              <el-col :span="18">
                <el-input placeholder="请输入验证码" v-model="emailFrom.code"/>
              </el-col>
              <el-col :span="6">
                <el-button type="primary"  style="width: 100px" plain>获取验证码</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <div>
            <el-button type="primary" plain>
              <el-icon><Refresh /></el-icon>
              保存
            </el-button>
          </div>

        </el-form>
      </card>
    </div>
    <div class="settings-right">
      <div style="position: sticky;top :20px">
        <card>
          <div style="text-align: center ;padding: 5px 15px 0 15px">
            <div>
              <el-avatar :size="70" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
              <div style="font-weight: bold">你好，{{store.user.username}}</div>
            </div>
            <el-divider style="margin: 10px 0"/>
            <div style="font-size: 14px;color: gray;padding: 10px">
              {{ desc || '这个人很懒，什么都没留下'}}
            </div>
          </div>
        </card>
        <card style="margin-top: 10px;font-size: 14px">
          <div>账号注册时间：{{registerTime}}</div>
          <div style="color: gray">欢迎加入校园论坛</div>
        </card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.settings-left {
  flex: 1;
  margin: 20px;
}

.settings-right {
  width: 300px;
  margin: 20px 30px 20px 0;
}
</style>
