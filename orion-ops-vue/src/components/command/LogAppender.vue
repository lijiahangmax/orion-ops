<template>
  <div class="logger-view-container">
    <!-- 工具 -->
    <div class="log-tools">
      <!-- 左侧工具 -->
      <div class="log-tools-fixed-left">
        <slot name="left-tools"></slot>
      </div>
      <!-- 右侧工具 -->
      <div class="log-tools-fixed-right">
        <!-- 复制 -->
        <a-button :size="size" class="mr4" type="primary" icon="copy" @click="copy">复制</a-button>
        <!-- 清空 -->
        <a-button :size="size" class="mr8" type="default" icon="delete" @click="clear">清空</a-button>
        <!-- 固定日志 -->
        <span class="log-fixed-label">固定: </span>
        <a-switch class="log-fixed-switch" v-model="fixedLog" :size="size"/>
        <!-- 状态 可关闭 -->
        <template v-if="$enum.LOG_TAIL_STATUS.RUNNABLE.value === status">
          <a-popconfirm title="确认关闭当前日志连接?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="close">
            <a-badge class="pointer"
                     :status="$enum.valueOf($enum.LOG_TAIL_STATUS, status).status"
                     :text="$enum.valueOf($enum.LOG_TAIL_STATUS, status).label"/>
          </a-popconfirm>
        </template>
        <!-- 状态 其他 -->
        <template v-else>
          <a-badge :status="$enum.valueOf($enum.LOG_TAIL_STATUS, status).status"
                   :text="$enum.valueOf($enum.LOG_TAIL_STATUS, status).label"/>
        </template>
      </div>
    </div>
    <!-- 日志容器 -->
    <div class="log-container" ref="logContainer" :style="appendStyle"></div>
  </div>
</template>

<script>
export default {
  name: 'LogAppender',
  props: {
    config: Object,
    appendStyle: {
      type: Object,
      default: () => {
        return {
          height: 'calc(100% - 40px)'
        }
      }
    },
    size: {
      config: String,
      default: 'small'
    }
  },
  data() {
    return {
      client: null,
      fixedLog: false,
      status: this.$enum.LOG_TAIL_STATUS.WAITING.value,
      scroll: 0
    }
  },
  methods: {
    openTail(data) {
      // 打开websocket
      this.client = new WebSocket(this.$api.fileTail(data.token))
      this.client.onopen = () => {
        this.status = this.$enum.LOG_TAIL_STATUS.RUNNABLE.value
      }
      this.client.onerror = () => {
        this.status = this.$enum.LOG_TAIL_STATUS.ERROR.value
      }
      this.client.onclose = () => {
        this.status = this.$enum.LOG_TAIL_STATUS.CLOSE.value
      }
      this.client.onmessage = event => {
        let msg
        if (event.data.length) {
          msg = event.data
        } else {
          msg = ' '
        }
        this.$refs.logContainer.innerHTML += `<pre class="log-line">${msg}</pre>`
        if (!this.fixedLog) {
          this.$refs.logContainer.scrollTop = this.$refs.logContainer.scrollHeight
        }
      }
    },
    copy() {
      this.$copy(this.$refs.logContainer.innerText)
    },
    clear() {
      this.$refs.logContainer.innerHTML = ''
      this.scroll = 0
    },
    close() {
      this.client.close()
    },
    storeScroll() {
      this.scroll = this.$refs.logContainer.scrollTop
    },
    toScroll() {
      if (this.fixedLog) {
        this.$refs.logContainer.scrollTop = this.scroll
      } else {
        this.$refs.logContainer.scrollTop = this.$refs.logContainer.scrollHeight
      }
    }
  },
  mounted() {
    this.$api.getTailToken(this.config)
      .then(({ data }) => {
        this.openTail(data)
      })
  }
}
</script>

<style lang="less" scoped>

.logger-view-container {
  height: 100%;
}

.log-tools {
  display: flex;
  align-items: center;
  justify-content: space-between;
  align-content: center;
  padding: 4px 0;

  .log-tools-fixed-left {
    width: 50%;
  }

  .log-tools-fixed-right {
    width: 50%;
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }

  .log-fixed-label {
    margin-right: 4px;
  }

  .log-fixed-switch {
    margin: 2px 12px 0 0;
  }

}

.log-container {
  overflow-y: auto;
  white-space: pre;
  padding: 8px;
  border-radius: 4px;
  width: 100%;
  color: #FFF;
  background: #212529;
  font-size: 13px;
  line-height: 17px;
}

/deep/ .log-line {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
}

</style>
