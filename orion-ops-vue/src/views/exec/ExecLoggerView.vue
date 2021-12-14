<template>
  <div class="exec-logger-appender">
    <LogAppender ref="appender"
                 size="default"
                 :appendStyle="{height: 'calc(100% - 46px)'}"
                 :relId="execId"
                 :config="{type: $enum.FILE_TAIL_TYPE.EXEC_LOG.value, relId: execId}">
      <!-- 左侧工具栏 -->
      <div class="appender-left-tools" slot="left-tools">
        <!-- 命令输入 -->
        <a-input-search class="command-write-input"
                        size="default"
                        v-if="$enum.BATCH_EXEC_STATUS.RUNNABLE.value === status"
                        v-model="command"
                        placeholder="输入"
                        @search="sendCommand">
          <a-icon type="forward" slot="enterButton"/>
        </a-input-search>
        <!-- 状态 -->
        <a-tag class="machine-exec-status" :color="$enum.valueOf($enum.BATCH_EXEC_STATUS, status).color">
          {{ $enum.valueOf($enum.BATCH_EXEC_STATUS, status).label }}
        </a-tag>
        <!-- used -->
        <span class="mx8" title="用时"
              v-if="$enum.BATCH_EXEC_STATUS.COMPLETE.value === status">
          {{ used }}ms
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
    </LogAppender>
  </div>
</template>

<script>
import LogAppender from '@/components/log/LogAppender'

export default {
  name: 'ExecLoggerView',
  components: {
    LogAppender
  },
  data() {
    return {
      status: null,
      execId: null,
      command: null,
      used: null,
      exitCode: null,
      pollId: null
    }
  },
  methods: {
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
          this.used = status.used
          this.exitCode = status.exitCode
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
  created() {
    this.execId = parseInt(this.$route.params.id)
  },
  async mounted() {
    // 获取状态
    await this.getStatus()
    if (this.status === this.$enum.BATCH_EXEC_STATUS.WAITING.value ||
      this.status === this.$enum.BATCH_EXEC_STATUS.RUNNABLE.value) {
      // 轮询状态
      this.pollId = setInterval(this.pollStatus, 1000)
    }
  }
}
</script>

<style lang="less" scoped>

.exec-logger-appender {
  padding: 0 8px 8px 8px;
  height: 100vh;

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
