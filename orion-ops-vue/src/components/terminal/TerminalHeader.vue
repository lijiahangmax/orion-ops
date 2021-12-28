<template>
  <div class="terminal-header-container">
    <div class="terminal-header-main">
      <!-- 左侧菜单 -->
      <div class="terminal-header-fixed-left">
        <!-- ssh信息 -->
        <div class="terminal-ssh">
          <span v-if="machine.username">
            <span title="复制ssh" @click="copySshCommand">{{ machine.username }}@</span>
            <span title="复制ip" @click="copyHost">{{ machine.host }}:{{ machine.port }}</span>
          </span>
        </div>
        <!-- 命令输入框 -->
        <div class="terminal-command-input-wrapper">
          <a-input-search placeholder="command"
                          :disabled="machine.status !== $enum.TERMINAL_STATUS.CONNECTED.value"
                          v-model="commandInput"
                          @search="inputCommand">
            <a-icon type="forward" slot="enterButton"/>
          </a-input-search>
        </div>
      </div>
      <!-- 右侧菜单 -->
      <div class="terminal-header-fixed-right">
        <!-- 状态 -->
        <a-popconfirm placement="bottom" ok-text="确认" cancel-text="取消" @confirm="confirmStatus">
          <template slot="title">
            <p v-if="machine.status === $enum.TERMINAL_STATUS.CONNECTED.value">确认断开?</p>
            <p v-else>确认重新连接?</p>
          </template>
          <a-badge :count="this.statusLabel" :number-style="{
                backgroundColor: this.statusColor,
                cursor: 'pointer',
                boxShadow: '0 0 0 1px #D9D9D9 inset',
                'margin-right': '15px'}"/>
        </a-popconfirm>
        <!-- 提示 -->
        <a-popover placement="bottom">
          <div slot="content" style="font-size: 15px; line-height: 26px">
            <span>ctrl + insert (复制)</span><br>
            <span>shift + insert (粘贴)</span><br>
            <span>ctrl + shift + v (粘贴)</span><br>
            <span>ctrl + shift + f (搜索)</span><br>
            <span>shift + 鼠标滚轮 (快速移动)</span><br>
            <span>alt + 鼠标左键 (批量选择)</span>
          </div>
          <a-icon class="trigger-icon" type="question-circle"/>
        </a-popover>
        <!-- 全屏 -->
        <a-icon id="terminal-screen-icon" class="trigger-icon"
                :type="isFullScreen ? 'fullscreen-exit' : 'fullscreen'"
                :title="isFullScreen ? '退出全屏' : '全屏'"
                @click="fullscreen"/>
        <!-- 设置 -->
        <a-icon id="terminal-setting-icon" class="trigger-icon" title="设置" type="setting" @click="openSetting"/>
        <!-- sftp -->
        <a-button id="sftp-trigger" :disabled="machine.status !== $enum.TERMINAL_STATUS.CONNECTED.value" type="primary" @click="openSftp">文件管理器</a-button>
      </div>
    </div>
    <!-- 事件 -->
    <div class="terminal-header-event-container">
      <!-- 设置模态框 -->
      <div id="terminal-settings-modal">
        <TerminalSettingModal ref="settingModal" :machineId="machineId"/>
      </div>
    </div>
  </div>
</template>

<script>
import TerminalSettingModal from './TerminalSettingModal'

export default {
  name: 'TerminalHeader',
  props: {
    machineId: Number,
    machine: Object
  },
  components: {
    TerminalSettingModal
  },
  data: function() {
    return {
      commandInput: '',
      isFullScreen: false
    }
  },
  computed: {
    statusLabel: function() {
      return this.$enum.valueOf(this.$enum.TERMINAL_STATUS, this.machine.status).label
    },
    statusColor: function() {
      return this.$enum.valueOf(this.$enum.TERMINAL_STATUS, this.machine.status).color
    }
  },
  methods: {
    copySshCommand() {
      const command = this.$utils.getSshCommand(this.machine.username, this.machine.host, this.machine.port)
      this.$message.success(`${command} 已复制`)
      this.$utils.copyToClipboard(command)
    },
    copyHost() {
      this.$message.success(`${this.machine.host} 已复制`)
      this.$utils.copyToClipboard(this.machine.host)
    },
    inputCommand() {
      this.$emit('inputCommand', this.commandInput)
      this.commandInput = ''
    },
    confirmStatus() {
      if (this.machine.status === this.$enum.TERMINAL_STATUS.CONNECTED.value) {
        this.$emit('disconnect')
      } else {
        this.$emit('reload')
      }
    },
    fullscreen() {
      if (this.isFullScreen) {
        this.$utils.exitFullScreen()
      } else {
        this.$utils.fullScreen()
      }
      this.isFullScreen = !this.isFullScreen
    },
    openSftp() {
      this.$emit('openSftp')
    },
    openSetting() {
      this.$refs.settingModal.openSetting()
    }
  }
}
</script>

<style lang="less" scoped>

.terminal-header-main {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #E9ECEF;

  .terminal-header-fixed-left {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .terminal-ssh {
      min-width: 160px;
      margin: 0 16px;
      cursor: pointer;
      color: #364FC7;
    }

    .terminal-command-input-wrapper {
      width: 400px;
    }
  }

  .terminal-header-fixed-right {
    display: flex;
    align-items: center;

    #sftp-trigger {
      margin-right: 15px
    }

    .trigger-icon {
      cursor: pointer;
      font-size: 22px;
      margin-right: 15px;
      transition: color 0.3s;
    }

    .trigger-icon:hover {
      color: #1C7ED6;
    }
  }

}

</style>
