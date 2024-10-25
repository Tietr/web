<script setup>
import {get} from "@/net/index.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";
import {useStore} from "@/stores/index.js";
import {Film, Headset, House, UserFilled} from "@element-plus/icons-vue";

const store = useStore()
const logout =()=>{
    get('/api/auth/logout',()=>{
      ElMessage.success("退出登录");
      store.auth.user = null;
      router.push("/");
    },(message)=>{
      ElMessage.error(message);
    })
  }



</script>

<template>
  <el-container style="height: 100vh; border: 1px solid #eee;">
    <!-- 侧边栏 -->
    <el-aside width="250px" style="background-color: #333; color: white; display: flex; flex-direction: column; justify-content: space-between;">
      <!-- 上部内容（标题和菜单） -->
      <div >
        <div style="text-align: center; padding: 10px; font-size: 40px; ">
          {{store.auth.user.username}}
        </div>
        <div style="font-size: 24px;">
          <el-menu
              default-active="1"
              background-color="#333"
              text-color="#fff"
              active-text-color="#ffd04b"
              style="border-right: 1px; text-align: center;"
          >
            <!-- 使菜单项的内容居中 -->
            <el-menu-item index="1">
              <el-icon><House /></el-icon>
              <span>Home</span>
            </el-menu-item>
            <el-menu-item index="2">
              <el-icon><Film /></el-icon>
              <span>Movies</span>
            </el-menu-item>
            <el-menu-item index="3">
              <el-icon><Headset /></el-icon>
              <span>Music</span>
            </el-menu-item>
          </el-menu>
        </div>
      </div>

      <!-- 按钮居中靠底部 -->
      <div style="padding: 20px; text-align: center;" >
        <el-button type="danger" @click="logout">logout</el-button>
      </div>
    </el-aside>

    <!-- 主内容部分 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header style="background-color: #b3c0d1; color: #333; text-align: center; line-height: 60px;">
        Management Dashboard
      </el-header>

      <!-- 主内容区 -->
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>


<style scoped>

</style>