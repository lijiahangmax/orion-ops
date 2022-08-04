<template>
  <div class="machine-monitor-container">
    <a-tabs defaultActiveKey="1" tabPosition="top" size="small" :animated="false">
      <!-- 概要 -->
      <a-tab-pane key="1" tab="概要">
        <MachineMonitorSummary :machineId="machineId"/>
      </a-tab-pane>
      <!-- 监控 -->
      <a-tab-pane key="2" tab="监控">
        <MachineMonitorChart :machineId="machineId"/>
      </a-tab-pane>
      <!-- 报警配置 -->
      <a-tab-pane key="3" tab="报警配置">
        <MachineMonitorAlarm :machineId="machineId"/>
      </a-tab-pane>
      <template #tabBarExtraContent>
        <a href="">Terminal</a>
      </template>
    </a-tabs>
  </div>
</template>

<script>
import MachineMonitorSummary from '@/components/machine/MachineMonitorSummary'

export default {
  name: 'MachineMonitorMetrics',
  components: {
    MachineMonitorSummary,
    MachineMonitorChart: () => import('@/components/machine/MachineMonitorChart'),
    MachineMonitorAlarm: () => import('@/components/machine/MachineMonitorAlarm')
  },
  data() {
    return {
      machineId: null
    }
  },
  methods: {
    openTerminal(e, record) {
      if (!e.ctrlKey) {
        e.preventDefault()
        // 打开模态框
        const now = Date.now()
        this.openTerminalArr.push({
          name: record.name,
          terminalId: now
        })
        this.$nextTick(() => {
          this.$refs[`terminalModal${now}`][0].open(record, now)
        })
        return false
      } else {
        // 跳转页面
        return true
      }
    }
  },
  mounted() {
    this.machineId = this.$route.params.machineId
  }
}
</script>

<style lang="less" scoped>

.machine-monitor-container {
  background: #FFF;
  padding: 8px 16px;
  border-radius: 4px;
}
</style>
