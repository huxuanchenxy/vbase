<template>
  <div id="ConsoleNodeLoad" style="width: 100%; height: 100%; text-align: center">
    <ve-histogram ref="consoleNodeLoad" :data="chartData" :extend="extend"  :settings="chartSettings" width="100%" height="100%" :legend-visible="true"></ve-histogram>
  </div>
</template>

<script>
import moment from "moment/moment";
export default {
  name: 'ConsoleNodeLoad',
  data() {
    return {
      chartData: {
        columns: ['id', 'push', 'proxy', 'gbReceive', 'gbSend'],
        rows: []
      },
      chartSettings: {
        labelMap: {
          'push': '直播推流',
          'proxy': '拉流代理',
          'gbReceive': '国标收流',
          'gbSend': '国标推流',
        },
        label:{
          color:"#ffffff"
        },
      },
      extend: {
        title: {
          show: true,
          text: "节点负载",
          left: "center",
          top: 20,
          textStyle:{
            color:'#ffffff'
          }
        },
        legend: {
          left: "center",
          bottom: "15px",
          textStyle:{
            color:'#ffffff'
          }
        },
        label: {
          show: true,
          position: "top"
        },
        textStyle:{
          color:'#ffffff'
        }
      }
    };
  },
  mounted() {
    this.$nextTick(_ => {
      setTimeout(()=>{
        this.$refs.consoleNodeLoad.echarts.resize()
      }, 100)
    })
  },
  destroyed() {
  },
  methods: {
    setData: function(data) {
      this.chartData.rows = data;
    }
  }
};
</script>
