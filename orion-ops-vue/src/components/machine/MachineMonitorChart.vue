<template>
  <div class="machine-monitor-chart-container">
    <div class="machine-monitor-chart-wrapper">
      <!-- 监控头部 -->
      <div class="machine-monitor-header">
        <!-- 时间区间 -->
        <div class="monitor-range-wrapper">
          <a-radio-group v-model="rangeType" @change="changeRangeType">
            <a-radio-button class="radius-0" value="a">
              实时
            </a-radio-button>
            <a-radio-button value="b">
              近24小时
            </a-radio-button>
            <a-radio-button value="c">
              近7天
            </a-radio-button>
            <a-radio-button class="radius-0" value="d">
              选择日期
            </a-radio-button>
          </a-radio-group>
        </div>
        <!-- 时间粒度 -->
        <div class="monitor-granularity-wrapper">
          <span class="normal-label granularity-label">
            时间粒度
          </span>
          <a-select v-model="granularityType" class="granularity-selector" @change="changeGranularityType">
            <a-select-option value="jack">
              三十秒
            </a-select-option>
            <a-select-option value="jack">
              一分钟
            </a-select-option>
            <a-select-option value="lucy">
              五分钟
            </a-select-option>
            <a-select-option value="Yiminghe">
              一小时
            </a-select-option>
            <a-select-option value="Yiminghe">
              一小时
            </a-select-option>
            <a-select-option value="Yiminghe">
              一天
            </a-select-option>
            <a-select-option value="Yiminghe">
              一周
            </a-select-option>
          </a-select>
        </div>
      </div>
      <!-- 监控主体 -->
      <a-divider class="m0"/>
      <div class="machine-monitor-body">
        <!-- CPU监控 -->
        <div class="chart-container">
          <!-- 监控类型 -->
          <div class="chart-monitor-label">
            CPU监控
          </div>
          <!-- 图表 -->
          <div class="chart-wrapper">
            <div class="chart-box">
              <!-- 监控类别 -->
              <div class="chart-desc-label">
                CPU使用率%
              </div>
              <!-- 监控表格 -->
              <a-spin class="metrics-chart-wrapper" :spinning="charts.cpu.loading">
                <div id="cpu-usage-chart" class="metrics-chart"/>
              </a-spin>
              <!-- 监控信息 -->
              <div class="chart-info">
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Max:
                  </span>
                  <span class="monitor-info-value">
                    {{ charts.cpu.usage.max }}%
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Min:
                  </span>
                  <span class="monitor-info-value">
                    {{ charts.cpu.usage.min }}%
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Avg:
                  </span>
                  <span class="monitor-info-value">
                   {{ charts.cpu.usage.avg }}%
                  </span>
                </div>
                <div class="monitor-action-wrapper">
                  展开
                </div>
              </div>
            </div>
          </div>
        </div>
        <a-divider class="m0"/>
        <!-- 内存监控 -->
        <div class="chart-container">
          <!-- label -->
          <div class="chart-monitor-label">
            内存监控
          </div>
          <!-- 图表 -->
          <div class="chart-wrapper">
            <div class="chart-box">
              <!-- 监控类别 -->
              <div class="chart-desc-label">
                内存使用率%
              </div>
              <!-- 监控表格 -->
              <a-spin class="metrics-chart-wrapper" :spinning="charts.memory.loading">
                <div id="memory-usage-chart" class="metrics-chart"/>
              </a-spin>
              <!-- 监控信息 -->
              <div class="chart-info">
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Max:
                  </span>
                  <span class="monitor-info-value">
                    {{ charts.memory.usage.max }}%
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Min:
                  </span>
                  <span class="monitor-info-value">
                     {{ charts.memory.usage.min }}%
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Avg:
                  </span>
                  <span class="monitor-info-value">
                   {{ charts.memory.usage.avg }}%
                  </span>
                </div>
                <div class="monitor-action-wrapper">
                  展开
                </div>
              </div>
            </div>
            <a-divider class="my8"/>
            <div class="chart-box">
              <!-- 监控类别 -->
              <div class="chart-desc-label">
                内存使用量MB
              </div>
              <!-- 监控表格 -->
              <a-spin class="metrics-chart-wrapper" :spinning="charts.memory.loading">
                <div id="memory-size-chart" class="metrics-chart"/>
              </a-spin>
              <!-- 监控信息 -->
              <div class="chart-info">
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Max:
                  </span>
                  <span class="monitor-info-value">
                    {{ charts.memory.size.max }} MB
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Min:
                  </span>
                  <span class="monitor-info-value">
                    {{ charts.memory.size.min }} MB
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Avg:
                  </span>
                  <span class="monitor-info-value">
                   {{ charts.memory.size.avg }} MB
                  </span>
                </div>
                <div class="monitor-action-wrapper">
                  展开
                </div>
              </div>
            </div>
          </div>
        </div>
        <a-divider class="m0"/>
        <!-- 网络监控 -->
        <div class="chart-container">
          <!-- label -->
          <div class="chart-monitor-label">
            网络带宽监控
          </div>
          <!-- 图表 -->
          <div class="chart-wrapper">
            <div class="chart-box">
              <!-- 监控类别 -->
              <div class="chart-desc-label">
                上行速率Mbps
              </div>
              <!-- 监控表格 -->
              <a-spin class="metrics-chart-wrapper" :spinning="charts.net.loading">
                <div id="net-sent-speed-chart" class="metrics-chart"/>
              </a-spin>
              <!-- 监控信息 -->
              <div class="chart-info">
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Max:
                  </span>
                  <span class="monitor-info-value">
                    {{ charts.net.sentSpeed.max }} Mbps
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Min:
                  </span>
                  <span class="monitor-info-value">
                     {{ charts.net.sentSpeed.min }} Mbps
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Avg:
                  </span>
                  <span class="monitor-info-value">
                   {{ charts.net.sentSpeed.avg }} Mbps
                  </span>
                </div>
                <div class="monitor-action-wrapper">
                  展开
                </div>
              </div>
            </div>
            <a-divider class="my8"/>
            <div class="chart-box">
              <!-- 监控类别 -->
              <div class="chart-desc-label">
                下行速率Mbps
              </div>
              <!-- 监控表格 -->
              <a-spin class="metrics-chart-wrapper" :spinning="charts.net.loading">
                <div id="net-recv-speed-chart" class="metrics-chart"/>
              </a-spin>
              <!-- 监控信息 -->
              <div class="chart-info">
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Max:
                  </span>
                  <span class="monitor-info-value">
                    {{ charts.net.recvSpeed.max }} Mbps
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Min:
                  </span>
                  <span class="monitor-info-value">
                     {{ charts.net.recvSpeed.min }} Mbps
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Avg:
                  </span>
                  <span class="monitor-info-value">
                   {{ charts.net.recvSpeed.avg }} Mbps
                  </span>
                </div>
                <div class="monitor-action-wrapper">
                  展开
                </div>
              </div>
            </div>
            <a-divider class="my8"/>
            <div class="chart-box">
              <!-- 监控类别 -->
              <div class="chart-desc-label">
                发送包数个/秒
              </div>
              <!-- 监控表格 -->
              <a-spin class="metrics-chart-wrapper" :spinning="charts.net.loading">
                <div id="net-sent-packet-chart" class="metrics-chart"/>
              </a-spin>
              <!-- 监控信息 -->
              <div class="chart-info">
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Max:
                  </span>
                  <span class="monitor-info-value">
                    {{ charts.net.sentPacket.max }} 个/秒
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Min:
                  </span>
                  <span class="monitor-info-value">
                     {{ charts.net.sentPacket.min }} 个/秒
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Avg:
                  </span>
                  <span class="monitor-info-value">
                   {{ charts.net.sentPacket.avg }} 个/秒
                  </span>
                </div>
                <div class="monitor-action-wrapper">
                  展开
                </div>
              </div>
            </div>
            <a-divider class="my8"/>
            <div class="chart-box">
              <!-- 监控类别 -->
              <div class="chart-desc-label">
                接收包数个/秒
              </div>
              <!-- 监控表格 -->
              <a-spin class="metrics-chart-wrapper" :spinning="charts.net.loading">
                <div id="net-recv-packet-chart" class="metrics-chart"/>
              </a-spin>
              <!-- 监控信息 -->
              <div class="chart-info">
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Max:
                  </span>
                  <span class="monitor-info-value">
                    {{ charts.net.recvPacket.max }} 个/秒
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Min:
                  </span>
                  <span class="monitor-info-value">
                     {{ charts.net.recvPacket.min }} 个/秒
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Avg:
                  </span>
                  <span class="monitor-info-value">
                   {{ charts.net.recvPacket.avg }} 个/秒
                  </span>
                </div>
                <div class="monitor-action-wrapper">
                  展开
                </div>
              </div>
            </div>
          </div>
        </div>
        <a-divider class="m0"/>
        <!-- 硬盘监控 -->
        <div class="chart-container">
          <!-- label -->
          <div class="chart-monitor-label">
            <span>网络带宽监控</span>
            <a-select class="disk-selector" v-model="diskSeq" placeholder="选择硬盘" @change="reloadDiskChart">
              <a-select-option v-for="disk of diskNames" :key="disk.seq" :value="disk.seq">
                <a-tooltip :title="disk.name" placement="topLeft">
                  {{ disk.name }}
                </a-tooltip>
              </a-select-option>
            </a-select>
          </div>
          <!-- 图表 -->
          <div class="chart-wrapper">
            <div class="chart-box">
              <!-- 监控类别 -->
              <div class="chart-desc-label">
                硬盘读流量KB/s
              </div>
              <!-- 监控表格 -->
              <a-spin class="metrics-chart-wrapper" :spinning="charts.disk.loading">
                <div id="disk-read-speed-chart" class="metrics-chart"/>
              </a-spin>
              <!-- 监控信息 -->
              <div class="chart-info">
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Max:
                  </span>
                  <span class="monitor-info-value">
                    {{ charts.disk.readSpeed.max }} KB/s
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Min:
                  </span>
                  <span class="monitor-info-value">
                     {{ charts.disk.readSpeed.min }} KB/s
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Avg:
                  </span>
                  <span class="monitor-info-value">
                   {{ charts.disk.readSpeed.avg }} KB/s
                  </span>
                </div>
                <div class="monitor-action-wrapper">
                  展开
                </div>
              </div>
            </div>
            <a-divider class="my8"/>
            <div class="chart-box">
              <!-- 监控类别 -->
              <div class="chart-desc-label">
                硬盘写流量KB/s
              </div>
              <!-- 监控表格 -->
              <a-spin class="metrics-chart-wrapper" :spinning="charts.disk.loading">
                <div id="disk-write-speed-chart" class="metrics-chart"/>
              </a-spin>
              <!-- 监控信息 -->
              <div class="chart-info">
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Max:
                  </span>
                  <span class="monitor-info-value">
                    {{ charts.disk.writeSpeed.max }} KB/s
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Min:
                  </span>
                  <span class="monitor-info-value">
                     {{ charts.disk.writeSpeed.min }} KB/s
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Avg:
                  </span>
                  <span class="monitor-info-value">
                   {{ charts.disk.writeSpeed.avg }} KB/s
                  </span>
                </div>
                <div class="monitor-action-wrapper">
                  展开
                </div>
              </div>
            </div>
            <a-divider class="my8"/>
            <div class="chart-box">
              <!-- 监控类别 -->
              <div class="chart-desc-label">
                硬盘读IOPS
              </div>
              <!-- 监控表格 -->
              <a-spin class="metrics-chart-wrapper" :spinning="charts.disk.loading">
                <div id="disk-read-count-chart" class="metrics-chart"/>
              </a-spin>
              <!-- 监控信息 -->
              <div class="chart-info">
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Max:
                  </span>
                  <span class="monitor-info-value">
                    {{ charts.disk.readCount.max }} 个/秒
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Min:
                  </span>
                  <span class="monitor-info-value">
                     {{ charts.disk.readCount.min }} 个/秒
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Avg:
                  </span>
                  <span class="monitor-info-value">
                   {{ charts.disk.readCount.avg }} 个/秒
                  </span>
                </div>
                <div class="monitor-action-wrapper">
                  展开
                </div>
              </div>
            </div>
            <a-divider class="my8"/>
            <div class="chart-box">
              <!-- 监控类别 -->
              <div class="chart-desc-label">
                硬盘写IOPS
              </div>
              <!-- 监控表格 -->
              <a-spin class="metrics-chart-wrapper" :spinning="charts.disk.loading">
                <div id="disk-write-count-chart" class="metrics-chart"/>
              </a-spin>
              <!-- 监控信息 -->
              <div class="chart-info">
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Max:
                  </span>
                  <span class="monitor-info-value">
                    {{ charts.disk.writeCount.max }} 个/秒
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Min:
                  </span>
                  <span class="monitor-info-value">
                     {{ charts.disk.writeCount.min }} 个/秒
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Avg:
                  </span>
                  <span class="monitor-info-value">
                   {{ charts.disk.writeCount.avg }} 个/秒
                  </span>
                </div>
                <div class="monitor-action-wrapper">
                  展开
                </div>
              </div>
            </div>
            <a-divider class="my8"/>
            <div class="chart-box">
              <!-- 监控类别 -->
              <div class="chart-desc-label">
                IO使用时间ms
              </div>
              <!-- 监控表格 -->
              <a-spin class="metrics-chart-wrapper" :spinning="charts.disk.loading">
                <div id="disk-usage-time-chart" class="metrics-chart"/>
              </a-spin>
              <!-- 监控信息 -->
              <div class="chart-info">
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Max:
                  </span>
                  <span class="monitor-info-value">
                    {{ charts.disk.usageTime.max }} ms
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Min:
                  </span>
                  <span class="monitor-info-value">
                     {{ charts.disk.usageTime.min }} ms
                  </span>
                </div>
                <div class="monitor-info-wrapper">
                  <span class="monitor-info-label">
                    Avg:
                  </span>
                  <span class="monitor-info-value">
                   {{ charts.disk.usageTime.avg }} ms
                  </span>
                </div>
                <div class="monitor-action-wrapper">
                  展开
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <a-divider class="m0"/>
    </div>
  </div>
</template>

<script>
import { timestampRender } from '@/lib/chart'

function initCharts() {
  return {
    cpu: {
      loading: false,
      usage: {
        chart: null,
        max: null,
        min: null,
        avg: null
      }
    },
    memory: {
      loading: false,
      usage: {
        chart: null,
        max: null,
        min: null,
        avg: null
      },
      size: {
        chart: null,
        max: null,
        min: null,
        avg: null
      }
    },
    net: {
      loading: false,
      sentSpeed: {
        chart: null,
        max: null,
        min: null,
        avg: null
      },
      recvSpeed: {
        chart: null,
        max: null,
        min: null,
        avg: null
      },
      sentPacket: {
        chart: null,
        max: null,
        min: null,
        avg: null
      },
      recvPacket: {
        chart: null,
        max: null,
        min: null,
        avg: null
      }
    },
    disk: {
      loading: false,
      readSpeed: {
        chart: null,
        max: null,
        min: null,
        avg: null
      },
      writeSpeed: {
        chart: null,
        max: null,
        min: null,
        avg: null
      },
      readCount: {
        chart: null,
        max: null,
        min: null,
        avg: null
      },
      writeCount: {
        chart: null,
        max: null,
        min: null,
        avg: null
      },
      usageTime: {
        chart: null,
        max: null,
        min: null,
        avg: null
      }
    }
  }
}

export default {
  name: 'MachineMonitorChart',
  props: {
    machineId: Number
  },
  data() {
    return {
      rangeType: null,
      granularityType: null,
      charts: initCharts(),
      diskNames: [],
      diskSeq: null
    }
  },
  methods: {
    changeRangeType(e) {
      this.rangeType = e.target.value
    },
    changeGranularityType(e) {
      this.granularityType = e.target.value
    },
    doRefresh() {
      this.renderChart(false)
    },
    async loadDisk() {
      const { data } = await this.$api.getMachineDiskName({
        machineId: this.machineId
      })
      this.diskNames = data
      this.diskSeq = data[0].seq
    },
    getParams() {
      const params = {
        machineId: this.machineId,
        granularity: 20,
        seq: this.diskSeq
      }
      params.endRange = ~~(Date.now() / 1000)
      params.startRange = params.endRange - 3600
      return params
    },
    renderChart(loading) {
      const params = this.getParams()
      // 渲染CPU
      this.renderCpuChart(loading, params)
      // 渲染内存
      this.renderMemoryChart(loading, params)
      // 渲染带宽
      this.renderNetChart(loading, params)
      // 渲染IO
      this.renderDiskChart(loading, params)
    },
    renderCpuChart(loading, params) {
      this.charts.cpu.loading = loading
      this.$api.getMachineMonitorCpuChart(params).then(({ data }) => {
        this.charts.cpu.loading = false
        this.charts.cpu.usage.max = data.usage.max
        this.charts.cpu.usage.min = data.usage.min
        this.charts.cpu.usage.avg = data.usage.avg
        this.$nextTick(() => {
          timestampRender('cpu-usage-chart', this.charts.cpu.usage, 'chart', false, text => {
            return parseFloat(text).toFixed(1)
          }, item => {
            item.name = 'CPU使用率'
            item.value = item.value + '%'
          }, data.usage.metrics)
        })
      }).catch(() => {
        this.charts.cpu.loading = false
      })
    },
    renderMemoryChart(loading, params) {
      this.charts.memory.loading = loading
      this.$api.getMachineMonitorMemoryChart(params).then(({ data }) => {
        this.charts.memory.loading = false
        this.charts.memory.usage.max = data.usage.max
        this.charts.memory.usage.min = data.usage.min
        this.charts.memory.usage.avg = data.usage.avg
        this.charts.memory.size.max = data.size.max
        this.charts.memory.size.min = data.size.min
        this.charts.memory.size.avg = data.size.avg
        this.$nextTick(() => {
          // 渲染内存使用率
          timestampRender('memory-usage-chart', this.charts.memory.usage, 'chart', false, text => {
            return parseFloat(text).toFixed(2)
          }, item => {
            item.name = '内存使用率'
            item.value = item.value + '%'
          }, data.usage.metrics)
          // 渲染内存使用量
          timestampRender('memory-size-chart', this.charts.memory.size, 'chart', false, text => {
            return text
          }, item => {
            item.name = '内存使用量'
            item.value = item.value + ' MB'
          }, data.size.metrics)
        })
      }).catch(() => {
        this.charts.memory.loading = false
      })
    },
    renderNetChart(loading, params) {
      this.charts.net.loading = loading
      this.$api.getMachineMonitorNetChart(params).then(({ data }) => {
        this.charts.net.loading = false
        this.charts.net.sentSpeed.max = data.sentSpeed.max
        this.charts.net.sentSpeed.min = data.sentSpeed.min
        this.charts.net.sentSpeed.avg = data.sentSpeed.avg
        this.charts.net.recvSpeed.max = data.recvSpeed.max
        this.charts.net.recvSpeed.min = data.recvSpeed.min
        this.charts.net.recvSpeed.avg = data.recvSpeed.avg
        this.charts.net.sentPacket.max = data.sentPacket.max
        this.charts.net.sentPacket.min = data.sentPacket.min
        this.charts.net.sentPacket.avg = data.sentPacket.avg
        this.charts.net.recvPacket.max = data.recvPacket.max
        this.charts.net.recvPacket.min = data.recvPacket.min
        this.charts.net.recvPacket.avg = data.recvPacket.avg
        this.$nextTick(() => {
          // 渲染上行速率
          timestampRender('net-sent-speed-chart', this.charts.net.sentSpeed, 'chart', false, text => {
            return parseFloat(text).toFixed(2)
          }, item => {
            item.name = '上行速率'
            item.value = item.value + ' Mbps'
          }, data.sentSpeed.metrics)
          // 渲染下行速率
          timestampRender('net-recv-speed-chart', this.charts.net.recvSpeed, 'chart', false, text => {
            return parseFloat(text).toFixed(2)
          }, item => {
            item.name = '下行速率'
            item.value = item.value + ' Mbps'
          }, data.recvSpeed.metrics)
          // 渲染发送包数
          timestampRender('net-sent-packet-chart', this.charts.net.sentPacket, 'chart', false, text => {
            return text
          }, item => {
            item.name = '发送包数'
            item.value = item.value + ' 个/秒'
          }, data.sentPacket.metrics)
          // 渲染接收包数
          timestampRender('net-recv-packet-chart', this.charts.net.recvPacket, 'chart', false, text => {
            return text
          }, item => {
            item.name = '接收包数'
            item.value = item.value + ' 个/秒'
          }, data.recvPacket.metrics)
        })
      }).catch(() => {
        this.charts.memory.loading = false
      })
    },
    renderDiskChart(loading, params) {
      this.charts.disk.loading = loading
      this.$api.getMachineMonitorDiskChart(params).then(({ data }) => {
        this.charts.disk.loading = false
        this.charts.disk.readSpeed.max = data.readSpeed.max
        this.charts.disk.readSpeed.min = data.readSpeed.min
        this.charts.disk.readSpeed.avg = data.readSpeed.avg
        this.charts.disk.writeSpeed.max = data.writeSpeed.max
        this.charts.disk.writeSpeed.min = data.writeSpeed.min
        this.charts.disk.writeSpeed.avg = data.writeSpeed.avg
        this.charts.disk.readCount.max = data.readCount.max
        this.charts.disk.readCount.min = data.readCount.min
        this.charts.disk.readCount.avg = data.readCount.avg
        this.charts.disk.writeCount.max = data.writeCount.max
        this.charts.disk.writeCount.min = data.writeCount.min
        this.charts.disk.writeCount.avg = data.writeCount.avg
        this.charts.disk.usageTime.max = data.usageTime.max
        this.charts.disk.usageTime.min = data.usageTime.min
        this.charts.disk.usageTime.avg = data.usageTime.avg
        this.$nextTick(() => {
          // 渲染硬盘读流量
          timestampRender('disk-read-speed-chart', this.charts.disk.readSpeed, 'chart', false, text => {
            return parseFloat(text).toFixed(2)
          }, item => {
            item.name = '硬盘读流量'
            item.value = item.value + ' KB/s'
          }, data.readSpeed.metrics)
          // 渲染硬盘写流量
          timestampRender('disk-write-speed-chart', this.charts.disk.writeSpeed, 'chart', false, text => {
            return parseFloat(text).toFixed(2)
          }, item => {
            item.name = '硬盘写流量'
            item.value = item.value + ' KB/s'
          }, data.writeSpeed.metrics)
          // 渲染硬盘读次数
          timestampRender('disk-read-count-chart', this.charts.disk.readCount, 'chart', false, text => {
            return text
          }, item => {
            item.name = '硬盘读次数'
            item.value = item.value + ' 个/s'
          }, data.readCount.metrics)
          // 渲染硬盘写次数
          timestampRender('disk-write-count-chart', this.charts.disk.writeCount, 'chart', false, text => {
            return text
          }, item => {
            item.name = '硬盘写次数'
            item.value = item.value + ' 个/s'
          }, data.writeCount.metrics)
          // 渲染使用时间
          timestampRender('disk-usage-time-chart', this.charts.disk.usageTime, 'chart', false, text => {
            return text
          }, item => {
            item.name = '硬盘使用时间'
            item.value = item.value + ' ms'
          }, data.usageTime.metrics)
        })
      }).catch(() => {
        this.charts.disk.loading = false
      })
    },
    reloadDiskChart() {
      const params = this.getParams()
      this.renderDiskChart(true, params)
    }
  },
  async created() {
    // 加载硬盘
    await this.loadDisk()
    // 渲染表格
    this.renderChart(true)
  }
}
</script>

<style lang="less" scoped>
.machine-monitor-chart-container {
  padding: 16px;
  overflow: auto;

  .machine-monitor-chart-wrapper {
    background: #FFFFFF;
    box-shadow: 0 1px 4px 0 #D9D9D9;
    margin: 2px;
    padding: 24px;
  }
}

.machine-monitor-header {
  display: flex;
  margin-bottom: 18px;

  .monitor-range-wrapper {
    margin-right: 48px;
  }

  .granularity-label {
    margin-right: 4px;
    color: #000;
  }

  .granularity-selector {
    width: 110px;
  }
}

.chart-container {
  display: flex;
  padding: 8px 0 8px 8px;

  .chart-monitor-label {
    color: #888;
    font-weight: 600;
    width: 114px;
  }

  .chart-wrapper {
    width: calc(100% - 114px);
  }

  .chart-box {
    display: flex;
  }

  .chart-desc-label {
    width: 124px;
    color: #000;
    font-weight: 600;
    font-size: 13px;
  }

  .metrics-chart-wrapper {
    width: calc(100% - 512px);
  }

  .metrics-chart {
    width: 100%;
    height: 88px;
    padding: 4px 16px 4px 4px;
  }

  .chart-info {
    width: 384px;
    display: flex;
  }

  .monitor-info-wrapper {
    display: flex;
    flex-direction: column;
    width: 118px;
  }

  .monitor-info-value {
    color: #000;
    padding-top: 4px;
  }
}

.disk-selector {
  width: calc(100% - 24px);
  margin-top: 12px;
}
</style>
