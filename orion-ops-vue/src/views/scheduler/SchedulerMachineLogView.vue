<template>
  <SchedulerTaskMachineLogAppender ref="logView"/>
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
