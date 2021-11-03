<template>
  <div id="terminal-container">
    <!-- 头部 -->
    <a-affix :offset-top="0">
      <TerminalHeader :machineId="machineId"
                      :machine="machine"
                      @inputCommand='inputCommand'
                      @disconnect='disconnect'
                      @openSftp="openSftp"/>
    </a-affix>
    <!-- terminal主体 -->
    <TerminalMain ref="terminalMain"
                  :machineId="machineId"
                  @closeLoading="closeLoading"
                  @terminalStatusChange="terminalStatusChange"/>
    <!-- sftp侧栏 -->
    <MachineSftp ref="machineSftp" :machineId="machineId"/>
  </div>
</template>

<script>
import TerminalHeader from '@/components/terminal/TerminalHeader'
import TerminalMain from '@/components/terminal/TerminalMain'
import MachineSftp from '@/components/sftp/MachineSftp'

export default {
  name: 'MachineTerminal',
  components: {
    TerminalHeader,
    TerminalMain,
    MachineSftp
  },
  data: function() {
    return {
      machineId: null,
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
    disconnect(e) {
      this.$refs.terminalMain.disconnect(e)
    },
    terminalStatusChange(status) {
      this.machine.status = status
    },
    openSftp() {
      this.$refs.machineSftp.visible = true
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
        document.title = `${data.machineName} | ${data.host}`

        // 初始化terminal
        const options = {
          backgroundColor: data.backgroundColor,
          fontColor: data.fontColor,
          fontSize: data.fontSize
        }
        const setting = {
          accessToken: data.accessToken,
          enableWebLink: data.enableWebLink,
          enableWebGL: data.enableWebGL
        }
        this.$refs.terminalMain.initTerminal(options, setting)
      } catch (e) {
        this.loading()
        this.$message.error('初始化失败')
        this.machine.status = this.$enum.TERMINAL_STATUS.ERROR.value
      }
    }
  },
  created() {
    if (this.$route.params.id) {
      this.machineId = parseInt(this.$route.params.id)
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
