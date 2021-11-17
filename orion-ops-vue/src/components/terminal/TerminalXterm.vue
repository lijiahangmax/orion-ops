<template>
  <div id="terminal-container">
    <!-- 头部 -->
    <TerminalHeader :machineId="machineId"
                    :machine="machine"
                    @inputCommand='inputCommand'
                    @reload='reload'
                    @disconnect='disconnect'
                    @openSftp="openSftp"/>
    <!-- terminal主体 -->
    <TerminalMain ref="terminalMain"
                  :machineId="machineId"
                  @closeLoading="closeLoading"
                  @terminalStatusChange="terminalStatusChange"/>
    <!-- sftp侧栏 -->
    <MachineSftpDrawer ref="machineSftpDrawer" :machineId="machineId"/>
  </div>
</template>

<script>
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
    machineId: null
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
    closeLoading() {
      if (this.loading) {
        this.loading()
      }
    },
    inputCommand(e) {
      this.$refs.terminalMain.writerCommand(e)
    },
    dispose() {
      this.$refs.terminalMain.term && this.$refs.terminalMain.term.dispose()
      this.$refs.terminalMain.plugin.fit && this.$refs.terminalMain.plugin.fit.dispose()
      this.$refs.terminalMain.plugin.search && this.$refs.terminalMain.plugin.search.dispose()
      this.$refs.terminalMain.plugin.links && this.$refs.terminalMain.plugin.links.dispose()
      this.$refs.terminalMain.plugin.webgl && this.$refs.terminalMain.plugin.webgl.dispose()
    },
    reload() {
      this.dispose()
      this.getAccessToken()
    },
    disconnect(e) {
      this.$refs.terminalMain.disconnect(e)
    },
    terminalStatusChange(status) {
      this.machine.status = status
    },
    openSftp() {
      this.$refs.machineSftpDrawer.visible = true
    },
    async getAccessToken() {
      this.loading = this.$message.loading('建立连接中...', 10)
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
          enableWebLink: data.enableWebLink,
          enableWebGL: data.enableWebGL
        }
        // 初始化terminal
        this.$refs.terminalMain.initTerminal(options, setting)
      } catch (e) {
        this.loading()
        this.$message.error('初始化失败')
        this.machine.status = this.$enum.TERMINAL_STATUS.ERROR.value
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
