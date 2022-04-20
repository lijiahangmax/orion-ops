<template>
  <a-spin :spinning="loading">
    <div class="app-release-container" :style="{height}">
      <!-- 菜单 -->
      <div class="release-machines-menu">
        <a-menu mode="inline" v-model="selectedKeys">
          <a-menu-item v-for="machine in record.machines" :key="machine.id" :title="machine.machineName">
            <div class="menu-item-machine-wrapper">
              <!-- 机器名称 -->
              <span class="menu-item-machine-name auto-ellipsis-item">{{ machine.machineName }}</span>
              <!-- 状态 -->
              <span class="menu-item-machine-status">
                <a-tag :color="$enum.valueOf($enum.ACTION_STATUS, machine.status).color">
                  {{ $enum.valueOf($enum.ACTION_STATUS, machine.status).label }}
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
                     :status="$enum.valueOf($enum.ACTION_STATUS, machine.status).stepStatus">
              <template v-for="action in machine.actions">
                <a-step :key="action.id"
                        :title="action.actionName"
                        :subTitle="action.used ? `${action.used}ms` : ''">
                  <template v-if="action.status === $enum.ACTION_STATUS.RUNNABLE.value" #icon>
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
                         :appendStyle="{height: appenderHeight}"
                         :relId="machine.id"
                         :tailType="$enum.FILE_TAIL_TYPE.APP_RELEASE_LOG.value"
                         :downloadType="$enum.FILE_DOWNLOAD_TYPE.APP_RELEASE_MACHINE_LOG.value"
                         :rightMenuX="e => e.offsetX + 298">
              <template #left-tools>
                <!-- 停止 -->
                <a-popconfirm v-if="$enum.ACTION_STATUS.RUNNABLE.value === machine.status"
                              title="是否要停止执行?"
                              placement="bottomLeft"
                              ok-text="确定"
                              cancel-text="取消"
                              @confirm="terminateMachine(machine.id)">
                  <a-button icon="close">停止</a-button>
                </a-popconfirm>
                <!-- 跳过 -->
                <a-popconfirm v-if="$enum.ACTION_STATUS.WAIT.value === machine.status"
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

import LogAppender from '@/components/log/LogAppender'

export default {
  name: 'AppReleaseLogAppender',
  components: { LogAppender },
  props: {
    appenderHeight: {
      type: String,
      default: 'calc(100vh - 156px)'
    },
    height: String
  },
  computed: {},
  data() {
    return {
      id: null,
      loading: false,
      record: {},
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
          if (machine.status !== this.$enum.ACTION_STATUS.WAIT.value &&
            machine.status !== this.$enum.ACTION_STATUS.SKIPPED.value) {
            this.$refs[`appender-${machine.id}`][0].openTail()
            this.openedFiles.push(machine.id)
          }
        }
      }).then(() => {
        // 设置轮询
        if (this.record.status === this.$enum.RELEASE_STATUS.RUNNABLE.value) {
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
      this.current = {}
    },
    terminateMachine(releaseMachineId) {
      this.$api.terminateAppReleaseMachine({
        id: this.record.id,
        releaseMachineId: releaseMachineId
      }).then(() => {
        this.$message.success('已停止')
      })
    },
    skipMachine(releaseMachineId) {
      this.$api.skipAppReleaseMachine({
        id: this.record.id,
        releaseMachineId: releaseMachineId
      }).then(() => {
        this.$message.success('已跳过')
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
        const notFinish = data.map(s => s.status).filter(s => s === this.$enum.BUILD_STATUS.WAIT.value ||
          s === this.$enum.BUILD_STATUS.RUNNABLE.value)
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
            if (!opened && s.status !== this.$enum.ACTION_STATUS.WAIT.value &&
              s.status !== this.$enum.ACTION_STATUS.SKIPPED.value) {
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
          if (status !== this.$enum.ACTION_STATUS.FINISH.value) {
            curr = i
            break
          }
        }
        this.current[machine.id] = curr
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
.app-release-container {
  display: flex;
  background: #F0F2F5;

  .release-machines-menu {
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

  .release-machine-container {
    width: calc(100% - 287px);
    margin: 16px 16px 16px 0;
    background: #FFF;
    padding: 8px 16px 16px 16px;
    border-radius: 4px;

    .machine-steps {
      height: 56px;
      padding: 8px 0 12px 0;
    }
  }

}
</style>
