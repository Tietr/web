<script setup>
import {ChatLineRound, Lock, Message, User} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {post} from "@/net/index.js";
import {ElMessage} from "element-plus";

const form = reactive({
  password: "",
  password_repeat: "",
  email:"",
  code:""
})
const isEmailValid = ref(false)
const coldTime = ref(0)
const active =ref(0)

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
const rules = {
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
    {required: true, message: '请输入获取到的验证码', trigger:'blur',},
    { min: 6, max: 6, message: '请输入正确的验证码', trigger: ['blur','change'] },
  ]
}
const validateEmail=()=>{
  post("api/auth/valid-email",{
    email: form.email,
  },(message)=>{
    coldTime.value = 60
    setInterval(()=>coldTime.value--,1000)
    ElMessage.success(message)
  })
}
const onValidate = (prop,isValid) => {
  if(prop === 'email'){
    isEmailValid.value = isValid;
  }
}
</script>

<template>
  <div style="margin:30px 20px ">
    <el-steps style="max-width: 600px" :active="active" finish-status="success">
      <el-step title="验证邮箱" />
      <el-step title="重置密码" />
    </el-steps>
  </div>
  <Transition name="el-zoom-in-center" mode="out-in" >
    <div style="text-align: center; margin:20vh 20px; height: 100%" v-if="active === 0">
      <div style="margin: 50px;">
        <div style="font-size: 25px; font-weight: bold">重置密码</div>
        <div style="font-size: 14px;color: grey">请输入需要重置的电子邮件地址</div>
      </div>
      <div style="margin-top: 50px;">
        <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef" >
          <el-form-item prop="email">
            <el-input v-model="form.email" type="text" placeholder="电子邮件">
              <template #prefix>
                <el-icon><message /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code">
            <el-row :gutter="20">
              <el-col :span="15" >
                <el-input v-model="form.code" type="text" placeholder="请输入验证码" maxlength="6">
                  <template #prefix>
                    <el-icon><ChatLineRound /></el-icon>
                  </template>
                </el-input>
              </el-col>
              <el-col :span="8">
                <el-button style="width: 124px" type="primary" :disabled="!isEmailValid || coldTime > 0" @click="validateEmail" >{{ coldTime > 0 ?"请等待"+ coldTime +"秒":"获取验证码" }}</el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </div>
      <div style="margin-top: 70px;">
        <el-button @click="active = 1" style="width: 270px" type="danger">开始重置密码</el-button>
      </div>
    </div>
  </Transition>
  <Transition name="el-zoom-in-center" mode="out-in">
    <div style="text-align: center; margin:20vh 20px;  height: 100%" v-if="active === 1">
      <div style="margin: 50px;">
        <div style="font-size: 25px; font-weight: bold">重置密码</div>
        <div style="font-size: 14px;color: grey">请填写您的新密码，务必牢记</div>
      </div>
      <div style="margin-top: 50px;">
        <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef" >
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="输入密码" maxlength="20">
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password_repeat">
            <el-input v-model="form.password_repeat" type="password" placeholder="重复输入密码" maxlength="20">
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <div style="margin-top: 70px;">
        <el-button @click="active = 1" style="width: 270px" type="danger">立即重置密码</el-button>
      </div>
    </div>
  </Transition>


</template>

<style scoped>

</style>