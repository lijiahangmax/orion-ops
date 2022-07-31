<template>
  <div class="app-release-container">
    <!-- 步骤 -->
    <div class="app-release-steps">
      <a-steps :current="current" :status="detail.status | formatActionStatus('stepStatus')">
        <template v-for="action in detail.actions">
          <a-step :key="action.id"
                  :title="action.actionName"
                  :subTitle="action.used ? `${action.used}ms` : ''">
            <template v-if="action.status === ACTION_STATUS.RUNNABLE.value" #icon>
              <a-icon type="loading"/>
            </template>
          </a-step>
        </template>
      </a-steps>
    </div>
    <!-- 日志 -->
    <div class="machine-release-log">
      <logAppender ref="appender"
                   size="default"
                   :height="appenderHeight"
                   :relId="id"
                   :tailType="FILE_TAIL_TYPE.APP_RELEASE_LOG.value"
                   :downloadType="FILE_DOWNLOAD_TYPE.APP_RELEASE_MACHINE_LOG.value">
        <!-- 左侧工具 -->
        <template #left-tools>
          <div class="machine-log-tools">
            <a-tag color="#5C7CFA" v-if="detail.machineName">
              {{ detail.machineName }}
            </a-tag>
            <a-tag color="#40C057" v-if="detail.machineHost">
              {{ detail.machineHost }}
            </a-tag>
            <!-- 命令输入 -->
            <a-input-search class="command-write-input"
                            size="default"
                            v-if="ACTION_STATUS.RUNNABLE.value === detail.status"
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
            <a-popconfirm v-if="ACTION_STATUS.RUNNABLE.value === detail.status"
                          title="是否要停止执行?"
                          placement="bottomLeft"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="terminateMachine">
              <a-button icon="close">停止</a-button>
            </a-popconfirm>
          </div>
        </template>
      </logAppender>
    </div>
  </div>
</template>

<script>
import { enumValueOf, ACTION_STATUS, FILE_DOWNLOAD_TYPE, FILE_TAIL_TYPE } from '@/lib/enum'
import LogAppender from '@/components/log/LogAppender'

export default {
  name: 'AppReleaseMachineLogAppender',
  components: { LogAppender },
  props: {
    appenderHeight: String
  },
  data() {
    return {
      FILE_TAIL_TYPE,
      FILE_DOWNLOAD_TYPE,
      ACTION_STATUS,
      id: null,
      current: 0,
      detail: {},
      command: null,
      sendLf: true,
      pollId: null
    }
  },
  methods: {
    open(id) {
      this.id = id
      this.$api.getAppReleaseMachineDetail({
        releaseMachineId: this.id
      }).then(({ data }) => {
        this.detail = data
        this.setStepsCurrent()
        // 设置轮询状态
        if (this.detail.status === ACTION_STATUS.WAIT.value ||
          this.detail.status === ACTION_STATUS.RUNNABLE.value) {
          this.pollId = setInterval(this.pollStatus, 2000)
        }
      }).then(() => {
        this.$nextTick(() => this.$refs.appender.openTail())
      })
    },
    close() {
      // 关闭轮询
      if (this.pollId) {
        clearInterval(this.pollId)
        this.pollId = null
      }
      // 关闭tail
      this.$nextTick(() => {
        this.$refs.appender.clear()
        this.$refs.appender.dispose()
      })
      this.id = null
      this.current = 0
      this.detail = {}
    },
    terminateMachine() {
      this.$api.terminateAppReleaseMachine({
        releaseMachineId: this.detail.id
      }).then(() => {
        this.$message.success('已停止')
      })
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
      this.$api.writeAppReleaseMachine({
        releaseMachineId: this.detail.id,
        command
      })
    },
    pollStatus() {
      this.$api.getAppReleaseMachineStatus({
        releaseMachineId: this.id
      }).then(({ data }) => {
        this.detail.status = data.status
        // 清除状态轮询
        if (this.detail.status !== ACTION_STATUS.WAIT.value &&
          this.detail.status !== ACTION_STATUS.RUNNABLE.value) {
          clearInterval(this.pollId)
          this.pollId = null
        }
        // 设置action
        if (!data.actions || !data.actions.length || !this.detail.actions || !this.detail.actions.length) {
          return
        }
        for (const actionStatus of data.actions) {
          this.detail.actions.filter(s => s.id === actionStatus.id).forEach(e => {
            e.status = actionStatus.status
            e.used = actionStatus.used
          })
        }
        // 设置当前操作
        this.setStepsCurrent()
      })
    },
    setStepsCurrent() {
      const len = this.detail.actions.length
      let curr = len - 1
      for (let i = 0; i < len; i++) {
        const status = this.detail.actions[i].status
        if (status !== ACTION_STATUS.FINISH.value) {
          curr = i
          break
        }
      }
      this.current = curr
    }
  },
  filters: {
    formatActionStatus(status, f) {
      return enumValueOf(ACTION_STATUS, status)[f]
    }
  },
  beforeDestroy() {
    this.pollId !== null && clearInterval(this.pollId)
    this.pollId = null
  }
}
</script>

<style lang="less" scoped>

.app-release-steps {
  height: 46px;
  padding: 8px 12px 0 12px;
}

.machine-log-tools {
  display: flex;
  align-items: center;
}
</style>
