<template>
  <a-spin :spinning="loading">
    <div class="app-release-container" :style="{height}">
      <!-- 菜单 -->
      <div class="release-machines-menu">
        <a-menu mode="inline" v-model="selectedKeys">
          <a-menu-item v-for="machine in record.machines"
                       :key="machine.id"
                       :title="machine.machineName"
                       @click="chooseMachineLog(machine.id)">
            <div class="menu-item-machine-wrapper">
              <!-- 机器名称 -->
              <span class="menu-item-machine-name auto-ellipsis-item">{{ machine.machineName }}</span>
              <!-- 状态 -->
              <span class="menu-item-machine-status">
                <a-tag :color="machine.status | formatActionStatus('color')">
                  {{ machine.status | formatActionStatus('label') }}
                </a-tag>
              </span>
            </div>
          </a-menu-item>
        </a-menu>
      </div>
      <!-- 机器 -->
      <div class="release-machine-container">
        <div class="release-machine"
             v-for="machine in record.machines"
             v-show="machine.id === selectedKeys[0]"
             :key="machine.id">
          <!-- 进度 -->
          <div class="machine-steps">
            <a-steps :current="current[machine.id]"
                     :status="machine.status | formatActionStatus('stepStatus')">
              <template v-for="action in machine.actions">
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
          <div class="machine-logger-appender">
            <LogAppender :ref="`appender-${machine.id}`"
                         size="default"
                         :height="appenderHeight"
                         :relId="machine.id"
                         :tailType="FILE_TAIL_TYPE.APP_RELEASE_LOG.value"
                         :downloadType="FILE_DOWNLOAD_TYPE.APP_RELEASE_MACHINE_LOG.value">
              <template #left-tools>
                <!-- 命令输入 -->
                <a-input-search class="command-write-input"
                                size="default"
                                v-if="ACTION_STATUS.RUNNABLE.value === machine.status"
                                v-model="command"
                                placeholder="输入"
                                @search="sendCommand(machine.id)">
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
                <a-popconfirm v-if="ACTION_STATUS.RUNNABLE.value === machine.status"
                              title="是否要停止执行?"
                              placement="bottomLeft"
                              ok-text="确定"
                              cancel-text="取消"
                              @confirm="terminateMachine(machine.id)">
                  <a-button icon="close">停止</a-button>
                </a-popconfirm>
                <!-- 跳过 -->
                <a-popconfirm v-if="ACTION_STATUS.WAIT.value === machine.status"
                              title="是否要跳过执行?"
                              placement="bottomLeft"
                              ok-text="确定"
                              cancel-text="取消"
                              @confirm="skipMachine(machine.id)">
                  <a-button icon="stop">跳过</a-button>
                </a-popconfirm>
              </template>
            </LogAppender>
          </div>
        </div>
      </div>
    </div>
  </a-spin>
</template>

<script>
import { enumValueOf, ACTION_STATUS, FILE_DOWNLOAD_TYPE, FILE_TAIL_TYPE, RELEASE_STATUS } from '@/lib/enum'
import LogAppender from '@/components/log/LogAppender'

export default {
  name: 'AppReleaseLogAppender',
  components: { LogAppender },
  props: {
    appenderHeight: String,
    height: String
  },
  computed: {},
  data() {
    return {
      FILE_TAIL_TYPE,
      FILE_DOWNLOAD_TYPE,
      ACTION_STATUS,
      id: null,
      loading: false,
      record: {},
      command: null,
      sendLf: true,
      pollId: null,
      selectedKeys: [],
      openedFiles: [],
      current: {}
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
      await this.$api.getAppReleaseDetail({
        id: id,
        queryAction: 1
      }).then(({ data }) => {
        // 设置数据
        this.record = data
        this.selectedKeys[0] = data.machines[0].id
        this.loading = false
        this.setStepsCurrent()
      }).then(() => {
        // 打开日志
        for (const machine of this.record.machines) {
          if (machine.status !== ACTION_STATUS.WAIT.value &&
            machine.status !== ACTION_STATUS.SKIPPED.value) {
            this.$refs[`appender-${machine.id}`][0].openTail()
            this.openedFiles.push(machine.id)
          }
        }
      }).then(() => {
        // 设置轮询
        if (this.record.status === RELEASE_STATUS.RUNNABLE.value) {
          // 轮询状态
          this.pollId = setInterval(this.pollStatus, 2000)
        }
      }).catch(() => {
        this.loading = false
      })
    },
    close() {
      if (!this.record.machines || !this.record.machines.length) {
        return
      }
      // 关闭tail
      for (const machine of this.record.machines) {
        const appender = this.$refs[`appender-${machine.id}`][0]
        appender.clear()
        appender.dispose()
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
      this.current = {}
    },
    chooseMachineLog(id) {
      setTimeout(() => {
        this.$nextTick(() => this.$refs[`appender-${id}`][0].fitTerminal())
      }, 100)
    },
    terminateMachine(releaseMachineId) {
      this.$api.terminateAppReleaseMachine({
        releaseMachineId: releaseMachineId
      }).then(() => {
        this.$message.success('已停止')
      })
    },
    skipMachine(releaseMachineId) {
      this.$api.skipAppReleaseMachine({
        releaseMachineId: releaseMachineId
      }).then(() => {
        this.$message.success('已跳过')
      })
    },
    sendCommand(releaseMachineId) {
      let command = this.command || ''
      if (this.sendLf) {
        command += '\n'
      }
      if (!command) {
        return
      }
      this.command = ''
      this.$api.writeAppReleaseMachine({
        releaseMachineId: releaseMachineId,
        command
      })
    },
    async pollStatus() {
      if (!this.record.machines || !this.record.machines.length) {
        return
      }
      const pollId = this.record.machines.map(s => s.id)
      this.$api.getAppReleaseMachineListStatus({
        releaseMachineIdList: pollId
      }).then(({ data }) => {
        if (!data || !data.length) {
          return
        }
        const notFinish = data.map(s => s.status).filter(s => s === ACTION_STATUS.WAIT.value ||
          s === ACTION_STATUS.RUNNABLE.value)
        // 清除状态轮询
        if (notFinish.length === 0) {
          clearInterval(this.pollId)
          this.pollId = null
        }
        // 设置机器状态
        for (const machineStatus of data) {
          this.record.machines.filter(s => s.id === machineStatus.id).forEach(s => {
            s.status = machineStatus.status
            // 检查打开tail
            const opened = this.openedFiles.filter(e => e === s.id).length > 0
            if (!opened && s.status !== ACTION_STATUS.WAIT.value &&
              s.status !== ACTION_STATUS.SKIPPED.value) {
              // 打开日志
              this.$refs[`appender-${s.id}`][0].openTail()
              this.openedFiles.push(s.id)
            }
            // 设置action
            if (!machineStatus.actions || !machineStatus.actions.length || !s.actions || !s.actions.length) {
              return
            }
            for (const actionStatus of machineStatus.actions) {
              s.actions.filter(a => a.id === actionStatus.id).forEach(e => {
                e.status = actionStatus.status
                e.used = actionStatus.used
              })
            }
          })
        }
        // 设置当前操作
        this.setStepsCurrent()
      })
    },
    setStepsCurrent() {
      if (!this.record.machines || !this.record.machines.length) {
        return
      }
      for (const machine of this.record.machines) {
        const len = machine.actions.length
        let curr = len - 1
        for (let i = 0; i < len; i++) {
          const status = machine.actions[i].status
          if (status !== ACTION_STATUS.FINISH.value) {
            curr = i
            break
          }
        }
        this.current[machine.id] = curr
      }
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
.app-release-container {
  display: flex;
  background: #F0F2F5;

  .release-machines-menu {
    width: 240px;
    margin: 16px;
    padding: 8px;
    background: #FFFFFF;
    border-radius: 4px;

    ::v-deep .ant-menu-item {
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

  .release-machine-container {
    width: calc(100% - 287px);
    margin: 16px 16px 16px 0;
    background: #FFF;
    border-radius: 4px;

    .machine-steps {
      height: 46px;
      padding: 8px 12px 0 12px;
    }
  }

}
</style>
