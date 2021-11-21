<template>
  <div class="exec-logger-appender">
    <LogAppender ref="appender"
                 size="default"
                 :appendStyle="{height: 'calc(100% - 46px)'}"
                 :relId="execId"
                 :config="{type: 10, relId: execId}">
      <!-- 左侧工具栏 -->
      <div class="appender-left-tools"
           v-if="$enum.BATCH_EXEC_STATUS.WAITING.value === status || $enum.BATCH_EXEC_STATUS.RUNNABLE.value === status"
           slot="left-tools">
        <!-- 命令输入 -->
        <a-input-search class="command-write-input"
                        size="default"
                        v-if="$enum.BATCH_EXEC_STATUS.RUNNABLE.value === status"
                        v-model="command"
                        placeholder="输入"
                        @search="sendCommand">
          <a-icon type="forward" slot="enterButton"/>
        </a-input-search>
        <!-- 终止 -->
        <a-button class="terminated-button"
                  size="default"
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
      command: null
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
      this.$api.terminatedExecTask({
        id: this.execId
      }).then(() => {
        this.$message.success('已终止')
      })
    }
  },
  created() {
    this.execId = parseInt(this.$route.params.id)
    this.status = parseInt(this.$route.params.status)
  }
}
</script>

<style lang="less" scoped>

.exec-logger-appender {
  padding: 0 8px 8px 8px;
  height: 100vh;

  .appender-left-tools {
    display: flex;

    .command-write-input {
      margin-right: 8px;
      width: 70%;
    }
  }
}

</style>
