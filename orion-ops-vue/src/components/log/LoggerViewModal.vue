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
    <div style="padding: 0 8px 8px 8px;">
      <LogAppender ref="appender"
                   size="default"
                   :relId="id"
                   :appendStyle="{height: 'calc(100vh - 92px)'}"
                   :downloadType="$enum.FILE_DOWNLOAD_TYPE.TAIL_LIST_FILE.value"
                   :config="{type: $enum.FILE_TAIL_TYPE.TAIL_LIST.value, relId: id}">
        <!-- 左侧工具栏 -->
        <template #left-tools>
          <div class="fixed-left-tools">
            <!-- 文件名称 -->
            <a-tag color="#40C057" style="max-width: 95%" :title="file.path">
              {{ file.path }}
            </a-tag>
            <!-- 复制 -->
            <a-icon class="span-blue pointer" type="copy" title="复制" @click="$copy(file.path)"/>
          </div>
        </template>
      </LogAppender>
    </div>
  </a-modal>
</template>

<script>
import LogAppender from '@/components/log/LogAppender'

export default {
  name: 'LoggerViewModal',
  components: {
    LogAppender
  },
  data() {
    return {
      id: null,
      file: {},
      visible: false
    }
  },
  methods: {
    open(id) {
      this.visible = true
      this.id = id
      this.$api.getTailDetail({
        id
      }).then(({ data }) => {
        this.file = data
      }).then(() => {
        this.$nextTick(() => this.$refs.appender.openTail())
      })
    },
    close() {
      this.id = null
      this.file = {}
      this.visible = false
      this.$nextTick(() => {
        this.$refs.appender.clear()
        this.$refs.appender.close()
      })
    }
  }
}
</script>

<style lang="less" scoped>

.fixed-left-tools {
  display: flex;
  align-items: center;
}

</style>
