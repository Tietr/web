<script setup>

</script>

<template>
  <div style="width:100vw;height:100vh;overflow:hidden;display: flex">
    <div style="flex: 1;background-color: aquamarine">
      <el-image
          style="width:100%;height:100%;"
          fit="cover"
          src="https://q2.itc.cn/q_70/images03/20240127/12b80fe65bfe469a946f2f13a5a22d38.jpeg"/>
    </div>
    <div class="welcome-title">


      <div>个人学习开发项目模版</div>
    </div>
    <div style="width: 400px;background-color: white" >
      <Transition name="el-zoom-in-center">
        <router-view/>
      </Transition>
    </div>
  </div>

</template>

<style scoped>
.welcome-title {
  position: absolute;
  bottom:30px;
  left: 30px;
  font-size: 20px;
  font-weight: bold;
  color: #000e36;
  text-shadow: 0 0 10px #322923;
}

</style>