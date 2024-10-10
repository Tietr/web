<script setup>
import {get} from "@/net/index.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const logout =()=>{
    get('/api/auth/logout',(Message)=>{
      ElMessage.success(Message);
      router.push("/");
    })
  }

</script>

<template>
  <div>
    欢迎来到本项目的主页面
  </div>
  <el-button @click="logout()" type="danger" plain>退出登录</el-button>
</template>

<style scoped>

</style>