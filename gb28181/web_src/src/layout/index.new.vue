<template>
  <el-container style="height: 100%">
    <el-header>
      <ui-header-2/>
    </el-header>
    <el-main>
      <el-container>
        <aside-menu></aside-menu>
        <transition name="fade">
          <router-view></router-view>
        </transition>
      </el-container>
    </el-main>
  </el-container>
</template>

<script>
import uiHeader2 from "./UiHeader2.vue";
import asideMenu from "./AsideMenu.vue";

export default {
  name: "index",
  components: {
    uiHeader2,
    asideMenu
  },
}
</script>
<style>
body{
  font-family: sans-serif;
}
.el-header{
  padding:0;
}
.el-card__body, .el-main{
  padding:0;
}
/*定义标题栏*/
.page-header {
  margin-bottom: 1rem;
  padding: 0.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color:white;
}

.page-title {
  font-weight: bold;
  text-align: left;
}

.page-header-btn {
  text-align: right;
}
</style>
<style scoped>
.fade-enter {
  visibility: hidden;
  opacity: 0;
}

.fade-leave-to {
  display: none;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity .5s ease;
}

.fade-enter-to,
.fade-leave {
  visibility: visible;
  opacity: 1;
}
</style>
