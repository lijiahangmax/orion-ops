<template>
  <a-modal v-model="visible"
           :closable="false"
           :title="null"
           :footer="null"
           :dialogStyle="{top: '16px', padding: 0}"
           :bodyStyle="{padding: '4px'}"
           @cancel="close"
           width="98%">
    <!-- 日志面板 -->
    <AppBuildLogAppender ref="logger" appenderHeight="calc(100vh - 148px)"/>
  </a-modal>
</template>

<script>
import AppBuildLogAppender from '@/components/log/AppBuildLogAppender'

export default {
  name: 'AppBuildLogAppenderModal',
  components: { AppBuildLogAppender },
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
