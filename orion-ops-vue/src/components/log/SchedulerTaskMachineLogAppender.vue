<template>
  <div class="task-logger-appender">
    <LogAppender ref="appender"
                 size="default"
                 :appendStyle="{height: appenderHeight}"
                 :relId="id"
                 :tailType="$enum.FILE_TAIL_TYPE.SCHEDULER_TASK_MACHINE_LOG.value"
                 :downloadType="$enum.FILE_DOWNLOAD_TYPE.SCHEDULER_TASK_MACHINE_LOG.value">
      <!-- 左侧工具栏 -->
      <template #left-tools>
        <div class="appender-left-tools">
          <!-- 状态 -->
          <a-tag v-if="status" :color="$enum.valueOf($enum.SCHEDULER_TASK_MACHINE_STATUS, status).color">
            {{ $enum.valueOf($enum.SCHEDULER_TASK_MACHINE_STATUS, status).label }}
          </a-tag>
          <!-- used -->
          <span class="mx8" title="用时"
                v-if="keepTime && $enum.SCHEDULER_TASK_MACHINE_STATUS.WAIT.value !== status
                  && $enum.SCHEDULER_TASK_MACHINE_STATUS.RUNNABLE.value !== status">
            {{ `${keepTime} (${used}ms)` }}
          </span>
          <!-- exitCode -->
          <span class="mx8" title="退出码"
                v-if="exitCode !== null"
                :style="{'color': exitCode === 0 ? '#4263EB' : '#E03131'}">
            {{ exitCode }}
          </span>
          <!-- 停止 -->
          <a-popconfirm v-if="$enum.SCHEDULER_TASK_MACHINE_STATUS.RUNNABLE.value === status"
                        title="是否要停止执行?"
                        placement="bottomLeft"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="terminated">
            <a-button class="ml8" icon="close" size="small">停止</a-button>
          </a-popconfirm>
        </div>
      </template>
    </LogAppender>
  </div>
</template>

<script>
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
      id: null,
      status: null,
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
      if (this.status === this.$enum.SCHEDULER_TASK_MACHINE_STATUS.WAIT.value ||
        this.status === this.$enum.SCHEDULER_TASK_MACHINE_STATUS.RUNNABLE.value) {
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
    terminated() {
      this.status = this.$enum.SCHEDULER_TASK_MACHINE_STATUS.TERMINATED.value
      // this.$api.terminatedExecTask({
      //   id: this.execId
      // }).then(() => {
      //   this.$message.success('已停止')
      // })
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
      if (this.status !== this.$enum.SCHEDULER_TASK_MACHINE_STATUS.WAIT.value &&
        this.status !== this.$enum.SCHEDULER_TASK_MACHINE_STATUS.RUNNABLE.value) {
        clearInterval(this.pollId)
        this.pollId = null
      }
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
