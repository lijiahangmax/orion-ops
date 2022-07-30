<template>
  <SchedulerTaskMachineLogAppender ref="logView" appenderHeight="calc(100vh - 41px)"/>
</template>

<script>
import SchedulerTaskMachineLogAppender from '@/components/log/SchedulerTaskMachineLogAppender'

export default {
  name: 'SchedulerMachineLogView',
  components: {
    SchedulerTaskMachineLogAppender
  },
  mounted() {
    this.$refs.logView.open(parseInt(this.$route.params.id))
  }
}
</script>

<style lang="less" scoped>
</style>
