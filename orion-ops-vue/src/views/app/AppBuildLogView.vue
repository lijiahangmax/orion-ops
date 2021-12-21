<template>
  <AppBuildLogAppender ref="logView"/>
</template>

<script>
import AppBuildLogAppender from '@/components/log/AppBuildLogAppender'

export default {
  name: 'AppBuildLogView',
  components: {
    AppBuildLogAppender
  },
  mounted() {
    this.$refs.logView.open(parseInt(this.$route.params.id))
  }
}
</script>

<style lang="less" scoped>
</style>
