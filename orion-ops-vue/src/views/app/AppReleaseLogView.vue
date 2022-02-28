<template>
  <AppReleaseLogAppender ref="appender" height="100vh"/>
</template>

<script>
import AppReleaseLogAppender from '@/components/log/AppReleaseLogAppender'

export default {
  name: 'AppReleaseLogView',
  components: {
    AppReleaseLogAppender
  },
  mounted() {
    this.$refs.appender.open(parseInt(this.$route.params.id))
  }
}
</script>

<style scoped>

</style>
