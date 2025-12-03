<template>
  <div id="platformInfo" style="width: 100%">
    <div class="page-header">
      <div class="page-title">本级平台信息</div>
    </div>
    <div class="page-body">
      <div id="shared" style="text-align: right; margin-top: 1rem">

        <el-form ref="platform1" label-width="160px">
          <el-row :gutter="24">
            <el-col :span="8">
                <el-form-item label="本机IP">
                  <el-input v-model="config.ip"></el-input>
                </el-form-item>
                <el-form-item label="本机端口">
                  <el-input v-model="config.port"></el-input>
                </el-form-item>
                <el-form-item label="登录密码">
                  <el-input v-model="config.password"></el-input>
                </el-form-item>
                <el-form-item label="国标编码">
                  <el-input v-model="config.id"></el-input>
                </el-form-item>
                <el-form-item label="域">
                  <el-input v-model="config.domain"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="24">
              <el-form-item>
                <el-button type="primary" style="float:left;width:160px" @click="restart()">保存并重启</el-button>
              </el-form-item>
            </el-col>
          </el-row>

        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'app',
  components: {
  },
  data() {
    return {
      config: {
        ip: null,
        port: null,
        password: null,
        id: null,
        domain: null
      }
    }
  },
  computed: {
  },
  created() {
    this.$axios({
      method: 'get',
      url: `/api/server/system/configInfo`,
    }).then((res)=> {
      if (res.data.code === 0) {
        var sip = res.data.data.sip;
        this.config.ip = sip.ip;
        this.config.port = sip.port;
        this.config.password = sip.password;
        this.config.id = sip.id;
        this.config.domain = sip.domain;
      }
    });
  },
  destroyed() {
  },
  methods: {
    restart: function() {
      this.$confirm('是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios({
          method: 'post',
          url: `/api/server/save-and-restart`,
          data: {
            ip: this.config.ip,
            port: this.config.port,
            password: this.config.password,
            id: this.config.id,
            domain: this.config.domain
          }
        }).then(() => {
          this.$message({
            showClose: true,
            message: '重启成功，请稍后刷新页面',
            type: 'success'
          });
        });
      });
    }
  }
};
</script>
<style>
#platformInfo{
  padding-left: 124px;
  overflow: hidden;
}
.subscribe-on{
  color: #409EFF;
  font-size: 18px;
}
.subscribe-off{
  color: #afafb3;
  font-size: 18px;
}
.page-body{
  margin-top: 50px;
}

.el-form-item {
  color: #FFFFFF;
}
</style>
