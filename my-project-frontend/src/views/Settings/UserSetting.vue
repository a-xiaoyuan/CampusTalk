<script setup>


import Card from "@/components/Card.vue";
import {Message, Refresh, Select, User} from "@element-plus/icons-vue";
import {useStore} from "@/store/index.js";
import {computed} from "vue";
const store=useStore()
const registerTime = computed(() => new Date(store.user.registerTime).toLocaleString())
</script>

<template>
  <div style="display: flex">
    <div class="settings-left">
      <card :icon="User" title="账号信息设置" desc="在这里设置您的个人信息，您可以在隐私设置中展示您的个人信息">
        <el-form label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="用户名">
            <el-input/>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group>
              <el-radio label="男">男</el-radio>
              <el-radio label="女">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input/>
          </el-form-item>
          <el-form-item label="QQ号">
            <el-input/>
          </el-form-item>
          <el-form-item label="微信号">
            <el-input/>
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input type="textarea" :rows="6"/>
          </el-form-item>
          <div>
            <el-button type="primary" plain>
              <el-icon><Select /></el-icon>
              保存
            </el-button>
          </div>
        </el-form>
      </card>
      <card style="margin-top: 10px" :icon="Message" title="电子邮件设置" desc="您可以在这里修改默认绑定的电子邮件">
        <el-form label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="邮箱">
            <el-input/>
          </el-form-item>
          <el-form-item>
            <el-row style="width :100% " :gutter="10">
              <el-col :span="18">
                <el-input placeholder="请输入验证码"/>
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
