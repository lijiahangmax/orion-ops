<template>
  <a-modal v-model="visible"
           :closable="false"
           :title="null"
           :footer="null"
           :dialogStyle="{top: '32px', padding: 0}"
           :bodyStyle="{padding: 0}"
           :destroyOnClose="true"
           :forceRender="true"
           @cancel="close"
           width="96%">
    <!-- 日志面板 -->
    <AppActionLogAppender ref="logger" appenderHeight="calc(100vh - 106px)"/>
  </a-modal>
</template>

<script>
import AppActionLogAppender from '@/components/log/AppActionLogAppender'

export default {
  name: 'AppActionLogAppenderModal',
  components: { AppActionLogAppender },
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
      }, 300)
    },
    close() {
      this.visible = false
      this.$nextTick(() => {
        this.$refs.logger.close()
      })
    }
  }
}
</script>

<style scoped>

</style>
