<template>
  <div id="terminal-layer">
    <!-- 头部 -->
    <TerminalHeader :machineId="machineId"
                    :machine="machine"
                    @inputCommand='inputCommand'
                    @disconnect='disconnect'
                    @openSftp="openSftp"/>
    <!-- terminal主体 -->
    <TerminalMain ref="terminalMain"
                  :machineId="machineId"
                  @dispatchAccessData="dispatchAccessData"
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
    inputCommand(e) {
      this.$refs.terminalMain.writerCommand(e)
    },
    disconnect(e) {
      this.$refs.terminalMain.disconnect(e)
    },
    dispatchAccessData(data) {
      this.machine.machineId = this.machineId
      this.machine.host = data.host
      this.machine.port = data.port
      this.machine.username = data.username
      this.machine.machineName = data.machineName
      document.title = `${data.machineName} | ${data.host}`
    },
    terminalStatusChange(status) {
      this.machine.status = status
    },
    openSftp() {
      this.$refs.machineSftp.visible = true
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
    this.$refs.terminalMain.openTerminal()
  }
}
</script>

<style scoped>

</style>
