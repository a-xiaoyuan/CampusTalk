<script setup>

import Card from "@/components/Card.vue";
import {Lock, Setting, Switch} from "@element-plus/icons-vue";
</script>

<template>
  <div style="max-width: 600px;margin: auto">
    <div style="margin-top: 20px">
      <card :icon="Setting" title="隐私设置" desc="在这里可以设置哪些内容可以被其他人看到，各位请注意隐私">
        <div class="checkbox-list">
          <el-checkbox>公开我的手机号</el-checkbox>
          <el-checkbox>公开我的邮箱</el-checkbox>
          <el-checkbox>公开我的微信号</el-checkbox>
          <el-checkbox>公开我的QQ号</el-checkbox>
          <el-checkbox>公开我的性别</el-checkbox>
        </div>
      </card>
      <card style="margin: 20px 0" :icon="Setting" tile="修改密码"
      desc="在这里修改你的密码，请务必牢记密码">
        <el-form label-width="100" style="margin: 20px">
          <el-form-item label="旧密码">
            <el-input :prefix-icon="Lock" placeholder="请输入旧密码" maxlength="16"></el-input>
          </el-form-item>
          <el-form-item label="新密码">
            <el-input :prefix-icon="Lock" placeholder="请输入旧密码" maxlength="16"></el-input>
          </el-form-item>
          <el-form-item label="确认新密码">
            <el-input :prefix-icon="Lock" placeholder="请输入旧密码" maxlength="16"></el-input>
          </el-form-item>
          <div style="text-align: center">
            <el-button :icon="Switch" type="primary">修改密码</el-button>
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