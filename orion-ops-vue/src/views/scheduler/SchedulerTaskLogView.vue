<template>
  <div>
    <SchedulerTaskLogAppender ref="logView" height="100vh"/>
  </div>
</template>

<script>
import SchedulerTaskLogAppender from '@/components/log/SchedulerTaskLogAppender'

export default {
  name: 'SchedulerTaskLogView',
  components: {
    SchedulerTaskLogAppender
  },
  mounted() {
    this.$refs.logView.open(parseInt(this.$route.params.id))
  }
}
</script>

<style lang="less" scoped>
</style>
