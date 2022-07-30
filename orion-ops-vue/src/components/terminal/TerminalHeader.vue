<template>
  <div class="terminal-header-container">
    <div class="terminal-header-main">
      <!-- 左侧菜单 -->
      <div class="terminal-header-fixed-left">
        <!-- ssh信息 -->
        <div class="terminal-ssh">
          <span v-if="machine.username">
            <a-tooltip title="复制ssh">
              <span @click="copySshCommand">{{ machine.username }}@</span>
            </a-tooltip>
            <a-tooltip title="复制ip">
              <span @click="copyHost">{{ machine.host }}:{{ machine.port }}</span>
            </a-tooltip>
          </span>
        </div>
        <!-- 命令输入框 -->
        <div class="terminal-command-input-wrapper">
          <a-input-search placeholder="command"
                          :disabled="machine.status !== TERMINAL_STATUS.CONNECTED.value"
                          v-model="commandInput"
                          @search="sendCommand">
            <template #enterButton>
              <a-icon type="forward"/>
            </template>
          </a-input-search>
        </div>
      </div>
      <!-- 右侧菜单 -->
      <div class="terminal-header-fixed-right">
        <!-- 状态 -->
        <a-popconfirm placement="bottom" ok-text="确认" cancel-text="取消" @confirm="confirmStatus">
          <template #title>
            <p v-if="machine.status === TERMINAL_STATUS.CONNECTED.value">确认断开?</p>
            <p v-else>确认重新连接?</p>
          </template>
          <a-badge :count="statusLabel" :numberStyle="statusStyle"/>
        </a-popconfirm>
        <!-- 提示 -->
        <a-popover placement="bottom">
          <template #content>
            <div style="font-size: 15px; line-height: 26px">
              <span>ctrl + insert (复制)</span><br>
              <span>ctrl + shift + c (复制)</span><br>
              <span>shift + insert (粘贴)</span><br>
              <span>ctrl + shift + v (粘贴)</span><br>
              <span>ctrl + shift + f (搜索)</span><br>
              <span>shift + 鼠标滚轮 (快速移动)</span><br>
              <span>alt + 鼠标左键 (批量选择)</span>
            </div>
          </template>
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
        <a-button id="sftp-trigger" :disabled="machine.status !== TERMINAL_STATUS.CONNECTED.value" type="primary" @click="openSftp">文件管理器</a-button>
      </div>
    </div>
    <!-- 设置模态框 -->
    <TerminalSettingModal ref="settingModal" :machineId="machineId"/>
  </div>
</template>

<script>
import { exitFullScreen, fullScreen, getSshCommand } from '@/lib/utils'
import { enumValueOf, TERMINAL_STATUS } from '@/lib/enum'
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
      TERMINAL_STATUS,
      commandInput: '',
      isFullScreen: false
    }
  },
  computed: {
    statusLabel: function() {
      return enumValueOf(TERMINAL_STATUS, this.machine.status).label
    },
    statusStyle: function() {
      return {
        backgroundColor: enumValueOf(TERMINAL_STATUS, this.machine.status).color,
        cursor: 'pointer',
        'user-select': 'none',
        'margin-right': '16px'
      }
    }
  },
  methods: {
    copySshCommand() {
      const command = getSshCommand(this.machine.username, this.machine.host, this.machine.port)
      this.$copy(command, true)
    },
    copyHost() {
      this.$copy(this.machine.host, true)
    },
    sendCommand() {
      this.$emit('sendCommand', this.commandInput)
      this.commandInput = ''
    },
    confirmStatus() {
      if (this.machine.status === TERMINAL_STATUS.CONNECTED.value) {
        this.$emit('dispose')
      } else {
        this.$emit('reload')
      }
    },
    fullscreen() {
      if (this.isFullScreen) {
        exitFullScreen()
      } else {
        fullScreen()
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
  background: #E9ECEF;

  .terminal-header-fixed-left {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .terminal-ssh {
      max-width: 160px;
      margin: 0 16px 0 8px;
      cursor: pointer;
      color: rgba(0, 0, 0, .85);
      font-weight: 500;
      font-size: 15px;
    }

    .terminal-command-input-wrapper {
      width: 400px;
    }
  }

  .terminal-header-fixed-right {
    display: flex;
    align-items: center;

    #sftp-trigger {
      margin-right: 8px
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
