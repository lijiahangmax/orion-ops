<template>
  <a-modal v-model="visible"
           :closable="false"
           :title="null"
           :footer="null"
           :dialogStyle="{top: '16px', padding: 0}"
           :bodyStyle="{padding: '1px'}"
           @cancel="close"
           width="98%">
    <!-- 日志面板 -->
    <SchedulerTaskLogAppender ref="logger" appenderHeight="calc(100vh - 130px)" height="calc(100vh - 34px)"/>
  </a-modal>
</template>

<script>
import SchedulerTaskLogAppender from '@/components/log/SchedulerTaskLogAppender'

export default {
  name: 'SchedulerTaskLogAppenderModal',
  components: {
    SchedulerTaskLogAppender
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

<style lang="less" scoped>
</style>
