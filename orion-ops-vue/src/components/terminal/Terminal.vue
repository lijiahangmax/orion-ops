<template>
  <div id="terminal-layer">
    <!-- 头部 -->
    <TerminalBar :machineId="machineId"
                 :machine="machine"
                 :setting="setting"
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
  import TerminalBar from './TerminalBar'
  import TerminalMain from './TerminalMain'
  import MachineSftp from '../sftp/MachineSftp'

  export default {
    name: 'Terminal',
    components: {
      TerminalBar,
      TerminalMain,
      MachineSftp
    },
    data: function() {
      return {
        machineId: null,
        machine: {
          status: 0
        },
        setting: {}
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
        this.setting.id = data.id
        this.setting.terminalType = data.terminalType
        this.setting.fontSize = data.fontSize
        this.setting.backgroundColor = data.backgroundColor
        this.setting.fontColor = data.fontColor
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
      if (this.$route.query.id) {
        this.machineId = parseInt(this.$route.query.id)
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
