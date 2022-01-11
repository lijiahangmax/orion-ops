<template>
  <div class="exec-logger-appender">
    <LogAppender ref="appender"
                 size="default"
                 :appendStyle="{height: appenderHeight}"
                 :relId="execId"
                 :config="{type: $enum.FILE_TAIL_TYPE.EXEC_LOG.value, relId: execId}">
      <!-- 左侧工具栏 -->
      <template #left-tools>
        <div class="appender-left-tools">
          <!-- 命令输入 -->
          <a-input-search class="command-write-input"
                          size="default"
                          v-if="$enum.BATCH_EXEC_STATUS.RUNNABLE.value === status"
                          v-model="command"
                          placeholder="输入"
                          @search="sendCommand">
            <template #enterButton>
              <a-icon type="forward"/>
            </template>
          </a-input-search>
          <!-- 状态 -->
          <a-tag class="machine-exec-status" v-if="status" :color="$enum.valueOf($enum.BATCH_EXEC_STATUS, status).color">
            {{ $enum.valueOf($enum.BATCH_EXEC_STATUS, status).label }}
          </a-tag>
          <!-- used -->
          <span class="mx8" title="用时"
                v-if="$enum.BATCH_EXEC_STATUS.COMPLETE.value === status">
            {{ `${keepTime} (${used}ms)` }}
          </span>
          <!-- exitCode -->
          <span class="mx8" title="退出码"
                v-if="exitCode !== null"
                :style="{'color': exitCode === 0 ? '#4263EB' : '#E03131'}">
            {{ exitCode }}
          </span>
          <!-- 终止 -->
          <a-button class="terminated-button"
                    v-if="$enum.BATCH_EXEC_STATUS.RUNNABLE.value === status"
                    icon="close"
                    @click="terminated">
            终止
          </a-button>
        </div>
      </template>
    </LogAppender>
  </div>
</template>

<script>
import LogAppender from './LogAppender'

export default {
  name: 'ExecLoggerAppender',
  components: {
    LogAppender
  },
  props: {
    appenderHeight: {
      type: String,
      default: 'calc(100vh - 54px)'
    }
  },
  data() {
    return {
      status: null,
      execId: null,
      command: null,
      keepTime: null,
      used: null,
      exitCode: null,
      pollId: null
    }
  },
  methods: {
    async open(execId) {
      this.execId = execId
      // 关闭轮询
      if (this.pollId) {
        clearInterval(this.pollId)
      }
      // 打开日志
      this.$refs.appender.openTail()
      // 获取状态
      await this.getStatus()
      // 检查轮询状态
      if (this.status === this.$enum.BATCH_EXEC_STATUS.WAITING.value ||
        this.status === this.$enum.BATCH_EXEC_STATUS.RUNNABLE.value) {
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
      }
      this.status = null
      this.execId = null
      this.command = null
      this.keepTime = null
      this.used = null
      this.exitCode = null
      this.pollId = null
    },
    sendCommand() {
      if (!this.command) {
        return
      }
      const command = this.command
      this.command = ''
      this.$api.writeExecTask({
        id: this.execId,
        command
      })
    },
    terminated() {
      this.status = this.$enum.BATCH_EXEC_STATUS.TERMINATED.value
      this.$api.terminatedExecTask({
        id: this.execId
      }).then(() => {
        this.$message.success('已终止')
      })
    },
    async getStatus() {
      await this.$api.getExecTaskStatus({
        idList: [this.execId]
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
      if (this.status !== this.$enum.BATCH_EXEC_STATUS.WAITING.value &&
        this.status !== this.$enum.BATCH_EXEC_STATUS.RUNNABLE.value) {
        clearInterval(this.pollId)
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

.exec-logger-appender {
  padding: 0 8px 8px 8px;

  .appender-left-tools {
    display: flex;
    align-items: center;

    .command-write-input {
      margin-right: 8px;
      width: 70%;
    }
  }
}

</style>
