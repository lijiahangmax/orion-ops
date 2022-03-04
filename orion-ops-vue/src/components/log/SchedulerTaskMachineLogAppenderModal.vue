<template>
  <a-modal v-model="visible"
           :closable="false"
           :title="null"
           :footer="null"
           :dialogStyle="{top: '16px', padding: 0}"
           :bodyStyle="{padding: '0px 4px 4px 4px'}"
           @cancel="close"
           width="98%">
    <!-- 日志面板 -->
    <SchedulerTaskMachineLogAppender ref="logger" appenderHeight="calc(100vh - 92px)"/>
  </a-modal>
</template>

<script>
import SchedulerTaskMachineLogAppender from '@/components/log/SchedulerTaskMachineLogAppender'

export default {
  name: 'SchedulerTaskMachineLogAppenderModal',
  components: {
    SchedulerTaskMachineLogAppender
  },
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
