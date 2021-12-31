<template>
  <a-modal v-model="visible"
           :closable="false"
           :title="null"
           :footer="null"
           :dialogStyle="{top: '16px'}"
           :bodyStyle="{padding: '4px'}"
           @cancel="close"
           width="95%">
    <!-- 日志面板 -->
    <AppReleaseMachineLogAppender ref="logger" appenderHeight="calc(100vh - 164px)"/>
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
      this.$nextTick(() => {
        this.$refs.logger.open(id)
      })
    },
    close() {
      this.visible = true
      this.$nextTick(() => {
        this.$refs.logger.close()
      })
    }
  }
}
</script>

<style scoped>

</style>
