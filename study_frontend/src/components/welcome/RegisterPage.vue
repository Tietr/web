<script setup>
import {Lock, User, Message, ChatLineRound} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {reactive,ref} from "vue";

const form = reactive({
  username: "",
  password: "",
  password_repeat: "",
  email:"",
  code:""
})
const validName = (rule,value,callback)=>{
  if (value===''){
    callback(new Error("请输入用户名"));
  }else if(!/[\u4e00-\u9fa5a-zA-Z0-9]+/.test(value)){
    callback(new Error("用户名不能包含特殊字符"))
  }else if(2 > (String)(value).length || 8 < (String)(value).length) {
    callback(new Error("用户名长度应该在2个到8个字符之间"))
  }else {
    callback()
  }
}
const validPassword = (rule,value,callback)=>{
  if (value===''){
    callback(new Error("请输入密码"))
  }else if(!/^[^\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error("密码不能包含中文字符"))
  }else if(6 > (String)(value).length || 20 < (String)(value).length) {
    callback(new Error("密码长度应该在6到20个字符之间"))
  }else{
    callback()
  }
}
const validPasswordCheck = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error("两次密码不一致"))
  } else {
    callback()
  }
}
const isEmailValid = ref(false)
const onValidate = (prop,isValid) => {
  if(prop === 'email'){
    isEmailValid.value = isValid;
  }
}

const rules = {
  username: [
    { validator: validName, trigger:['blur','change']}
  ],
  password: [
    { validator:validPassword, trigger:['blur','change']},
  ],
  password_repeat: [
    {validator:validPasswordCheck, trigger:['blur','change'] },
  ],
  email: [
    {required: true, message: '请输入邮箱地址', trigger:'blur',},
    {type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'],},
  ],
  code:[

  ]
}



</script>

<template>
  <div style="text-align: center; margin:20vh 40px; ">
    <div style="margin: 50px;">
      <div style="font-size: 25px; font-weight: bold">注册新用户</div>
      <div style="font-size: 14px;color: grey">欢迎注册，请在下方填写您的相关信息</div>
    </div>
    <div style="margin-top: 50px;">
      <el-form :model="form" :rules="rules" @validate="onValidate">
        <el-form-item prop="username">
          <el-input v-model="form.username" type="text" placeholder="用户名">
            <template #prefix>
              <el-icon ><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="输入密码" >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" type="password" placeholder="重复输入密码" >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" type="text" placeholder="电子邮件">
            <template #prefix>
              <el-icon><message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row :gutter="7">
            <el-col :span="16" >
              <el-input v-model="form.code" type="text" placeholder="请输入验证码">
                <template #prefix>
                  <el-icon><ChatLineRound /></el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="8">
              <el-button type="primary" :disabled="!isEmailValid" >获取验证码</el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
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