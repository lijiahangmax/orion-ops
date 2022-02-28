<template>
  <div class="app-build-container">
    <!-- 步骤 -->
    <div class="app-build-steps">
      <a-steps :current="current" :status="stepStatus">
        <template v-for="action in detail.actions">
          <a-step :key="action.id"
                  :title="action.actionName"
                  :subTitle="action.used ? `${action.used}ms` : ''">
            <template v-if="action.status === $enum.ACTION_STATUS.RUNNABLE.value" #icon>
              <a-icon type="loading"/>
            </template>
          </a-step>
        </template>
      </a-steps>
    </div>
    <!-- 日志 -->
    <div class="app-build-log">
      <logAppender ref="appender"
                   size="default"
                   :appendStyle="{height: appenderHeight}"
                   :relId="id"
                   :downloadType="$enum.FILE_DOWNLOAD_TYPE.APP_BUILD_LOG.value"
                   :config="{type: $enum.FILE_TAIL_TYPE.APP_BUILD_LOG.value, relId: id}">
        <!-- 左侧工具 -->
        <template #left-tools>
          <div class="build-log-tools">
            <a-tag color="#5C7CFA" v-if="detail.seq">
              #{{ detail.seq }}
            </a-tag>
            <a-tag color="#40C057" v-if="detail.appName">
              {{ detail.appName }}
            </a-tag>
            <a-tag color="#845EF7" v-if="detail.profileName">
              {{ detail.profileName }}
            </a-tag>
            <div class="download-bundle-wrapper" v-if="detail.status === $enum.BUILD_STATUS.FINISH.value">
              <a-button v-if="!downloadUrl" type="default" icon="link" @click="loadDownloadUrl">获取产物链接</a-button>
              <a target="_blank" :href="downloadUrl" @click="clearDownloadUrl" v-else>
                <a-button type="default" icon="download">下载产物</a-button>
              </a>
            </div>
          </div>
        </template>
      </logAppender>
    </div>
  </div>
</template>

<script>

import LogAppender from '@/components/log/LogAppender'

export default {
  name: 'AppBuildLogAppender',
  components: { LogAppender },
  props: {
    appenderHeight: {
      type: String,
      default: 'calc(100vh - 112px)'
    }
  },
  computed: {
    stepStatus() {
      if (this.detail.status) {
        return this.$enum.valueOf(this.$enum.BUILD_STATUS, this.detail.status).stepStatus
      }
      return null
    }
  },
  data() {
    return {
      id: null,
      current: 0,
      detail: {},
      pollId: null,
      downloadUrl: null
    }
  },
  methods: {
    open(id) {
      this.id = id
      this.$api.getAppBuildDetail({
        id: this.id
      }).then(({ data }) => {
        this.detail = data
        this.setStepsCurrent()
        // 设置轮询状态
        if (this.detail.status === this.$enum.BUILD_STATUS.WAIT.value ||
          this.detail.status === this.$enum.BUILD_STATUS.RUNNABLE.value) {
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
      this.current = 0
      this.detail = {}
      this.downloadUrl = null
    },
    pollStatus() {
      this.$api.getAppBuildStatus({
        id: this.id
      }).then(({ data }) => {
        this.detail.status = data.status
        // 清除状态轮询
        if (this.detail.status !== this.$enum.BUILD_STATUS.WAIT.value &&
          this.detail.status !== this.$enum.BUILD_STATUS.RUNNABLE.value) {
          clearInterval(this.pollId)
          this.pollId = null
        }
        // 设置action
        if (!data.actions || !data.actions.length || !this.detail.actions || !this.detail.actions.length) {
          return
        }
        for (const actionStatus of data.actions) {
          this.detail.actions.filter(s => s.id === actionStatus.id).forEach(e => {
            e.status = actionStatus.status
            e.used = actionStatus.used
          })
        }
        // 设置当前操作
        this.setStepsCurrent()
      })
    },
    setStepsCurrent() {
      const len = this.detail.actions.length
      let curr = len - 1
      for (let i = 0; i < len; i++) {
        const status = this.detail.actions[i].status
        if (status !== this.$enum.ACTION_STATUS.FINISH.value) {
          curr = i
          break
        }
      }
      this.current = curr
    },
    async loadDownloadUrl() {
      try {
        const downloadUrl = await this.$api.getFileDownloadToken({
          type: this.$enum.FILE_DOWNLOAD_TYPE.APP_BUILD_BUNDLE.value,
          id: this.id
        })
        this.downloadUrl = this.$api.fileDownloadExec({ token: downloadUrl.data })
      } catch (e) {
        // ignore
      }
    },
    clearDownloadUrl() {
      setTimeout(() => {
        this.downloadUrl = null
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

.app-build-steps {
  height: 56px;
  padding: 12px;
}

.app-build-log {
  padding: 8px;

  .build-log-tools {
    display: flex;
    align-items: center;
  }
}

</style>
