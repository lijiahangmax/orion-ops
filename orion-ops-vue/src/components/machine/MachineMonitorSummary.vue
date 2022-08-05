<template>
  <div class="summary-container">
    <!-- 第一层 信息&监控 -->
    <a-row class="metrics-floor-1">
      <!-- 信息 -->
      <a-col :span="10">
        <div class="machine-card-wrapper machine-summary-wrapper">
          <a-card :bodyStyle="{padding: '18px', height: '392px'}">
            <template v-if="summaryLoading || !summaryData">
              <a-skeleton active :title="true" :paragraph="{rows: 9}"/>
            </template>
            <template v-else-if="summaryData">
              <h3 class="card-title">机器信息</h3>
              <div class="machine-info-wrapper">
                <span class="machine-info-label">机器id</span>
                <span class="machine-info-value">{{ summaryData.machineId }}</span>
              </div>
              <div class="machine-info-wrapper">
                <span class="machine-info-label">主机名称</span>
                <span class="machine-info-value">{{ summaryData.os.hostname }}</span>
              </div>
              <div class="machine-info-wrapper">
                <span class="machine-info-label">
                  系统负载
                  <a-tooltip title="windows 无法统计">
                    <a-icon type="question-circle"/>
                  </a-tooltip>
                </span>
                <span class="machine-info-value">
                  {{ summaryData.load.oneMinuteLoad }},
                  {{ summaryData.load.fiveMinuteLoad }},
                  {{ summaryData.load.fifteenMinuteLoad }}
                </span>
              </div>
              <div class="machine-info-wrapper">
                <span class="machine-info-label">启动时间</span>
                <span class="machine-info-value">{{ summaryData.os.uptime }}</span>
              </div>
              <div class="machine-info-wrapper">
                <span class="machine-info-label">系统版本</span>
                <span class="machine-info-value" :title="summaryData.os.osName">{{ summaryData.os.osName }}</span>
              </div>
              <div class="machine-info-wrapper">
                <span class="machine-info-label">CPU 型号</span>
                <span class="machine-info-value" :title="summaryData.os.cpuName">{{ summaryData.os.cpuName }}</span>
              </div>
              <div class="machine-info-wrapper">
                <span class="machine-info-label">CPU 核心数</span>
                <span class="machine-info-value">{{ summaryData.os.cpuLogicalCore }}核</span>
              </div>
              <div class="machine-info-wrapper">
                <span class="machine-info-label">总内存</span>
                <span class="machine-info-value">{{ summaryData.os.totalMemory }}</span>
              </div>
              <div class="machine-info-wrapper">
                <span class="machine-info-label">采集状态</span>
                <span class="machine-info-value">
                  <a-badge :status="summaryData.status ? 'processing' : 'default'"/>
                  {{ summaryData.status ? '采集中' : '已暂停' }}
                </span>
              </div>
              <div class="machine-info-wrapper">
                <span class="machine-info-label">插件版本</span>
                <span class="machine-info-value">V{{ summaryData.version }}</span>
              </div>
            </template>
          </a-card>
        </div>
      </a-col>
      <!-- 监控 -->
      <a-col :span="14">
        <a-row>
          <a-col :span="12">
            <div class="machine-card-wrapper machine-chart-top-wrapper machine-chart-left-wrapper">
              <a-card :loading="chartLoading" :bodyStyle="{padding: '18px', height: '186px'}">
                <h3 class="card-title">CPU利用率 (%) </h3>
              </a-card>
            </div>
          </a-col>
          <a-col :span="12">
            <div class="machine-card-wrapper machine-chart-top-wrapper machine-chart-right-wrapper">
              <a-card :loading="chartLoading" :bodyStyle="{padding: '18px', height: '186px'}">
                <h3 class="card-title">内存利用量 (MB) </h3>
              </a-card>
            </div>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <div class="machine-card-wrapper machine-chart-bottom-wrapper machine-chart-left-wrapper">
              <a-card :loading="chartLoading" :bodyStyle="{padding: '18px', height: '186px'}">
                <h3 class="card-title">带宽使用 (Mbps) </h3>
              </a-card>
            </div>
          </a-col>
          <a-col :span="12">
            <div class="machine-card-wrapper machine-chart-bottom-wrapper machine-chart-right-wrapper">
              <a-card :loading="chartLoading" :bodyStyle="{padding: '18px', height: '186px'}">
                <h3 class="card-title">系统盘IO (MB/s) </h3>
              </a-card>
            </div>
          </a-col>
        </a-row>
      </a-col>
    </a-row>
    <!-- 第二层 磁盘&进程 -->
    <a-row class="metrics-floor-2">
      <!-- 磁盘 -->
      <a-col :span="9">
        <div class="machine-card-wrapper machine-disk-wrapper">
          <a-card :loading="summaryLoading" :bodyStyle="{padding: '18px'}">
            <h3 class="card-title">磁盘信息</h3>
          </a-card>
        </div>
      </a-col>
      <!-- 进程 -->
      <a-col :span="15">
        <div class="machine-card-wrapper machine-processes-wrapper">
          <a-card :loading="summaryLoading" :bodyStyle="{padding: '18px'}">
            <h3 class="card-title">进程 Top 10</h3>
          </a-card>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script>
export default {
  name: 'MachineMonitorSummary',
  props: {
    machineId: Number,
    config: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    return {
      summaryLoading: false,
      chartLoading: false,
      summaryData: {}
    }
  },
  methods: {
    name() {
    }
  },
  created() {
    this.summaryLoading = true
    this.$api.getMachineMonitorMetrics({
      machineId: this.machineId
    }).then(({ data }) => {
      this.summaryLoading = false
      this.summaryData = data
    }).catch(() => {
      this.summaryLoading = false
      this.summaryData = null
    })
  },
  mounted() {
  }
}
</script>

<style lang="less" scoped>

.summary-container {
  padding: 16px;
  overflow: auto;

  .machine-card-wrapper {
    box-shadow: 0 1px 4px 0 #D9D9D9;
  }

  .machine-summary-wrapper {
    margin: 2px 8px 2px 2px;
  }

  .machine-chart-top-wrapper {
    margin-top: 2px;
    margin-bottom: 7px;
  }

  .machine-chart-bottom-wrapper {
    margin-top: 10px;
    margin-bottom: 2px;
  }

  .machine-chart-left-wrapper {
    margin-left: 8px;
    margin-right: 6px;
  }

  .machine-chart-right-wrapper {
    margin-left: 10px;
    margin-right: 2px;
  }

  .machine-info-wrapper {
    width: 100%;
    margin-bottom: 15px;
    display: flex;

    .machine-info-label {
      color: rgba(0, 0, 0, .4);
      display: inline-block;
      white-space: nowrap;
      font-size: 12px;
      width: 92px;
    }

    .machine-info-value {
      font-size: 12px;
      color: rgba(0, 0, 0, .9);
      display: inline-block;
      white-space: pre;
      width: calc(100% - 95px);
      text-overflow: ellipsis;
      overflow: hidden;
    }
  }

  .metrics-floor-1 {
    margin-bottom: 12px;
  }

  .metrics-floor-2 {
    margin-top: 10px;
  }

  .machine-disk-wrapper {
    margin: 2px 8px 2px 2px;
  }

  .machine-processes-wrapper {
    margin: 2px 2px 2px 8px;
  }
}

.card-title {
  color: rgb(0, 0, 0, .9);
  font-weight: 700;
  margin-bottom: 14px;
}

</style>
