<template>
  <a-modal v-model="visible"
           :closable="false"
           :title="null"
           :footer="null"
           :dialogStyle="{top: '16px', padding: 0}"
           :bodyStyle="{padding: '0 4px 4px 4px'}"
           :destroyOnClose="true"
           :forceRender="true"
           @cancel="close"
           width="98%">
    <!-- 日志面板 -->
    <div style="padding: 8px;">
      <LogAppender ref="appender"
                   size="default"
                   :appendStyle="{height: 'calc(100vh - 92px)'}"
                   :relId="id"
                   :tailType="$enum.FILE_TAIL_TYPE.TAIL_LIST.value"
                   :downloadType="$enum.FILE_DOWNLOAD_TYPE.TAIL_LIST_FILE.value">
        <!-- 左侧工具栏 -->
        <template #left-tools>
          <div class="fixed-left-tools">
            <!-- 文件名称 -->
            <a-breadcrumb>
              <!-- 机器名称 -->
              <a-breadcrumb-item v-if="file.machineName">
                <a-tag color="#7950F2" class="m0">
                  {{ file.machineName }}
                </a-tag>
              </a-breadcrumb-item>
              <!-- 机器主机 -->
              <a-breadcrumb-item v-if="file.machineHost">
                <a-tag color="#5C7CFA" class="m0">
                  {{ file.machineHost }}
                </a-tag>
              </a-breadcrumb-item>
              <!-- 名称 -->
              <a-breadcrumb-item v-if="file.name">
                <a-tag color="#15AABF" class="m0">
                  {{ file.name }}
                </a-tag>
              </a-breadcrumb-item>
              <!-- 文件名称 -->
              <a-breadcrumb-item v-if="file.path">
                <a-tag color="#40C057"
                       class="pointer"
                       :title="file.path"
                       @click="$copy(file.path)">
                  {{ file.fileName }}
                </a-tag>
              </a-breadcrumb-item>
            </a-breadcrumb>
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
