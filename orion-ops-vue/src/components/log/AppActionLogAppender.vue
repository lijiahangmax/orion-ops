<template>
  <div class="app-action-container">
    <!-- 日志 -->
    <div class="app-action-log">
      <logAppender ref="appender"
                   size="default"
                   :appendStyle="{height: appenderHeight}"
                   :relId="id"
                   :tailType="$enum.FILE_TAIL_TYPE.APP_ACTION_LOG.value"
                   :downloadType="$enum.FILE_DOWNLOAD_TYPE.APP_ACTION_LOG.value">
        <!-- 左侧工具 -->
        <template #left-tools>
          <div class="action-log-tools">
            <a-tag color="#5C7CFA" v-if="detail.actionName">
              {{ detail.actionName }}
            </a-tag>
          </div>
        </template>
      </logAppender>
    </div>
  </div>
</template>

<script>

import LogAppender from '@/components/log/LogAppender'

export default {
  name: 'AppActionLogAppender',
  components: { LogAppender },
  props: {
    appenderHeight: {
      type: String,
      default: 'calc(100vh - 56px)'
    }
  },
  data() {
    return {
      id: null,
      pollId: null,
      detail: {}
    }
  },
  methods: {
    open(id) {
      this.id = id
      this.$api.getAppActionDetail({
        id: this.id
      }).then(({ data }) => {
        this.detail = data
        // 设置轮询状态
        if (this.detail.status === this.$enum.ACTION_STATUS.WAIT.value ||
          this.detail.status === this.$enum.ACTION_STATUS.RUNNABLE.value) {
          this.pollId = setInterval(this.pollStatus, 2000)
        }
      }).then(() => {
        this.$nextTick(() => this.$refs.appender.openTail())
      })
    },
    close() {
      // 关闭轮询
      if (this.pollId) {
        clearInterval(this.pollId)
        this.pollId = null
      }
      // 关闭tail
      this.$nextTick(() => {
        this.$refs.appender.clear()
        this.$refs.appender.close()
      })
      this.id = null
      this.detail = {}
    },
    pollStatus() {
      this.$api.getAppActionStatus({
        id: this.id
      }).then(({ data }) => {
        this.detail.status = data.status
        // 清除状态轮询
        if (this.detail.status !== this.$enum.ACTION_STATUS.WAIT.value &&
          this.detail.status !== this.$enum.ACTION_STATUS.RUNNABLE.value) {
          clearInterval(this.pollId)
          this.pollId = null
        }
      })
    }
  },
  beforeDestroy() {
    this.pollId !== null && clearInterval(this.pollId)
    this.pollId = null
  }
}
</script>

<style lang="less" scoped>

.app-action-log {
  padding: 8px;

  .action-log-tools {
    display: flex;
    align-items: baseline;
  }
}

</style>
