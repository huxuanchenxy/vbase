<template>
  <!-- <span>账号：</span>
  <input
      type="input"
      autocomplete="on"
      ref="account"
      value="admin"
  />
  <span>密码：</span>
  <input
      type="password"
      autocomplete="on"
      ref="password"
      value="admin"
  />
  <button @click="login">登录</button> -->
  <div class="root">
    <div class="container-shell">
      <!-- <div class="option">
        <span>缓冲(秒):</span>
        <input
            style="width: 50px"
            type="number"
            ref="buffer"
            value="0.2"
            @change="changeBuffer"
        />
      </div> -->
      <div id="container" ref="container"></div>
      <div class="input">
        <div>地址：</div>
        <input
            type="input"
            autocomplete="on"
            ref="host"
            value="https://123.60.149.38:18080"
        />
        <div>设备ID：</div>
        <input
            type="input"
            autocomplete="on"
            ref="deviceId"
            value="34020000001110000001"
        />
        <div>通道ID：</div>
        <input
            type="input"
            autocomplete="on"
            ref="channelId"
            value="34020000001310000001"
        />
        <button v-if="!playing" @click="play">播放</button>
        <button v-else @click="destroy">关闭</button>
      </div>
      <div class="input" v-if="loaded" style="line-height: 30px">
        <div>流媒体地址：</div>
        <span>{{ playUrl }}</span>
        <!-- <button @click="destroy">停止</button>
        <button v-if="quieting" @click="cancelMute">取消静音</button>
        <template v-else>
          <button @click="mute">静音</button>
          音量
          <select v-model="volume" @change="volumeChange">
            <option value="1">100</option>
            <option value="0.75">75</option>
            <option value="0.5">50</option>
            <option value="0.25">25</option>
          </select>
        </template>
        <span>旋转</span>
        <select v-model="rotate" @change="rotateChange">
          <option value="0">0</option>
          <option value="90">90</option>
          <option value="270">270</option>
        </select>

        <button @click="fullscreen">全屏</button>
        <button @click="screenShot">截图</button>
        <div style="line-height: 30px">
          <input
              type="checkbox"
              ref="operateBtns"
              v-model="showOperateBtns"
              @change="restartPlay"
          /><span>操作按钮</span>
          <input
              type="checkbox"
              ref="operateBtns"
              v-model="showBandwidth"
              @change="restartPlay"
          /><span>网速</span>
          <span v-if="performance">性能：{{ performance }}</span>
        </div> -->
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: "Player",
  props: {},
  data() {
    return {
      jessibuca: null,
      version: '',
      wasm: false,
      vc: "ff",
      playing: false,
      quieting: true,
      loaded: false, // mute
      showOperateBtns: false,
      showBandwidth: false,
      err: "",
      speed: 0,
      performance: "",
      volume: 1,
      rotate: 0,
      useWCS: false,
      useMSE: true,
      useOffscreen: false,
      recording: false,
      recordType: 'webm',
      scale: 0,
      playUrl: ""
    };
  },
  mounted() {
    this.create();
    window.onerror = (msg) => (this.err = msg);
  },
  unmounted() {
    this.destroy();
  },
  methods: {
    create(options) {
      options = options || {};
      this.jessibuca = new window.Jessibuca(
          Object.assign(
              {
                container: this.$refs.container,
                videoBuffer: 0.2,//Number(this.$refs.buffer.value), // 缓存时长
                isResize: false,
                useWCS: this.useWCS,
                useMSE: this.useMSE,
                text: "",
                // background: "bg.jpg",
                loadingText: "加载中...",
                // hasAudio:false,
                debug: true,
                supportDblclickFullscreen: true,
                showBandwidth: this.showBandwidth, // 显示网速
                operateBtns: {
                  fullscreen: this.showOperateBtns,
                  screenshot: this.showOperateBtns,
                  play: this.showOperateBtns,
                  audio: this.showOperateBtns,
                },
                vod: this.vod,
                forceNoOffscreen: !this.useOffscreen,
                isNotMute: true,
                timeout: 10
              },
              options
          )
      );
      var _this = this;
      this.jessibuca.on("load", function () {
        console.log("on load");
      });
      this.jessibuca.on("log", function (msg) {
        console.log("on log", msg);
      });
      this.jessibuca.on("record", function (msg) {
        console.log("on record:", msg);
      });
      this.jessibuca.on("pause", function () {
        console.log("on pause");
        _this.playing = false;
      });
      this.jessibuca.on("play", function () {
        console.log("on play");
        _this.playing = true;
      });
      this.jessibuca.on("fullscreen", function (msg) {
        console.log("on fullscreen", msg);
      });
      this.jessibuca.on("mute", function (msg) {
        console.log("on mute", msg);
        _this.quieting = msg;
      });
      this.jessibuca.on("mute", function (msg) {
        console.log("on mute2", msg);
      });
      this.jessibuca.on("audioInfo", function (msg) {
        console.log("audioInfo", msg);
      });
      this.jessibuca.on("videoInfo", function (info) {
        console.log("videoInfo", info);
      });
      this.jessibuca.on("error", function (error) {
        console.log("error", error);
      });
      this.jessibuca.on("timeout", function () {
        console.log("timeout");
      });
      this.jessibuca.on('start', function () {
        console.log('frame start');
      })
      this.jessibuca.on("performance", function (performance) {
        var show = "卡顿";
        if (performance === 2) {
          show = "非常流畅";
        } else if (performance === 1) {
          show = "流畅";
        }
        _this.performance = show;
      });
      this.jessibuca.on('buffer', function (buffer) {
        console.log('buffer', buffer);
      })
      this.jessibuca.on('stats', function (stats) {
        console.log('stats', stats);
      })
      this.jessibuca.on('kBps', function (kBps) {
        console.log('kBps', kBps);
      });
      this.jessibuca.on("play", () => {
        this.playing = true;
        this.loaded = true;
        this.quieting = this.jessibuca.isMute();
      });
      this.jessibuca.on('recordingTimestamp', (ts) => {
        console.log('recordingTimestamp', ts);
      })
      // console.log(this.jessibuca);
    },
    play() {
      var that = this;
      var url = this.$refs.host.value + '/api/demo/play/start/' + this.$refs.deviceId.value + '/' + this.$refs.channelId.value;
      this.$axios({
        method: 'get',
        url: url
      }).then(function (res) {
        console.log(res);
        if (res.data.code === 0) {
          that.jessibuca.play(res.data.data.wss_flv);
          that.playUrl = res.data.data.wss_flv;
        } else {
          alert(res.data.msg);
        }
      }).catch(function (err) {
        console.log(err);
      });
    },
    // login() {
    //   var account = this.$refs.account.value;
    //   var password = this.$refs.password.value;
    //   this.$axios({
    //     method: 'get',
    //     url: `http://123.60.149.38:18080/api/user/login?username=${account}&password=${password}`
    //   }).then(function (res) {
    //     alert(res.data.msg);
    //   }).catch(function (err) {
    //     alert(err);
    //   });
    // },
    mute() {
      this.jessibuca.mute();
    },
    cancelMute() {
      this.jessibuca.cancelMute();
    },
    pause() {
      this.jessibuca.pause();
      this.playing = false;
      this.err = "";
      this.performance = "";
    },
    volumeChange() {
      this.jessibuca.setVolume(this.volume);
    },
    rotateChange() {
      this.jessibuca.setRotate(this.rotate);
    },
    destroy() {
      var that = this;
      var url = this.$refs.host.value + '/api/demo/play/stop/' + this.$refs.deviceId.value + '/' + this.$refs.channelId.value;
      this.$axios({
        method: 'get',
        url: url
      }).then(function (res) {
        console.log(res);
        if (res.data.code === 0) {
          if (that.jessibuca) {
            that.jessibuca.destroy();
          }
          that.create();
          that.playing = false;
          that.loaded = false;
          that.performance = "";
        } else {
          alert(res.data.msg);
        }
      }).catch(function (err) {
        alert(err);
        console.log(err);
      });
    },
    fullscreen() {
      this.jessibuca.setFullscreen(true);
    },
    clearView() {
      this.jessibuca.clearView();
    },
    startRecord() {
      const time = new Date().getTime();
      this.jessibuca.startRecord(time, this.recordType);
    },
    stopAndSaveRecord() {
      this.jessibuca.stopRecordAndSave();
    },
    screenShot() {
      this.jessibuca.screenshot();
    },
    // restartPlay(type) {
    //   if (type === 'mse') {
    //     this.useWCS = false;
    //     this.useOffscreen = false;
    //   } else if (type === 'wcs') {
    //     this.useMSE = false
    //   } else if (type === 'offscreen') {
    //     this.useMSE = false
    //   }
    //   this.destroy();
    //   setTimeout(() => {
    //     this.play();
    //   }, 100)
    // },
    changeBuffer() {
      this.jessibuca.setBufferTime(Number(this.$refs.buffer.value));
    },
    scaleChange() {
      this.jessibuca.setScaleMode(this.scale);
    },
  },
};
</script>
<style>
.root {
  display: flex;
  place-content: center;
  margin-top: 3rem;
}
.container-shell {
  position: relative;
  backdrop-filter: blur(5px);
  background: hsla(0, 0%, 50%, 0.5);
  padding: 30px 4px 10px 4px;
  /* border: 2px solid black; */
  width: auto;
  position: relative;
  border-radius: 5px;
  box-shadow: 0 10px 20px;
}
.container-shell-title {
  position: absolute;
  color: darkgray;
  top: 4px;
  left: 10px;
  text-shadow: 1px 1px black;
}
.tag-version {
}
#container {
  background: rgba(13, 14, 27, 0.7);
  width: 800px;
  height: 640px;
}
.input {
  display: flex;
  align-items: center;
  margin-top: 10px;
  color: white;
  place-content: stretch;
}
.input2 {
  bottom: 0px;
}
.input input[type='input'] {
  flex: auto;
}
.err {
  position: absolute;
  top: 40px;
  left: 10px;
  color: red;
}
.option {
  position: absolute;
  top: 4px;
  right: 10px;
  display: flex;
  place-content: center;
  font-size: 12px;
}
.option span {
  color: white;
}
.page {
  /* background: url(/bg.jpg); */
  background-repeat: no-repeat;
  background-position: top;
}
@media (max-width: 720px) {
  #container {
    width: 90vw;
    height: 52.7vw;
  }
}
</style>