<template>
  <div class="task-logger-appender">
    <LogAppender ref="appender"
                 size="default"
                 :appendStyle="{height: appenderHeight}"
                 :relId="id"
                 :tailType="FILE_TAIL_TYPE.SCHEDULER_TASK_MACHINE_LOG.value"
                 :downloadType="FILE_DOWNLOAD_TYPE.SCHEDULER_TASK_MACHINE_LOG.value">
      <!-- 左侧工具栏 -->
      <template #left-tools>
        <div class="appender-left-tools">
          <!-- 状态 -->
          <a-tag v-if="status" :color="status | formatMachineStatus('color')">
            {{ status | formatMachineStatus('label') }}
          </a-tag>
          <!-- used -->
          <span class="mx8" title="用时"
                v-if="keepTime && SCHEDULER_TASK_MACHINE_STATUS.WAIT.value !== status
                  && SCHEDULER_TASK_MACHINE_STATUS.RUNNABLE.value !== status">
            {{ `${keepTime} (${used}ms)` }}
          </span>
          <!-- exitCode -->
          <span class="mx8" title="退出码"
                v-if="exitCode !== null"
                :style="{'color': exitCode === 0 ? '#4263EB' : '#E03131'}">
            {{ exitCode }}
          </span>
          <!-- 命令输入 -->
          <a-input-search class="command-write-input"
                          size="default"
                          v-if="SCHEDULER_TASK_MACHINE_STATUS.RUNNABLE.value === status"
                          v-model="command"
                          placeholder="输入"
                          @search="sendCommand">
            <template #enterButton>
              <a-icon type="forward"/>
            </template>
            <!-- 发送 lf -->
            <template #suffix>
              <a-icon :class="{'send-lf-trigger': true, 'send-lf-trigger-enable': sendLf}"
                      title="是否发送 \n"
                      type="pull-request"
                      @click="() => sendLf = !sendLf"/>
            </template>
          </a-input-search>
          <!-- 停止 -->
          <a-popconfirm v-if="SCHEDULER_TASK_MACHINE_STATUS.RUNNABLE.value === status"
                        title="是否要停止执行?"
                        placement="bottomLeft"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="terminate">
            <a-button icon="close">停止</a-button>
          </a-popconfirm>
        </div>
      </template>
    </LogAppender>
  </div>
</template>

<script>
import { enumValueOf, FILE_DOWNLOAD_TYPE, FILE_TAIL_TYPE, SCHEDULER_TASK_MACHINE_STATUS } from '@/lib/enum'
import LogAppender from './LogAppender'

export default {
  name: 'SchedulerTaskMachineLogAppender',
  components: {
    LogAppender
  },
  props: {
    appenderHeight: {
      type: String,
      default: 'calc(100vh - 56px)'
    }
  },
  data() {
    return {
      FILE_TAIL_TYPE,
      FILE_DOWNLOAD_TYPE,
      SCHEDULER_TASK_MACHINE_STATUS,
      id: null,
      status: null,
      command: null,
      sendLf: true,
      keepTime: null,
      used: null,
      exitCode: null,
      pollId: null
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
      // 打开日志
      this.$refs.appender.openTail()
      // 获取状态
      await this.getStatus()
      // 检查轮询状态
      if (this.status === SCHEDULER_TASK_MACHINE_STATUS.WAIT.value ||
        this.status === SCHEDULER_TASK_MACHINE_STATUS.RUNNABLE.value) {
        // 轮询状态
        this.pollId = setInterval(this.pollStatus, 2000)
      }
    },
    close() {
      // 关闭tail
      this.$refs.appender.clear()
      this.$refs.appender.close()
      // 关闭轮询
      if (this.pollId) {
        clearInterval(this.pollId)
        this.pollId = null
      }
      this.id = null
      this.status = null
      this.keepTime = null
      this.used = null
      this.exitCode = null
    },
    sendCommand() {
      let command = this.command || ''
      if (this.sendLf) {
        command += '\n'
      }
      if (!command) {
        return
      }
      this.command = ''
      this.$api.writeMachineSchedulerTaskRecord({
        machineRecordId: this.id,
        command
      })
    },
    terminate() {
      this.status = SCHEDULER_TASK_MACHINE_STATUS.TERMINATED.value
      this.$api.terminateMachineSchedulerTaskRecord({
        machineRecordId: this.id
      }).then(() => {
        this.$message.success('已停止')
      })
    },
    async getStatus() {
      await this.$api.getSchedulerTaskMachinesRecordStatus({
        idList: [this.id]
      }).then(({ data }) => {
        if (data && data.length) {
          const status = data[0]
          this.status = status.status
          this.exitCode = status.exitCode
          this.used = status.used
          this.keepTime = status.keepTime
        }
      })
    },
    async pollStatus() {
      await this.getStatus()
      if (this.status !== SCHEDULER_TASK_MACHINE_STATUS.WAIT.value &&
        this.status !== SCHEDULER_TASK_MACHINE_STATUS.RUNNABLE.value) {
        clearInterval(this.pollId)
        this.pollId = null
      }
    }
  },
  filters: {
    formatMachineStatus(status, f) {
      return enumValueOf(SCHEDULER_TASK_MACHINE_STATUS, status)[f]
    }
  },
  beforeDestroy() {
    this.pollId !== null && clearInterval(this.pollId)
    this.pollId = null
  }
}
</script>

<style lang="less" scoped>
.task-logger-appender {
  padding: 8px;

  .appender-left-tools {
    display: flex;
    align-items: center;
  }
}

</style>
