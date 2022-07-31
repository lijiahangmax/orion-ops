<template>
  <div class="app-build-container">
    <!-- 步骤 -->
    <div class="app-build-steps">
      <a-steps :current="current" :status="stepStatus">
        <template v-for="action in detail.actions">
          <a-step :key="action.id"
                  :title="action.actionName"
                  :subTitle="action.used ? `${action.used}ms` : ''">
            <template v-if="action.status === ACTION_STATUS.RUNNABLE.value" #icon>
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
                   :height="appenderHeight"
                   :relId="id"
                   :tailType="FILE_TAIL_TYPE.APP_BUILD_LOG.value"
                   :downloadType="FILE_DOWNLOAD_TYPE.APP_BUILD_LOG.value">
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
            <!-- 命令输入 -->
            <a-input-search class="command-write-input"
                            size="default"
                            v-if="BUILD_STATUS.RUNNABLE.value === detail.status"
                            v-model="command"
                            placeholder="输入"
                            @search="sendCommand">
              <template #enterButton>
                <a-icon type="forward"/>
              </template>
              <!-- 发送 lf -->
              <template #suffix>
                <a-icon :class="{'send-lf-trigger': true, 'send-lf-trigger-enable': sendLf}"
                        title="是否发送 \n"
                        type="pull-request"
                        @click="() => sendLf = !sendLf"/>
              </template>
            </a-input-search>
            <!-- 停止 -->
            <a-popconfirm v-if="BUILD_STATUS.RUNNABLE.value === detail.status"
                          title="是否要停止执行?"
                          placement="bottomLeft"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="terminate">
              <a-button icon="close">停止</a-button>
            </a-popconfirm>
            <!-- 下载 -->
            <div class="download-bundle-wrapper" v-if="detail.status === BUILD_STATUS.FINISH.value">
              <a-button v-if="!downloadUrl" icon="link" size="small" @click="loadDownloadUrl">获取产物链接</a-button>
              <a target="_blank" :href="downloadUrl" @click="clearDownloadUrl" v-else>
                <a-button icon="download">下载产物</a-button>
              </a>
            </div>
          </div>
        </template>
      </logAppender>
    </div>
  </div>
</template>

<script>
import { enumValueOf, ACTION_STATUS, BUILD_STATUS, FILE_DOWNLOAD_TYPE, FILE_TAIL_TYPE } from '@/lib/enum'
import LogAppender from '@/components/log/LogAppender'

export default {
  name: 'AppBuildLogAppender',
  components: { LogAppender },
  props: {
    appenderHeight: String
  },
  computed: {
    stepStatus() {
      if (this.detail.status) {
        return enumValueOf(BUILD_STATUS, this.detail.status).stepStatus
      }
      return null
    }
  },
  data() {
    return {
      FILE_TAIL_TYPE,
      FILE_DOWNLOAD_TYPE,
      BUILD_STATUS,
      ACTION_STATUS,
      id: null,
      current: 0,
      detail: {},
      command: null,
      sendLf: true,
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
        if (this.detail.status === BUILD_STATUS.WAIT.value ||
          this.detail.status === BUILD_STATUS.RUNNABLE.value) {
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
        this.$refs.appender.dispose()
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
        if (this.detail.status !== BUILD_STATUS.WAIT.value &&
          this.detail.status !== BUILD_STATUS.RUNNABLE.value) {
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
    terminate() {
      this.$api.terminateAppBuild({
        id: this.detail.id
      }).then(() => {
        this.$message.success('已停止')
      })
    },
    sendCommand() {
      let command = this.command || ''
      if (this.sendLf) {
        command += '\n'
      }
      if (!command) {
        return
      }
      this.command = ''
      this.$api.writeAppBuild({
        id: this.detail.id,
        command
      })
    },
    setStepsCurrent() {
      const len = this.detail.actions.length
      let curr = len - 1
      for (let i = 0; i < len; i++) {
        const status = this.detail.actions[i].status
        if (status !== ACTION_STATUS.FINISH.value) {
          curr = i
          break
        }
      }
      this.current = curr
    },
    async loadDownloadUrl() {
      try {
        const downloadUrl = await this.$api.getFileDownloadToken({
          type: FILE_DOWNLOAD_TYPE.APP_BUILD_BUNDLE.value,
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
  height: 46px;
  padding: 8px 12px 0 12px;
}

.build-log-tools {
  display: flex;
  align-items: center;
}

</style>
