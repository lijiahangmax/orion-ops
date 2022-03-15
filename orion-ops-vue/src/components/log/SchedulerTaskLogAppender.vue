<template>
  <a-spin :spinning="loading">
    <div class="task-logger-container" :style="{height}">
      <!-- 菜单 -->
      <div class="task-machines-menu">
        <a-menu mode="inline" v-model="selectedKeys">
          <a-menu-item v-for="machine in record.machines" :key="machine.id" :title="machine.machineName">
            <div class="menu-item-machine-wrapper">
              <!-- 机器名称 -->
              <span class="menu-item-machine-name auto-ellipsis-item">{{ machine.machineName }}</span>
              <!-- 状态 -->
              <span class="menu-item-machine-status">
                <a-tag :color="$enum.valueOf($enum.SCHEDULER_TASK_MACHINE_STATUS, machine.status).color">
                  {{ $enum.valueOf($enum.SCHEDULER_TASK_MACHINE_STATUS, machine.status).label }}
                </a-tag>
              </span>
            </div>
          </a-menu-item>
        </a-menu>
      </div>
      <!-- 日志 -->
      <div class="task-logger-appender-container">
        <div class="task-logger-appender"
             v-for="machine in record.machines"
             v-show="machine.id === selectedKeys[0]"
             :key="machine.id">
          <LogAppender :ref="`appender-${machine.id}`"
                       size="default"
                       :appendStyle="{height: appenderHeight}"
                       :relId="machine.id"
                       :tailType="$enum.FILE_TAIL_TYPE.SCHEDULER_TASK_MACHINE_LOG.value"
                       :downloadType="$enum.FILE_DOWNLOAD_TYPE.SCHEDULER_TASK_MACHINE_LOG.value"
                       :rightMenuX="e => e.offsetX + 268">
            <!-- 左侧工具栏 -->
            <template #left-tools>
              <div class="appender-left-tools">
                <!-- used -->
                <span class="mx8" title="用时"
                      v-if="machine.keepTime && $enum.SCHEDULER_TASK_MACHINE_STATUS.WAIT.value !== machine.status
                          && $enum.SCHEDULER_TASK_MACHINE_STATUS.RUNNABLE.value !== machine.status">
                    {{ `${machine.keepTime} (${machine.used}ms)` }}
                </span>
                <!-- exitCode -->
                <span class="mx8" title="退出码"
                      v-if="machine.exitCode !== null"
                      :style="{'color': machine.exitCode === 0 ? '#4263EB' : '#E03131'}">
                      {{ machine.exitCode }}
                </span>
                <!-- 停止 -->
                <a-popconfirm v-if="$enum.SCHEDULER_TASK_MACHINE_STATUS.RUNNABLE.value === machine.status"
                              title="是否要停止执行?"
                              placement="bottomLeft"
                              ok-text="确定"
                              cancel-text="取消"
                              @confirm="terminatedMachine(record.id, machine.id)">
                  <a-button icon="close">停止</a-button>
                </a-popconfirm>
                <!-- 跳过 -->
                <a-popconfirm v-if="$enum.SCHEDULER_TASK_MACHINE_STATUS.WAIT.value === machine.status"
                              title="是否要跳过执行?"
                              placement="bottomLeft"
                              ok-text="确定"
                              cancel-text="取消"
                              @confirm="skipMachine(record.id, machine.id)">
                  <a-button icon="stop">跳过</a-button>
                </a-popconfirm>
              </div>
            </template>
          </LogAppender>
        </div>
      </div>
    </div>
  </a-spin>
</template>

<script>
import LogAppender from './LogAppender'

export default {
  name: 'SchedulerTaskLogAppender',
  components: {
    LogAppender
  },
  props: {
    appenderHeight: {
      type: String,
      default: 'calc(100vh - 96px)'
    },
    height: String
  },
  data() {
    return {
      id: null,
      record: {},
      pollId: null,
      loading: false,
      selectedKeys: [],
      openedFiles: []
    }
  },
  methods: {
    async open(id) {
      this.id = id
      // 关闭轮询
      if (this.pollId) {
        clearInterval(this.pollId)
        this.pollId = null
      }
      // 加载明细
      this.loading = true
      await this.$api.getSchedulerTaskRecordDetail({
        id
      }).then(({ data }) => {
        // 设置数据
        this.record = data
        this.selectedKeys[0] = data.machines[0].id
        this.loading = false
      }).then(() => {
        // 打开日志
        for (const machine of this.record.machines) {
          if (machine.status !== this.$enum.SCHEDULER_TASK_MACHINE_STATUS.WAIT.value &&
            machine.status !== this.$enum.SCHEDULER_TASK_MACHINE_STATUS.SKIPPED.value) {
            this.$refs[`appender-${machine.id}`][0].openTail()
            this.openedFiles.push(machine.id)
          }
        }
      }).then(() => {
        // 设置轮询
        if (this.record.status === this.$enum.SCHEDULER_TASK_STATUS.RUNNABLE.value) {
          // 轮询状态
          this.pollId = setInterval(this.pollStatus, 2000)
        }
      }).catch(() => {
        this.loading = false
      })
    },
    close() {
      // 关闭tail
      for (const machine of this.record.machines) {
        const appender = this.$refs[`appender-${machine.id}`][0]
        appender.clear()
        appender.close()
      }
      // 关闭轮询
      if (this.pollId) {
        clearInterval(this.pollId)
        this.pollId = null
      }
      this.id = null
      this.record = {}
      this.selectedKeys = []
      this.openedFiles = []
    },
    terminatedMachine(id, machineRecordId) {
      this.$api.terminatedMachineSchedulerTaskRecord({
        id,
        machineRecordId
      }).then(() => {
        this.$message.success('已停止')
      })
    },
    skipMachine(id, machineRecordId) {
      this.$api.skipMachineSchedulerTaskRecord({
        id,
        machineRecordId
      }).then(() => {
        this.$message.success('已跳过')
      })
    },
    async pollStatus() {
      const pullIdList = this.record.machines.filter(s => s.status === this.$enum.SCHEDULER_TASK_MACHINE_STATUS.WAIT.value ||
        s.status === this.$enum.SCHEDULER_TASK_MACHINE_STATUS.RUNNABLE.value).map(s => s.id)
      if (pullIdList.length === 0) {
        clearInterval(this.pollId)
        this.pollId = null
        return
      }
      await this.$api.getSchedulerTaskMachinesRecordStatus({
        idList: pullIdList
      }).then(({ data }) => {
        if (!data || !data.length) {
          return
        }
        for (const status of data) {
          const matchMachines = this.record.machines.filter(s => s.id === status.id)
          const matchMachine = matchMachines && matchMachines.length && matchMachines[0]
          if (!matchMachine) {
            continue
          }
          // 设置状态
          matchMachine.status = status.status
          matchMachine.exitCode = status.exitCode
          matchMachine.used = status.used
          matchMachine.keepTime = status.keepTime
          // 检查打开tail
          const opened = this.openedFiles.filter(s => s === status.id).length > 0
          if (!opened && status.status !== this.$enum.SCHEDULER_TASK_MACHINE_STATUS.WAIT.value &&
            status.status !== this.$enum.SCHEDULER_TASK_MACHINE_STATUS.SKIPPED.value) {
            // 打开日志
            this.$refs[`appender-${status.id}`][0].openTail()
            this.openedFiles.push(status.id)
          }
        }
      })
    }
  },
  beforeDestroy() {
    this.pollId !== null && clearInterval(this.pollId)
    this.pollId = null
  }
}
</script>

<style lang="less" scoped>

.task-logger-container {
  display: flex;
  background: #F0F2F5;

  .task-machines-menu {
    width: 240px;
    margin: 16px;
    padding: 8px;
    background: #FFFFFF;
    border-radius: 4px;

    /deep/ .ant-menu-item {
      padding: 0 0 0 12px !important;
    }

    .menu-item-machine-wrapper {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .menu-item-machine-name {
        width: 147px;
      }
    }
  }

  .task-logger-appender-container {
    width: calc(100% - 287px);
    margin: 16px 16px 16px 0;
    background: #FFF;
    padding: 8px 16px 16px 16px;
    border-radius: 4px;

    .appender-left-tools {
      display: flex;
      align-items: center;
    }
  }

}

</style>
