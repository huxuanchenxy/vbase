<template>
  <div id="console" style="width: 100%">
    <div class="page-header">
      <div class="page-title">系统监控</div>
      <!-- <div class="page-header-btn">
        <el-button icon="el-icon-info" size="mini" style="margin-right: 1rem;" type="primary" @click="showInfo">平台信息
        </el-button>
      </div> -->
    </div>
    <el-row style="width: 50%; float: left;">
      <!-- <el-col :xl="{ span: 8 }" :lg="{ span: 8 }" :md="{ span: 12 }" :sm="{ span: 12 }" :xs="{ span: 24 }" >
        <div class="control-cell" id="ThreadsLoad" >
          <div style="width:100%; height:100%; ">
            <consoleCPU ref="consoleCPU"></consoleCPU>
          </div>
        </div>
      </el-col> -->
      <el-col :xl="{ span: 24 }" :lg="{ span: 24 }" :md="{ span: 24 }" :sm="{ span: 24 }" :xs="{ span: 24 }">
        <div class="control-cell" id="WorkThreadsLoad">
          <div
            style="width:100%; height:100%; background:#313238;color:white;border-radius:4px;box-shadow:0px 0px 3px grey">
            <consoleResource ref="consoleResource"></consoleResource>
          </div>
        </div>
      </el-col>
      <!-- <el-col :xl="{ span: 8 }" :lg="{ span: 8 }" :md="{ span: 12 }" :sm="{ span: 12 }" :xs="{ span: 24 }" >
        <div class="control-cell" id="WorkThreadsLoad" >
          <div style="width:100%; height:100%; ">
            <consoleNet ref="consoleNet"></consoleNet>
          </div>
        </div>
      </el-col> -->
      <!-- <el-col :xl="{ span: 8 }" :lg="{ span: 8 }" :md="{ span: 12 }" :sm="{ span: 12 }" :xs="{ span: 24 }" >
        <div class="control-cell" id="WorkThreadsLoad" >
          <div style="width:100%; height:100%; ">

            <consoleMem ref="consoleMem"></consoleMem>
          </div>
        </div>
      </el-col> -->
      <el-col :xl="{ span: 24 }" :lg="{ span: 24 }" :md="{ span: 24 }" :sm="{ span: 24 }" :xs="{ span: 24 }">
        <div class="control-cell" id="WorkThreadsLoad">
          <div
            style="width:100%; height:100%; background:#313238;color:white;border-radius:4px;box-shadow:0px 0px 3px grey">
            <consoleNodeLoad ref="consoleNodeLoad"></consoleNodeLoad>
          </div>
        </div>
      </el-col>
      <!-- <el-col :xl="{ span: 8 }" :lg="{ span: 8 }" :md="{ span: 12 }" :sm="{ span: 12 }" :xs="{ span: 24 }" >
        <div class="control-cell" id="WorkThreadsLoad" >
          <div style="width:100%; height:100%; ">
            <consoleDisk ref="consoleDisk"></consoleDisk>
          </div>
        </div>
      </el-col> -->
    </el-row>
    <el-row style="width: 50%; float: left;">
      <el-col :xl="{ span: 24 }" :lg="{ span: 24 }" :md="{ span: 24 }" :sm="{ span: 24 }" :xs="{ span: 24 }">
        <div class="control-cell" id="MonitorServer">
          <div
            style="width:100%; height:100%; background:#313238;color:white;border-radius:4px;box-shadow:0px 0px 3px grey">
            <el-table :data="serverList" style="width: 100%;font-size: 12px;">
              <el-table-column prop="ip" label="IP" min-width="100">
              </el-table-column>
              <el-table-column prop="type" label="服务器类型" min-width="120">
              </el-table-column>
              <el-table-column prop="opsLink" label="运维信息" min-width="100">
                <template v-slot:default="scope">
                  <el-button size="mini" style="margin-right: 1rem;" type="primary"
                    @click="openOpsLink(scope.row.opsLink)">
                    运维链接
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-col>
    </el-row>
    <!-- <configInfo ref="configInfo"></configInfo> -->
  </div>
</template>

<script>
import uiHeader from '../layout/UiHeader.vue'
// import consoleCPU from './console/ConsoleCPU.vue'
// import consoleMem from './console/ConsoleMEM.vue'
// import consoleNet from './console/ConsoleNet.vue'
import consoleNodeLoad from './console/ConsoleNodeLoad.vue'
// import consoleDisk from './console/ConsoleDisk.vue'
import consoleResource from './console/ConsoleResource.vue'
import configInfo from './dialog/configInfo.vue'

import echarts from 'echarts';

export default {
  name: 'app',
  components: {
    echarts,
    uiHeader,
    // consoleCPU,
    // consoleMem,
    // consoleNet,
    consoleNodeLoad,
    // consoleDisk,
    consoleResource,
    configInfo,
  },
  data() {
    return {
      timer: null,
      serverList: []
    };
  },
  created() {
    // this.getSystemInfo();
    this.getLoad();
    this.getResourceInfo();
    this.loopForSystemInfo();
    this.getAllMonitorServer();

  },
  destroyed() {
    if (this.timer != null) {
      window.clearTimeout(this.timer);
    }
  },
  unmounted() {
    if (this.timer != null) {
      window.clearTimeout(this.timer);
    }
  },
  methods: {
    loopForSystemInfo: function () {
      if (this.timer != null) {
        window.clearTimeout(this.timer);
      }
      this.timer = setTimeout(() => {
        // this.getSystemInfo();
        this.getLoad();
        this.timer = null;
        this.loopForSystemInfo()
        this.getResourceInfo()
      }, 5000)
    },
    getAllMonitorServer: function () {
      this.$axios({
        method: 'get',
        url: `/api/server/monitor_server`,
      }).then((res) => {
        if (res.data.code === 0) {
          this.serverList = res.data.data;
        }
      }).catch((error) => {
      });
    },
    getSystemInfo: function () {
      this.$axios({
        method: 'get',
        url: `/api/server/system/info`,
      }).then((res) => {
        if (res.data.code === 0) {
          this.$refs.consoleCPU.setData(res.data.data.cpu)
          this.$refs.consoleMem.setData(res.data.data.mem)
          this.$refs.consoleNet.setData(res.data.data.net, res.data.data.netTotal)
          this.$refs.consoleDisk.setData(res.data.data.disk)
        }
      }).catch((error) => {
      });
    },
    getLoad: function () {
      this.$axios({
        method: 'get',
        url: `/api/server/media_server/load`,
      }).then((res) => {
        if (res.data.code === 0) {
          if (res.data.data[0].gbReceive >= 40) {
            res.data.data[0].gbReceive = 2 * res.data.data[0].gbReceive;
          }
          console.log("gbReceive:" +res.data.data[0].gbReceive)
          this.$refs.consoleNodeLoad.setData(res.data.data)
        }
      }).catch((error) => {
      });
    },
    getResourceInfo: function () {
      this.$axios({
        method: 'get',
        url: `/api/server/resource/info`,
      }).then((res) => {
        if (res.data.code === 0) {
          this.$refs.consoleResource.setData(res.data.data)
        }
      }).catch((error) => {
      });
    },
    showInfo: function () {

      this.$axios({
        method: 'get',
        url: `/api/server/system/configInfo`,
      }).then((res) => {
        console.log(res)
        if (res.data.code === 0) {
          this.$refs.configInfo.openDialog(res.data.data)
        }
      }).catch((error) => {
      });
    },
    openOpsLink: function (url) {
      window.open(url, '_blank');
    }
  }
};
</script>

<style>
#console {
  padding-left: 124px;
  overflow: hidden;
  height: 100%;
}

.control-cell {
  padding-top: 10px;
  padding-left: 5px;
  padding-right: 10px;
  height: 360px;
}

.el-table tr {
  background: #313238;
  color: white;
}

.el-table th.el-table__cell {
  background-color: #313238;
}

.el-table--enable-row-hover .el-table__body tr:hover>td.el-table__cell {
  background-color: #222327;
}
</style>
