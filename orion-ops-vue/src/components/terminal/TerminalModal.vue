<template>
  <a-modal v-model="visible"
           :closable="false"
           :title="null"
           :footer="null"
           :maskClosable="false"
           :dialogStyle="{top: '16px', padding: 0}"
           :bodyStyle="{padding: '4px'}"
           @cancel="close"
           width="80%">
    <div class="terminal-wrapper">
      <TerminalXterm v-if="machineId"
                     ref="terminal"
                     wrapperHeight="100%"
                     terminalHeight="100%"
                     :machineId="machineId"
                     :rightMenuY="(e) => e.offsetY + 5"
                     :visibleHeader="false"/>
    </div>
  </a-modal>
</template>

<script>
import TerminalXterm from '@/components/terminal/TerminalXterm'

export default {
  name: 'TerminalModal',
  components: { TerminalXterm },
  data() {
    return {
      visible: false,
      machineId: null
    }
  },
  methods: {
    open(machineId) {
      this.visible = true
      this.$nextTick(() => {
        this.machineId = machineId
      })
    },
    close() {
      this.$refs.terminal.dispose()
      this.$refs.terminal.disconnect()
      this.visible = false
      this.machineId = null
    }
  }
}
</script>

<style lang="less" scoped>

.terminal-wrapper {
  height: calc(100vh - 40px);
}
</style>
