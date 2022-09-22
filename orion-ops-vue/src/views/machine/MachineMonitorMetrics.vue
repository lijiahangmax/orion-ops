<template>
  <div class="machine-monitor-container">
    <a-tabs v-model="active" tabPosition="top" size="small" :animated="false">
      <!-- 概要 -->
      <a-tab-pane v-if="monitorStarted" :key="1" tab="概要">
        <MachineMonitorSummary ref="summary" :machineId="machineId" :config="config"/>
      </a-tab-pane>
      <!-- 监控 -->
      <a-tab-pane v-if="monitorStarted" :key="2" tab="监控">
        <MachineMonitorChart ref="chart" :machineId="machineId"/>
      </a-tab-pane>
      <!-- 报警配置 -->
      <a-tab-pane :key="3" tab="报警配置">
        <MachineMonitorAlarmConfig :machineId="machineId"/>
      </a-tab-pane>
      <!-- 报警配置 -->
      <a-tab-pane :key="4" tab="报警记录">
        <MachineMonitorAlarmHistory :machineId="machineId" :machineName="machineName"/>
      </a-tab-pane>
      <!-- 拓展 -->
      <template #tabBarExtraContent>
        <!-- 刷新 -->
        <a-button class="mr16"
                  v-if="config"
                  v-show="active === 1 || active === 2"
                  size="small"
                  @click="refresh">
          刷新
        </a-button>
        <!-- 自动刷新 -->
        <span class="auto-refresh-wrapper" v-if="config" v-show="active === 1 || active === 2">
          <span class="normal-label refresh-label">自动刷新</span>
          <a-tooltip title="90秒自动刷新监控数据">
            <a-switch v-model="autoRefresh" size="small" @change="changeAutoRefresh"/>
          </a-tooltip>
          <a-divider type="vertical"/>
        </span>
        <!-- 机器名称 -->
        <a-tooltip v-if="config" title="点击复制 IP">
            <span class="pointer machine-name-wrapper" @click="$copy(config.machineHost)">
            {{ config.machineName }} ({{ config.machineHost }})
          </span>
        </a-tooltip>
        <a-divider v-if="config" type="vertical"/>
        <!-- terminal -->
        <a target="_blank" class="terminal-wrapper"
           :href="`#/machine/terminal/${machineId}`"
           @click="openTerminal($event)">
          <a-tooltip title="ctrl 点击新页面打开终端" placement="left">
            Terminal
          </a-tooltip>
        </a>
      </template>
    </a-tabs>
    <!-- 终端模态框 -->
    <TerminalModal ref="terminal" :visibleMinimize="false"/>
  </div>
</template>

<script>
import { MONITOR_STATUS } from '@/lib/enum'
import TerminalModal from '@/components/terminal/TerminalModal'

export default {
  name: 'MachineMonitorMetrics',
  components: {
    TerminalModal,
    MachineMonitorSummary: () => import('@/components/machine/MachineMonitorSummary'),
    MachineMonitorChart: () => import('@/components/machine/MachineMonitorChart'),
    MachineMonitorAlarmConfig: () => import('@/components/machine/MachineMonitorAlarmConfig'),
    MachineMonitorAlarmHistory: () => import('@/components/machine/MachineMonitorAlarmHistory')
  },
  data() {
    return {
      active: null,
      machineId: null,
      machineName: null,
      monitorStarted: false,
      config: null,
      autoRefresh: true,
      pollId: null
    }
  },
  methods: {
    refresh() {
      let ref
      if (this.active === 1) {
        ref = this.$refs.summary
      } else if (this.active === 2) {
        ref = this.$refs.chart
      }
      ref && ref.doRefresh && ref.doRefresh()
    },
    doRefresh() {
      if (!this.autoRefresh) {
        return
      }
      this.refresh()
    },
    changeAutoRefresh(e) {
      this.autoRefresh = e
    },
    openTerminal(e) {
      if (!e.ctrlKey) {
        e.preventDefault()
        // 打开模态框
        this.$api.getMachineDetail({
          id: this.machineId
        }).then(({ data }) => {
          this.$refs.terminal.open(data, this.machineId)
        })
        return false
      } else {
        // 跳转页面
        return true
      }
    }
  },
  created() {
    this.machineId = ~~this.$route.params.machineId
    this.active = ~~(this.$route.query.tab || 1)
  },
  mounted() {
    this.$api.getMachineMonitorConfig({
      machineId: this.machineId
    }).then(({ data }) => {
      this.config = data
      this.machineName = data.machineName
      this.monitorStarted = MONITOR_STATUS.RUNNING.value === data.status
      if (!this.monitorStarted && (this.active === 1 || this.active === 2)) {
        this.$message.warning('监控插件未运行')
      }
      this.pollId = setInterval(this.doRefresh, 90000)
    })
  },
  beforeDestroy() {
    this.pollId && clearInterval(this.pollId)
    this.pollId = null
  }
}
</script>

<style lang="less" scoped>

.machine-monitor-container {
  position: relative;
  top: -18px;
  left: -18px;
  width: calc(100% + 36px);
}

.machine-name-wrapper {
  font-weight: 500;
  color: rgba(0, 0, 0, .9);
}

.refresh-label {
  display: inline-block;
  margin-right: 8px;
  color: rgba(0, 0, 0, 0.9);
}

::v-deep .ant-tabs-bar {
  margin: 0;
  background: #FFF;
  padding: 4px 12px 4px 8px;
}

</style>
