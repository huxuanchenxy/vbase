<template>
  <div id="AsideMenu">

    <el-menu router :default-active="activeIndex" menu-trigger="click" background-color="#ffffff" text-color="#333"
             active-text-color="#1890ff" mode="horizontal">

      <el-menu-item index="/console">系统监控</el-menu-item>
      <el-menu-item index="/live">实时监控</el-menu-item>
      <el-menu-item index="/deviceList">下级国标设备</el-menu-item>
      <!-- <el-menu-item index="/map">电子地图</el-menu-item> -->
      <el-menu-item index="/pushVideoList">推流列表</el-menu-item>
      <el-menu-item index="/streamProxyList">拉流代理</el-menu-item>
      <!-- <el-menu-item index="/cloudRecord">云端录像</el-menu-item> -->
      <el-menu-item index="/mediaServerManger">流媒体</el-menu-item>
      <el-menu-item index="/parentPlatformList/15/1">国标级联</el-menu-item>
      <!-- <el-menu-item v-if="editUser" index="/userManager">用户管理</el-menu-item> -->
      <el-menu-item index="/userManager">用户管理</el-menu-item>
      <el-menu-item index="/platformInfo">本级平台信息</el-menu-item>

    </el-menu>
  </div>
</template>

<script>

export default {
  name: "AsideMenu",
  components: {Notification},
  data() {
    return {
      alarmNotify: false,
      sseSource: null,
      activeIndex: this.$route.path,
      editUser: this.$cookies.get("session").roleId==1
    };
  },
  created() {
    console.log(this.$cookies.get("session"))
    if (this.$route.path.startsWith("/channelList")) {
      this.activeIndex = "/deviceList"
    }
  },
  mounted() {
    window.addEventListener('beforeunload', e => this.beforeunloadHandler(e))
    // window.addEventListener('unload', e => this.unloadHandler(e))
    this.alarmNotify = this.getAlarmSwitchStatus() === "true";
    this.sseControl();
  },
  methods: {
    
    openDoc() {
      console.log(process.env.BASE_API)
      window.open(!!process.env.BASE_API ? process.env.BASE_API + "/doc.html" : "/doc.html")
    },
    beforeunloadHandler() {
      this.sseSource.close();
    },
    alarmNotifyChannge() {
      this.setAlarmSwitchStatus()
      this.sseControl()
    },
    sseControl() {
      let that = this;
      if (this.alarmNotify) {
        console.log("申请SSE推送API调用，浏览器ID: " + this.$browserId);
        this.sseSource = new EventSource('/api/emit?browserId=' + this.$browserId);
        this.sseSource.addEventListener('message', function (evt) {
          that.$notify({
            title: '收到报警信息',
            dangerouslyUseHTMLString: true,
            message: evt.data,
            type: 'warning'
          });
          console.log("收到信息：" + evt.data);
        });
        this.sseSource.addEventListener('open', function (e) {
          console.log("SSE连接打开.");
        }, false);
        this.sseSource.addEventListener('error', function (e) {
          if (e.target.readyState == EventSource.CLOSED) {
            console.log("SSE连接关闭");
          } else {
            console.log(e.target.readyState);
          }
        }, false);
      } else {
        if (this.sseSource != null) {
          this.sseSource.removeEventListener('open', null);
          this.sseSource.removeEventListener('message', null);
          this.sseSource.removeEventListener('error', null);
          this.sseSource.close();
        }

      }
    },
    getAlarmSwitchStatus() {
      if (localStorage.getItem("alarmSwitchStatus") == null) {
        localStorage.setItem("alarmSwitchStatus", false);
      }
      return localStorage.getItem("alarmSwitchStatus");
    },
    setAlarmSwitchStatus() {
      localStorage.setItem("alarmSwitchStatus", this.alarmNotify);
    }
  },
  destroyed() {
    window.removeEventListener('beforeunload', e => this.beforeunloadHandler(e))
    if (this.sseSource != null) {
      this.sseSource.removeEventListener('open', null);
      this.sseSource.removeEventListener('message', null);
      this.sseSource.removeEventListener('error', null);
      this.sseSource.close();
    }
  },

}

</script>
<style>
#AsideMenu{
  width:124px;
  height: 100%;
  position:fixed;
  background: #ffffff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.08);
  z-index: 5;
  overflow: hidden;
}
#AsideMenu .el-menu-item{
  width: 100%;
}
#AsideMenu .el-switch__label {
  color: #333 ;
}
.el-menu--popup .el-menu-item .el-switch .el-switch__label {
  color: #333 !important;
}
#AsideMenu .el-switch__label.is-active{
  color: #409EFF;
}
#AsideMenu .el-menu-item.is-active {
  color: #fff!important;
  background-color: #1890ff!important;
}
</style>
