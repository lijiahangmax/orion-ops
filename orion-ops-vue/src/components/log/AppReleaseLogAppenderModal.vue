<template>
  <a-modal v-model="visible"
           :closable="false"
           :title="null"
           :footer="null"
           :dialogStyle="{top: '16px', padding: 0}"
           :bodyStyle="{padding: '1px'}"
           :destroyOnClose="true"
           :forceRender="true"
           @cancel="close"
           width="98%">
    <!-- 日志面板 -->
    <AppReleaseLogAppender ref="logger" appenderHeight="calc(100vh - 186px)" height="calc(100vh - 34px)"/>
  </a-modal>
</template>

<script>
import AppReleaseLogAppender from '@/components/log/AppReleaseLogAppender'

export default {
  name: 'AppReleaseLogAppenderModal',
  components: {
    AppReleaseLogAppender
  },
  data() {
    return {
      visible: false
    }
  },
  methods: {
    open(id) {
      this.visible = true
      setTimeout(() => {
        this.$refs.logger.open(id)
      }, 100)
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
