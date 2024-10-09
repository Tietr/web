<script setup>
// const url = ''
  import {User,Lock} from  '@element-plus/icons-vue'
</script>

<template>
<!--白板页面成功-->
<!--  布局-->
  <div style="width:100vw;height:100vh;overflow:hidden;display: flex">
    <div style="flex: 1;background-color: aquamarine">
      <el-image
          style="width:100%;height:100%;"
          fit="cover"
          src="https://q2.itc.cn/q_70/images03/20240127/12b80fe65bfe469a946f2f13a5a22d38.jpeg"/>
    </div>
    <div style="width: 400px;background-color: white" >
<!--      400px 可以改成35vw-->
      <div style="text-align: center;margin:0 40px ">
        <div style="text-align: center; margin-top: 150px; ">
<!--          150px 可以改成30vh-->
            <div style="font-size: 25px; font-weight: bold">登录</div>
            <div style="font-size: 14px;color: grey">在进入系统前请先完成登录操作</div>
        </div>
        <div style="margin-top: 50px; ">
          <el-input type="text" placeholder="用户名或邮箱">
            <template #prefix>
              <el-icon ><User /></el-icon>
            </template>
          </el-input>
          <el-input type="text" style="margin-top: 10px" placeholder="密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </div>
        <el-row style="margin-top: 5px;">
          <el-col :span="12" style="text-align: left;">
<!--            <el-checkbox label="记住我" value="Value 1" />-->
            <el-checkbox v-model="checked1" label="记住我"/>
          </el-col>
          <el-col :span="12" style="text-align: right;">
            <el-link >忘记密码？</el-link>
          </el-col>
        </el-row>
        <div style="margin-top: 20px; ">
          <el-button style="width: 270px" type="success" plain >立即登录</el-button>
        </div>
        <el-divider>
          <span style="color: grey;font-size: 14px">没有账号</span>
        </el-divider>
        <div style="margin-top: 20px; ">
          <el-button style="width: 270px" type="primary" plain >注册账号</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
