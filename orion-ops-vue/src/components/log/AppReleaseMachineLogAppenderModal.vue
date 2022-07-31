<template>
  <a-modal v-model="visible"
           :closable="false"
           :title="null"
           :footer="null"
           :dialogStyle="{top: '16px', padding: 0}"
           :bodyStyle="{padding: 0}"
           :destroyOnClose="true"
           :forceRender="true"
           @cancel="close"
           width="96%">
    <!-- 日志面板 -->
    <AppReleaseMachineLogAppender ref="logger" appenderHeight="calc(100vh - 122px)"/>
  </a-modal>
</template>

<script>

import AppReleaseMachineLogAppender from '@/components/log/AppReleaseMachineLogAppender'

export default {
  name: 'AppReleaseMachineLogAppenderModal',
  components: { AppReleaseMachineLogAppender },
  data() {
    return {
      visible: false
    }
  },
  methods: {
    open(id) {
      this.visible = true
      setTimeout(() => {
        this.$refs.logger.open(id)
      }, 300)
    },
    close() {
      this.visible = false
      this.$nextTick(() => {
        this.$refs.logger.close()
      })
    }
  }
}
</script>

<style scoped>

</style>
