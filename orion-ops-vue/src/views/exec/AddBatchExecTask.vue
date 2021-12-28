<template>
  <div>
    <div class="batch-exec-task-container">
      <!-- 添加 -->
      <div class="task-add-container">
        <!-- 执行机器 -->
        <div class="machine-field-container">
          <span class="machine-field-label">执行机器:</span>
          <div class="machine-checker-wrapper">
            <MachineChecker ref="machineChecker"
                            :query="{status: $enum.ENABLE_STATUS.ENABLE.value}">
              <a slot="trigger" :disabled="isRun">已选择 {{ selectedMachines.length }} 台机器</a>
              <a-button type="primary" size="small" slot="footer" @click="chooseMachines">确定</a-button>
            </MachineChecker>
          </div>
        </div>
        <!-- 状态 -->
        <div class="status-container">
          <!-- label -->
          <span>执行命令: </span>
          <!-- status -->
          <a-tag :color="isRun ? '#4C6EF5' : '#40C057'">
            {{ isRun ? '已执行' : '未执行' }}
          </a-tag>
        </div>
        <!-- editor -->
        <div class="editor-container">
          <Editor ref="editor" :value="command" :readOnly="isRun"/>
        </div>
        <!-- 执行描述 -->
        <div class="machine-field-container machine-description-container">
          <span class="machine-field-label">执行描述:</span>
          <div class="machine-field-input">
            <a-input v-model="description"/>
          </div>
        </div>
        <!-- 模板选择 -->
        <div class="exec-buttons">
          <div>
            <a-button class="mr8" type="primary" :disabled="isRun" @click="exec" icon="caret-right">执行</a-button>
            <a-popconfirm placement="top"
                          title="确定要重置当前任务吗?"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="resetTask">
              <a-button type="primary" v-if="isRun" icon="undo">重置</a-button>
            </a-popconfirm>
          </div>
          <div>
            <a-button class="mr8" type="primary" @click="() => {$router.push({path: '/batch/exec/list'})}" icon="rollback">返回列表</a-button>
            <a-button type="primary" :disabled="isRun" @click="openTemplate" icon="unordered-list">模板选择</a-button>
          </div>
        </div>
      </div>
      <!-- 日志 -->
      <div v-if="isRun && execMachines.length" class="task-logger-view-container">
        <a-tabs v-model="activeTab"
                :tabBarStyle="{margin: 0}"
                :hide-add="true"
                type="editable-card"
                @edit="removeTab"
                :animated="false">
          <a-tab-pane v-for="execMachine of execMachines"
                      :key="execMachine.execId"
                      :forceRender="true"
                      :tab="execMachine.machineName">
            <div class="machine-log-view">
              <LogAppender :ref="'appender' + execMachine.execId"
                           :relId="execMachine.execId"
                           :appendStyle="{height: 'calc(100vh - 132px)'}"
                           :config="{type: $enum.FILE_TAIL_TYPE.EXEC_LOG.value, relId: execMachine.execId}">
                <!-- 左侧工具栏 -->
                <div class="appender-left-tools" slot="left-tools">
                  <!-- 命令输入 -->
                  <a-input-search class="command-write-input"
                                  v-if="$enum.BATCH_EXEC_STATUS.RUNNABLE.value === execMachine.status"
                                  v-model="execMachine.inputCommand"
                                  size="small"
                                  placeholder="输入"
                                  @search="sendCommand(execMachine)">
                    <a-icon type="forward" slot="enterButton"/>
                  </a-input-search>
                  <!-- 状态 -->
                  <a-tag class="machine-exec-status" :color="$enum.valueOf($enum.BATCH_EXEC_STATUS, execMachine.status).color">
                    {{ $enum.valueOf($enum.BATCH_EXEC_STATUS, execMachine.status).label }}
                  </a-tag>
                  <!-- used -->
                  <span class="mx8" title="用时"
                        v-if="$enum.BATCH_EXEC_STATUS.COMPLETE.value === execMachine.status">
                    {{ `${execMachine.keepTime} (${execMachine.used}ms)` }}
                  </span>
                  <!-- exitCode -->
                  <span class="mx8" title="退出码"
                        v-if="execMachine.exitCode !== null"
                        :style="{'color': execMachine.exitCode === 0 ? '#4263EB' : '#E03131'}">
                    {{ execMachine.exitCode }}
                  </span>
                  <!-- 终止 -->
                  <a-button class="terminated-button"
                            v-if="$enum.BATCH_EXEC_STATUS.RUNNABLE.value === execMachine.status"
                            size="small"
                            icon="close"
                            @click="terminated(execMachine)">
                    终止
                  </a-button>
                </div>
              </LogAppender>
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>
      <!-- 日志空状态 -->
      <div v-else class="task-logger-view-empty-container">
        <a-empty class="empty-status" description="执行命令以查看日志"/>
      </div>
      <!-- 事件 -->
      <div class="exec-event">
        <TemplateSelector ref="templateSelector" @selected="selectedTemplate"/>
      </div>
    </div>
  </div>
</template>

<script>
import MachineChecker from '@/components/machine/MachineChecker'
import TemplateSelector from '@/components/template/TemplateSelector'
import LogAppender from '@/components/log/LogAppender'
import Editor from '@/components/editor/Editor'

export default {
  name: 'AddBatchExecTask.vue',
  components: {
    MachineChecker,
    TemplateSelector,
    LogAppender,
    Editor
  },
  data() {
    return {
      command: null,
      description: null,
      selectedMachines: [],
      activeTab: 0,
      isRun: false,
      execMachines: [],
      pollId: null
    }
  },
  watch: {
    activeTab(b, a) {
      if (!a) {
        return
      }
      const $refAfter = this.$refs['appender' + a]
      if ($refAfter) {
        $refAfter[0].storeScroll()
      }
      const $refBefore = this.$refs['appender' + b]
      if ($refBefore) {
        $refBefore[0].toScroll()
      }
    }
  },
  methods: {
    chooseMachines() {
      const ref = this.$refs.machineChecker
      if (!ref.checkedList.length) {
        this.$message.warning('请选择执行的机器')
        return
      }
      this.selectedMachines = ref.checkedList
      ref.hide()
    },
    exec() {
      if (!this.selectedMachines.length) {
        this.$message.warn('请选择执行机器')
        return
      }
      const command = this.$refs.editor.getValue()
      if (!command.trim().length) {
        this.$message.warn('请输入执行命令')
        return
      }
      this.isRun = true
      this.$api.submitExecTask({
        machineIdList: this.selectedMachines,
        command: command,
        description: this.description
      }).then(({ data }) => {
        this.activeTab = data[0].execId
        for (const exec of data) {
          exec.inputCommand = null
          exec.status = this.$enum.BATCH_EXEC_STATUS.WAITING.value
          exec.exitCode = null
          exec.used = null
        }
        this.execMachines = data
        // 轮询状态
        this.pollId = setInterval(this.pollExecStatus, 1000)
      }).then(() => {
        // 打开日志
        this.execMachines.forEach(m => {
          this.$refs['appender' + m.execId][0].openTail()
        })
      }).catch(() => {
        this.isRun = false
      })
    },
    resetTask() {
      // 关闭轮询
      if (this.pollId) {
        clearInterval(this.pollId)
      }
      // 关闭日志
      this.execMachines.forEach(m => {
        this.$refs['appender' + m.execId][0].close()
      })
      this.activeTab = 0
      this.isRun = false
      this.execMachines = []
    },
    pollExecStatus() {
      const idList = this.execMachines.filter(s => {
        return s.status === this.$enum.BATCH_EXEC_STATUS.WAITING.value ||
          s.status === this.$enum.BATCH_EXEC_STATUS.RUNNABLE.value
      }).map(s => s.execId)
      if (!idList.length) {
        return
      }
      this.$api.getExecTaskStatus({
        idList
      }).then(({ data }) => {
        for (const status of data) {
          this.execMachines.filter(s => s.execId === status.id).forEach(s => {
            s.status = status.status
            s.exitCode = status.exitCode
            s.used = status.used
            s.keepTime = status.keepTime
          })
        }
      })
    },
    openTemplate() {
      this.$refs.templateSelector.open()
    },
    selectedTemplate(command) {
      this.command = command
      this.$refs.templateSelector.close()
    },
    sendCommand(execMachine) {
      const id = execMachine.execId
      const command = execMachine.inputCommand
      if (!command) {
        return
      }
      this.$api.writeExecTask({
        id,
        command
      }).then(() => {
        execMachine.inputCommand = null
      })
    },
    terminated(execMachine) {
      execMachine.status = this.$enum.BATCH_EXEC_STATUS.TERMINATED.value
      this.$api.terminatedExecTask({
        id: execMachine.execId
      }).then(() => {
        this.$message.success('已终止')
      })
    },
    removeTab(targetTab) {
      let activeTab = this.activeTab
      let lastIndex
      this.execMachines.forEach((appenderTab, i) => {
        if (appenderTab.execId === targetTab) {
          lastIndex = i - 1
        }
      })
      const $ref = this.$refs['appender' + targetTab]
      if ($ref && $ref.length) {
        $ref[0].close()
      }
      const execMachines = this.execMachines.filter(tailFile => tailFile.execId !== targetTab)
      if (execMachines.length && activeTab === targetTab) {
        if (lastIndex >= 0) {
          activeTab = execMachines[lastIndex].execId
        } else {
          activeTab = execMachines[0].execId
        }
      }
      if (!execMachines.length) {
        activeTab = null
      }
      this.execMachines = execMachines
      this.activeTab = activeTab
    }
  }
}
</script>

<style lang="less" scoped>

.batch-exec-task-container {
  display: flex;
  padding: 16px;
  background-color: #F0F2F5;
  height: 100vh;

  .task-add-container {
    width: 40%;
    margin-right: 1%;
    padding: 16px;
    border-radius: 4px;
    background-color: #FFF;

    .machine-field-container {
      display: flex;

      .machine-field-label {
        font-weight: 600;
        width: 65px;
        margin: 4px 8px;
      }

      .machine-checker-wrapper {
        display: flex;
        align-items: center;
      }

      .machine-field-input {
        width: calc(100% - 60px);
        padding-right: 8px;
      }
    }

    .status-container {
      display: flex;
      margin: 8px 0 0 8px;
      justify-content: space-between;
      align-items: center;
    }

    .editor-container {
      height: 70%;
      padding: 8px;
    }

    .machine-description-container {
      margin-bottom: 8px;
    }

    .exec-buttons {
      display: flex;
      justify-content: space-between;
      margin: 0 8px 8px 8px;
    }

  }

  .task-logger-view-container, .task-logger-view-empty-container {
    width: 59%;
    border-radius: 4px;
    background-color: #FFF;
  }

  .task-logger-view-container {
    padding: 8px 16px 16px 16px;;

    .machine-log-view {
      height: calc(100vh - 96px);
    }
  }

  .task-logger-view-empty-container {
    padding: 16px;
    display: flex;
    justify-content: center;
    align-items: center;

    .empty-status {
      margin-bottom: 20%;
    }
  }

  .appender-left-tools {
    display: flex;
    align-items: center;

    .command-write-input {
      margin-right: 8px;
      width: 65%;
    }
  }

}

</style>
