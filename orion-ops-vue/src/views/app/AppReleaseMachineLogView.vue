<template>
  <AppReleaseMachineLogAppender ref="appender" appenderHeight="calc(100vh - 86px)"/>
</template>

<script>
import AppReleaseMachineLogAppender from '@/components/log/AppReleaseMachineLogAppender'

export default {
  name: 'AppReleaseMachineLogView',
  components: {
    AppReleaseMachineLogAppender
  },
  mounted() {
    this.$refs.appender.open(parseInt(this.$route.params.id))
  }
}
</script>

<style scoped>

</style>
