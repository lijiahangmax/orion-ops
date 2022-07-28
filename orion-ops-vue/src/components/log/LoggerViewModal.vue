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
                   :isModal="true"
                   :appendStyle="{height: 'calc(100vh - 92px)'}"
                   :relId="id"
                   :tailType="FILE_TAIL_TYPE.TAIL_LIST.value"
                   :downloadType="FILE_DOWNLOAD_TYPE.TAIL_LIST_FILE.value"
                   @open="loggerOpened"
                   @close="loggerClosed">
        <!-- 左侧工具栏 -->
        <template #left-tools>
          <div class="fixed-left-tools">
            <!-- 日志信息 -->
            <div class="machine-log-info-container">
              <!-- 机器信息 -->
              <a-tooltip :title="file.machineHost">
                <span class="log-info-wrapper" style="margin-left: 2px;">
                  {{ file.machineName }}
                </span>
              </a-tooltip>
              <a-divider type="vertical" style="top: -0.36em; height: 1em"/>
              <!-- 文件信息 -->
              <a-tooltip :title="`${file.path}`">
                <span class="log-info-wrapper pointer" @click="$copy(file.path)">
                  {{ file.fileName }}
                </span>
              </a-tooltip>
            </div>
            <!-- 命令输入 -->
            <a-input-search class="command-write-input"
                            size="default"
                            v-if="visibleCommand"
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
          </div>
        </template>
      </LogAppender>
    </div>
  </a-modal>
</template>

<script>
import { FILE_DOWNLOAD_TYPE, FILE_TAIL_MODE, FILE_TAIL_TYPE } from '@/lib/enum'
import LogAppender from '@/components/log/LogAppender'

export default {
  name: 'LoggerViewModal',
  components: {
    LogAppender
  },
  data() {
    return {
      FILE_TAIL_TYPE,
      FILE_DOWNLOAD_TYPE,
      id: null,
      file: {},
      visible: false,
      visibleCommand: false,
      command: null,
      sendLf: true,
      closed: false
    }
  },
  methods: {
    open(id) {
      this.file = {}
      this.visibleCommand = false
      this.command = null
      this.sendLf = true
      this.closed = false
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
      this.visible = false
      this.$nextTick(() => {
        this.$refs.appender.clear()
        this.$refs.appender.close()
      })
    },
    loggerOpened() {
      if (this.$refs.appender.token.tailMode === FILE_TAIL_MODE.TAIL.value) {
        setTimeout(() => {
          if (!this.closed) {
            this.visibleCommand = true
          }
        }, 200)
      }
    },
    loggerClosed() {
      this.closed = true
      this.visibleCommand = false
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
      this.$api.writeTailCommand({
        token: this.$refs.appender.token.token,
        command
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

.machine-log-info-container {
  margin-right: 16px;
  margin-top: -9px;
  height: 1em;
}

.log-info-wrapper {
  color: rgba(0, 0, 0, .8);
  max-width: 200px;
  text-overflow: ellipsis;
  display: inline-block;
  overflow: hidden;
}
</style>
