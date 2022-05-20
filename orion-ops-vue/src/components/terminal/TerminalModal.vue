<template>
  <div class="terminal-modal-container">
    <a-modal v-model="visible"
             v-drag-modal
             :closable="false"
             :footer="null"
             :keyboard="false"
             :maskClosable="false"
             :forceRender="true"
             :dialogStyle="{top: '16px', padding: 0}"
             :bodyStyle="{padding: '4px'}"
             @cancel="close"
             width="80%">
      <!-- 标题 -->
      <template #title>
        <div class="terminal-wrapper-title-wrapper">
          <!-- 左侧 -->
          <div class="title-left-fixed">
            <!-- ssh信息 -->
            <div class="terminal-ssh">
              <span v-if="machine.username">
                <span title="复制ssh" @click="copySshCommand">{{ machine.username }}@</span>
                <span title="复制ip" @click="copyHost">{{ machine.host }}:{{ machine.sshPort }}</span>
              </span>
            </div>
          </div>
          <!-- 右侧 -->
          <div class="title-right-fixed">
            <a-icon class="title-right-item setting-trigger" type="setting" title="设置" @click="openSetting"/>
            <a-icon class="title-right-item min-size-trigger" type="shrink" title="最小化" @click="minimize"/>
            <a-icon class="title-right-item" type="close" title="关闭" @click="close"/>
          </div>
        </div>
      </template>
      <!-- 终端 -->
      <div class="terminal-wrapper">
        <TerminalXterm v-if="machineId"
                       ref="terminal"
                       wrapperHeight="100%"
                       terminalHeight="100%"
                       :machineId="machineId"
                       :visibleHeader="false"/>
      </div>
    </a-modal>
    <TerminalSettingModal ref="settingModal" :machineId="machineId"/>
  </div>
</template>

<script>
import TerminalXterm from '@/components/terminal/TerminalXterm'
import TerminalSettingModal from '@/components/terminal/TerminalSettingModal'

export default {
  name: 'TerminalModal',
  components: {
    TerminalSettingModal,
    TerminalXterm
  },
  data() {
    return {
      visible: false,
      machineId: null,
      symbol: null,
      machine: {}
    }
  },
  methods: {
    open(machine, symbol) {
      this.symbol = symbol
      this.machine = machine
      this.visible = true
      this.$nextTick(() => {
        this.machineId = machine.id
        this.$emit('open', this.symbol)
      })
    },
    close() {
      this.visible = false
      this.$refs.terminal.dispose()
      this.$refs.terminal.disconnect()
      this.machineId = null
      this.$emit('close', this.symbol)
    },
    minimize() {
      this.visible = false
      this.$emit('minimize', {
        symbol: this.symbol,
        name: this.machine.name,
        host: this.machine.host
      })
    },
    maximize() {
      this.visible = true
      setTimeout(() => {
        // fit 两次可能会超出modal不然有问题
        this.$refs.terminal.fitTerminal()
        this.$refs.terminal.fitTerminal()
        this.$refs.terminal.focus()
      }, 450)
    },
    openSetting() {
      this.$refs.settingModal.openSetting()
    },
    copySshCommand() {
      const command = this.$utils.getSshCommand(this.machine.username, this.machine.host, this.machine.sshPort)
      this.$message.success(`${command} 已复制`)
      this.$utils.copyToClipboard(command)
    },
    copyHost() {
      this.$message.success(`${this.machine.host} 已复制`)
      this.$utils.copyToClipboard(this.machine.host)
    }
  }
}
</script>

<style lang="less" scoped>

/deep/ .ant-modal-header {
  padding: 12px 16px 12px 8px;
}

.terminal-wrapper-title-wrapper {
  display: flex;
  justify-content: space-between;
  font-weight: 400;

  .terminal-ssh {
    font-size: 15px;
    cursor: pointer;
    color: #364FC7;
  }

  .title-right-fixed {
    font-size: 20px;
    color: rgba(0, 0, 0, .45);
    text-align: end;

    .min-size-trigger, .setting-trigger {
      margin-right: 16px;
    }

    .title-right-item {
      transition: color 0.3s;
      cursor: pointer;
    }

    .title-right-item:hover {
      color: #000000;
    }
  }
}

.terminal-wrapper {
  height: calc(100vh - 86px);
}

</style>
