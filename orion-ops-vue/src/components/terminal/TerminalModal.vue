<template>
  <div class="terminal-modal-container">
    <a-modal v-model="visible"
             v-drag-modal
             :closable="false"
             :footer="null"
             :keyboard="false"
             :maskClosable="false"
             :forceRender="true"
             :dialogStyle="{top: '32px', padding: 0}"
             :bodyStyle="{padding: 0}"
             @cancel="close"
             width="75%">
      <!-- 标题 -->
      <template #title>
        <div class="terminal-title-wrapper">
          <!-- 左侧 -->
          <div class="title-left-fixed">
            <!-- ssh信息 -->
            <div class="terminal-ssh">
              <span v-if="machine.username">
                <a-tooltip placement="right" title="复制ssh命令">
                  <span class="pointer" @click="copySshCommand">{{ machine.username }}@</span>
                </a-tooltip>
                <a-tooltip placement="right" title="复制ip">
                  <span class="pointer" @click="$copy(machine.host, true)">{{ machine.host }}:{{ machine.sshPort }} ({{ machine.name }})</span>
                </a-tooltip>
              </span>
            </div>
          </div>
          <!-- 右侧 -->
          <div class="title-right-fixed">
            <!-- 状态 -->
            <a-popconfirm v-if="status === TERMINAL_STATUS.DISCONNECTED.value"
                          placement="bottom"
                          title="确认重新连接?"
                          ok-text="确认"
                          cancel-text="取消"
                          @confirm="reOpen">
              <a-badge :count="statusLabel" :numberStyle="statusStyle"/>
            </a-popconfirm>
            <a-badge v-else :count="statusLabel" :numberStyle="statusStyle"/>
            <a-icon class="title-right-item ant-modal-draggable draggable-trigger" type="border-right" title="拖拽"/>
            <a-icon class="title-right-item setting-trigger" type="setting" title="设置" @click="openSetting"/>
            <a-icon v-if="visibleMinimize" class="title-right-item min-size-trigger" type="shrink" title="最小化" @click="minimize"/>
            <a-icon class="title-right-item" type="close" title="关闭" @click="close"/>
          </div>
        </div>
      </template>
      <!-- 终端 -->
      <div class="terminal-wrapper">
        <TerminalBody ref="terminal" @changeStatus="onchangeStatus"/>
      </div>
    </a-modal>
    <!-- 设置模态框 -->
    <TerminalSettingModal ref="settingModal" :machineId="machineId"/>
  </div>
</template>

<script>
import { getSshCommand } from '@/lib/utils'
import { enumValueOf, TERMINAL_STATUS } from '@/lib/enum'
import TerminalBody from '@/components/terminal/TerminalBody'
import TerminalSettingModal from '@/components/terminal/TerminalSettingModal'

export default {
  name: 'TerminalModal',
  components: {
    TerminalSettingModal,
    TerminalBody
  },
  props: {
    visibleMinimize: Boolean
  },
  data() {
    return {
      visible: false,
      machineId: null,
      terminalId: null,
      TERMINAL_STATUS,
      machine: {},
      status: TERMINAL_STATUS.NOT_CONNECT.value
    }
  },
  computed: {
    statusLabel: function() {
      return enumValueOf(TERMINAL_STATUS, this.status).label
    },
    statusStyle: function() {
      return {
        backgroundColor: enumValueOf(TERMINAL_STATUS, this.status).color,
        cursor: this.status === TERMINAL_STATUS.DISCONNECTED.value ? 'pointer' : 'default',
        'user-select': 'none',
        'margin-right': '16px'
      }
    }
  },
  methods: {
    open(machine, terminalId) {
      this.machine = machine
      this.machineId = machine.id
      this.terminalId = terminalId
      const loading = this.$message.loading('建立连接中...')
      // 获取访问数据
      this.$api.accessTerminal({
        machineId: machine.id
      }).then(({ data }) => {
        // 初始化
        this.visible = true
        loading()
        this.$nextTick(() => {
          this.$refs.terminal.init(data)
        })
        this.$emit('open', this.terminalId)
      }).catch(e => {
        loading()
        this.$message.error(e.msg || '初始化失败')
      })
    },
    reOpen() {
      this.status = TERMINAL_STATUS.NOT_CONNECT.value
      const loading = this.$message.loading('正在重新连接...')
      // 获取访问数据
      this.$api.accessTerminal({
        machineId: this.machine.id
      }).then(({ data }) => {
        loading()
        this.$nextTick(() => {
          this.$refs.terminal.dispose()
          this.$refs.terminal.init(data)
        })
      }).catch(e => {
        loading()
        this.$message.error(e.msg || '连接失败')
      })
    },
    close() {
      this.visible = false
      this.machineId = null
      this.status = TERMINAL_STATUS.NOT_CONNECT.value
      try {
        this.$refs.terminal.dispose();
      } catch (e) {
      }
      this.$emit('close', this.terminalId)
    },
    onchangeStatus(status) {
      this.status = status
    },
    minimize() {
      this.visible = false
      this.$emit('minimize', {
        terminalId: this.terminalId,
        name: this.machine.name,
        host: this.machine.host
      })
    },
    maximize() {
      this.visible = true
      // fit
      setTimeout(() => {
        this.$refs.terminal.fitTerminal()
        this.$refs.terminal.focus()
      }, 250)
    },
    openSetting() {
      this.$refs.settingModal.openSetting()
    },
    copySshCommand() {
      const command = getSshCommand(this.machine.username, this.machine.host, this.machine.sshPort)
      this.$copy(command, true)
    }
  }
}
</script>

<style lang="less" scoped>

.terminal-title-wrapper {
  display: flex;
  justify-content: space-between;
  font-size: 14px;

  .title-right-fixed {
    display: flex;
    align-items: center;
    font-size: 18px;
    text-align: end;

    .min-size-trigger, .setting-trigger, .draggable-trigger {
      margin-right: 12px;
    }

    .title-right-item {
      transition: color 0.3s;
      cursor: pointer;
    }

    .title-right-item:hover {
      color: #1890FF;
    }
  }
}

.terminal-wrapper {
  height: calc(100vh - 102px);
}

::v-deep .ant-modal-header {
  padding: 8px 10px 8px 8px;
  border-radius: 2px 2px 0 0;
}

</style>
