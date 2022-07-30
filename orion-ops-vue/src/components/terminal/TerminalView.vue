<template>
  <div class="terminal-container">
    <!-- 头部 -->
    <TerminalHeader :machineId="machineId"
                    :machine="machine"
                    @sendCommand='sendCommand'
                    @reload='reload'
                    @dispose='dispose'
                    @openSftp="openSftp"/>
    <!-- terminal 主体 -->
    <TerminalBody ref="terminal" :height="terminalHeight" @changeStatus="changeStatus"/>
    <!-- sftp 侧栏 -->
    <MachineSftpDrawer ref="sftp"
                       :machineId="machineId"
                       :machineName="machine.machineName"/>
  </div>
</template>

<script>
import { TERMINAL_STATUS } from '@/lib/enum'
import TerminalHeader from '@/components/terminal/TerminalHeader'
import TerminalBody from '@/components/terminal/TerminalBody'
import MachineSftpDrawer from '@/components/sftp/MachineSftpDrawer'

export default {
  name: 'TerminalView',
  components: {
    TerminalHeader,
    TerminalBody,
    MachineSftpDrawer
  },
  props: {
    terminalHeight: String
  },
  data: function() {
    return {
      machine: {
        status: null
      },
      machineId: null
    }
  },
  methods: {
    init(machine) {
      this.machine = {
        ...machine,
        status: TERMINAL_STATUS.NOT_CONNECT.value
      }
      this.machineId = machine.id
      const loading = this.$message.loading('建立连接中...')
      // 获取访问数据
      this.$api.accessTerminal({
        machineId: machine.id
      }).then(({ data }) => {
        // 初始化
        loading()
        this.$nextTick(() => {
          this.$refs.terminal.init(data)
        })
      }).catch(e => {
        loading()
        this.$message.error(e.msg || '初始化失败')
      })
    },
    sendCommand(e) {
      this.$refs.terminal.sendKey(e)
    },
    dispose() {
      this.$refs.terminal && this.$refs.terminal.dispose()
    },
    reload() {
      this.dispose()
      this.init(this.machine)
    },
    changeStatus(status) {
      this.machine.status = status
    },
    openSftp() {
      this.$refs.sftp.visible = true
    },
    fitTerminal() {
      this.$refs.terminal.fitTerminal()
    },
    toTop() {
      this.$refs.terminal.term && this.$refs.terminal.term.scrollToTop()
    },
    toBottom() {
      this.$refs.terminal.term && this.$refs.terminal.term.scrollToBottom()
    },
    focus() {
      this.$refs.terminal.focus()
    }
  }
}
</script>

<style scoped>

</style>
