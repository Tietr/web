<script setup>
import {Lock, User, Message, ChatLineRound} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {reactive} from "vue";
const form = reactive({
  username: "",
  password: "",
  password_repeat: "",
  email:"",
  code:""
})
</script>

<template>
  <div style="text-align: center; margin:20vh 40px; ">
    <div style="margin: 50px;">
      <div style="font-size: 25px; font-weight: bold">注册新用户</div>
      <div style="font-size: 14px;color: grey">欢迎注册，请在下方填写您的相关信息</div>
    </div>
    <div style="margin-top: 50px;">
      <el-input v-model="form.username" type="text" placeholder="用户名">
        <template #prefix>
          <el-icon ><User /></el-icon>
        </template>
      </el-input>
      <el-input v-model="form.password" type="password" placeholder="输入密码" style="margin-top: 10px">
        <template #prefix>
          <el-icon><Lock /></el-icon>
        </template>
      </el-input>
      <el-input v-model="form.password_repeat" type="password" placeholder="重复输入密码" style="margin-top: 10px">
        <template #prefix>
          <el-icon><Lock /></el-icon>
        </template>
      </el-input>
      <el-input v-model="form.email" type="text" placeholder="电子邮件" style="margin-top: 10px">
        <template #prefix>
          <el-icon><message /></el-icon>
        </template>
      </el-input>
      <div style="margin-top: 10px; ">
        <el-row :gutter="7">
          <el-col :span="16" >
            <el-input v-model="form.code" type="text" placeholder="请输入验证码">
              <template #prefix>
                <el-icon><ChatLineRound /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="8">
            <el-button type="primary" >获取验证码</el-button>
          </el-col>
        </el-row>
      </div>
      <div style="margin-top: 40px; ">
        <el-button style="width: 270px" type="success">立即注册</el-button>
      </div>
      <div style="margin-top: 20px">
        <span style="font-size: 12px;color: grey;">已有账号？</span>
        <el-link type="primary" style="translate: 0 -2px" @click="router.push('/')">立即登录</el-link>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>