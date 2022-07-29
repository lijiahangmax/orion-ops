<template>
  <div class="terminal-container" :style="{height: wrapperHeight}">
    <!-- 头部 -->
    <TerminalHeader v-if="visibleHeader"
                    :machineId="machineId"
                    :machine="machine"
                    @inputCommand='inputCommand'
                    @reload='reload'
                    @disconnect='disconnect'
                    @openSftp="openSftp"/>
    <!-- terminal 主体 -->
    <TerminalMain ref="terminalMain"
                  :machineId="machineId"
                  :terminalHeight="terminalHeight"
                  :isModal="isModal"
                  @initFinish="initFinish"
                  @terminalStatusChange="terminalStatusChange"/>
    <!-- sftp 侧栏 -->
    <MachineSftpDrawer v-if="!isModal"
                       ref="machineSftpDrawer"
                       :machineId="machineId"
                       :machineName="machine.machineName"/>
  </div>
</template>

<script>
import { TERMINAL_STATUS } from '@/lib/enum'
import TerminalHeader from '@/components/terminal/TerminalHeader'
import TerminalMain from '@/components/terminal/TerminalMain'
import MachineSftpDrawer from '@/components/sftp/MachineSftpDrawer'

export default {
  name: 'TerminalXterm',
  components: {
    TerminalHeader,
    TerminalMain,
    MachineSftpDrawer
  },
  props: {
    machineId: Number,
    wrapperHeight: String,
    terminalHeight: String,
    visibleHeader: Boolean,
    isModal: Boolean
  },
  data: function() {
    return {
      loading: null,
      machine: {
        status: 0,
        machineId: null,
        host: null,
        port: null,
        username: null,
        machineName: null
      }
    }
  },
  methods: {
    initFinish(r) {
      if (this.loading) {
        this.loading()
      }
      this.$emit('initFinish', r)
    },
    inputCommand(e) {
      this.$refs.terminalMain.writerCommand(e)
    },
    dispose() {
      this.$refs.terminalMain.term && this.$refs.terminalMain.term.dispose()
      this.$refs.terminalMain.plugin.search && this.$refs.terminalMain.plugin.search.dispose()
      this.$refs.terminalMain.plugin.links && this.$refs.terminalMain.plugin.links.dispose()
    },
    reload() {
      this.dispose()
      this.disconnect()
      this.getAccessToken()
    },
    disconnect() {
      this.$refs.terminalMain && this.$refs.terminalMain.disconnect()
    },
    terminalStatusChange(status) {
      this.machine.status = status
    },
    openSftp() {
      this.$refs.machineSftpDrawer.visible = true
    },
    fitTerminal() {
      this.$refs.terminalMain.fitTerminal()
    },
    toTop() {
      this.$refs.terminalMain.term && this.$refs.terminalMain.term.scrollToTop()
    },
    toBottom() {
      this.$refs.terminalMain.term && this.$refs.terminalMain.term.scrollToBottom()
    },
    focus() {
      this.$refs.terminalMain.term && this.$refs.terminalMain.term.focus()
    },
    async getAccessToken() {
      this.loading = this.$message.loading('建立连接中...')
      try {
        const { data } = await this.$api.accessTerminal({ machineId: this.machineId })
        // 初始化数据
        this.machine.host = data.host
        this.machine.port = data.port
        this.machine.username = data.username
        this.machine.machineName = data.machineName
        // terminal选项
        const options = {
          backgroundColor: data.backgroundColor,
          fontColor: data.fontColor,
          fontSize: data.fontSize,
          fontFamily: data.fontFamily
        }
        // terminal设置
        const setting = {
          accessToken: data.accessToken,
          enableWebLink: data.enableWebLink
        }
        // 初始化terminal
        this.$refs.terminalMain.init(options, setting)
      } catch (e) {
        this.loading()
        this.$message.error(e.msg || '初始化失败')
        this.machine.status = TERMINAL_STATUS.ERROR.value
      }
    }
  },
  mounted() {
    if (!this.machineId) {
      this.$message.error('参数错误')
      return
    }
    this.machine.machineId = this.machineId
    this.getAccessToken()
  }
}
</script>

<style scoped>

</style>
