<template>
  <!-- 日志面板 -->
  <ExecLoggerAppender ref="logger" appenderHeight="calc(100vh - 41px)"/>
</template>

<script>
import ExecLoggerAppender from '@/components/log/ExecLoggerAppender'

export default {
  name: 'ExecLoggerView',
  components: {
    ExecLoggerAppender
  },
  async mounted() {
    this.$refs.logger.open(parseInt(this.$route.params.id))
  }
}
</script>

<style lang="less" scoped>
</style>
