<template>
  <div class="logger-view-container-wrapper">
    <!-- 日志面板 -->
    <div class="logger-view-container">
      <!-- 工具 -->
      <div class="log-tools" v-if="toolsProps.visible !== false">
        <!-- 左侧工具 -->
        <div class="log-tools-fixed-left">
          <slot name="left-tools" v-if="toolsProps.visibleLeft !== false"/>
        </div>
        <!-- 右侧工具 -->
        <div class="log-tools-fixed-right" v-if="toolsProps.visibleRight !== false">
          <!-- 行数 -->
          <div class="mr8 line-tool-container" v-if="rightToolsProps.line !== false">
            <a-tooltip title="防止内容太多而造成浏览器卡死, 设置行数保留阈值">
              <a-input v-model="linesThresholdTemp" :size="size" v-limit-integer addon-before="行数">
                <template #addonAfter>
                  <a-icon class="pointer" type="check" title="保存" @click="saveLinesThreshold"/>
                </template>
              </a-input>
            </a-tooltip>
          </div>
          <!-- 复制 -->
          <a-button class="mr8"
                    v-if="rightToolsProps.copy !== false"
                    :size="size"
                    type="primary"
                    icon="copy"
                    @click="copy">
            复制
          </a-button>
          <!-- 清空 -->
          <a-button class="mr8"
                    v-if="rightToolsProps.clean !== false"
                    :size="size"
                    type="default"
                    icon="delete"
                    @click="clear">
            清空
          </a-button>
          <!-- 下载 -->
          <div class="log-download-wrapper" v-if="rightToolsProps.download !== false">
            <a-button :size="size" v-if="!downloadUrl" type="default" icon="link" @click="loadDownloadUrl">获取下载链接</a-button>
            <a target="_blank" :href="downloadUrl" @click="clearDownloadUrl" v-else>
              <a-button :size="size" type="default" icon="download">下载</a-button>
            </a>
          </div>
          <!-- 固定日志 -->
          <div class="log-fixed-wrapper nowrap" v-if="rightToolsProps.fixed !== false">
            <span class="log-fixed-label normal-label ml8">固定</span>
            <a-switch class="log-fixed-switch" v-model="fixedLog" :size="size"/>
          </div>
          <!-- 状态 -->
          <div class="log-status-wrapper nowrap" v-if="rightToolsProps.status !== false">
            <!-- 状态 执行中 -->
            <template v-if="$enum.LOG_TAIL_STATUS.RUNNABLE.value === status">
              <a-popconfirm title="确认关闭当前日志连接?"
                            placement="topRight"
                            ok-text="确定"
                            cancel-text="取消"
                            @confirm="close">
                <a-badge class="pointer"
                         :status="$enum.LOG_TAIL_STATUS.RUNNABLE.status"
                         :text="$enum.LOG_TAIL_STATUS.RUNNABLE.label"/>
              </a-popconfirm>
            </template>
            <!-- 状态 已关闭 -->
            <template v-else-if="$enum.LOG_TAIL_STATUS.CLOSE.value === status">
              <a-tooltip :title="`code: ${closeCode}${closeReason ? '; reason: ' + closeReason: ''}`">
                <a-badge :status="$enum.LOG_TAIL_STATUS.CLOSE.status"
                         :text="$enum.LOG_TAIL_STATUS.CLOSE.label"/>
              </a-tooltip>
            </template>
            <!-- 状态 其他 -->
            <template v-else>
              <a-badge :status="$enum.valueOf($enum.LOG_TAIL_STATUS, status).status"
                       :text="$enum.valueOf($enum.LOG_TAIL_STATUS, status).label"/>
            </template>
          </div>
        </div>
      </div>
      <!-- 日志容器 -->
      <div class="log-container"
           ref="logContainer"
           :style="appendStyle"
           @contextmenu.prevent="$refs.rightMenu.openRightMenu"/>
    </div>
    <!-- 事件 -->
    <div class="logger-view-event-container">
      <RightClickMenu ref="rightMenu" :x="rightMenuX" :y="rightMenuY" @clickRight="clickRightMenuItem">
        <template #items>
          <a-menu-item key="copy">
            <span class="right-menu-item"><a-icon type="copy"/>复制所有</span>
          </a-menu-item>
          <a-menu-item key="clear">
            <span class="right-menu-item"><a-icon type="stop"/>清空</span>
          </a-menu-item>
          <a-menu-item key="lock" v-if="!fixedLog">
            <span class="right-menu-item"><a-icon type="lock"/>锁定</span>
          </a-menu-item>
          <a-menu-item key="unlock" v-if="fixedLog">
            <span class="right-menu-item"><a-icon type="unlock"/>滚动</span>
          </a-menu-item>
          <a-menu-item key="toTop">
            <span class="right-menu-item"><a-icon type="vertical-align-top"/>去顶部</span>
          </a-menu-item>
          <a-menu-item key="toBottom">
            <span class="right-menu-item"><a-icon type="vertical-align-bottom"/>去底部</span>
          </a-menu-item>
        </template>
      </RightClickMenu>
    </div>
  </div>
</template>

<script>
import RightClickMenu from '@/components/common/RightClickMenu'

const stain = {
  DEBUG: {
    class: 'color-debug',
    reg: new RegExp('debug', 'ig')
  },
  INFO: {
    class: 'color-info',
    reg: new RegExp('info', 'ig')
  },
  WARN: {
    class: 'color-warn',
    reg: new RegExp('warn', 'ig')
  },
  ERROR: {
    class: 'color-error',
    reg: new RegExp('error', 'ig')
  },
  DATE_TIME: {
    class: 'color-date',
    reg: new RegExp('[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d(\\.\\d{1,4})?', 'ig')
  },
  DATE: {
    class: 'color-date',
    reg: new RegExp('[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])(\\.\\d{1,4})?', 'ig')
  }
}

const urlPattern = new RegExp('(http|https)://([\\w.]+/?)\\S*\\b', 'ig')

/**
 * 右键菜单操作
 */
const rightMenuHandler = {
  copy() {
    this.copy()
  },
  clear() {
    this.clear()
  },
  lock() {
    this.fixedLog = true
  },
  unlock() {
    this.fixedLog = false
  },
  toTop() {
    this.$refs.logContainer.scrollTop = 0
  },
  toBottom() {
    this.$refs.logContainer.scrollTop = this.$refs.logContainer.scrollHeight
  }
}

export default {
  name: 'LogAppender',
  components: { RightClickMenu },
  props: {
    config: Object,
    appendStyle: {
      type: Object,
      default: () => {
        return {
          height: '100%'
        }
      }
    },
    size: {
      type: String,
      default: 'small'
    },
    relId: Number,
    tailType: Number,
    downloadType: Number,
    toolsProps: {
      type: Object,
      default: () => {
        return {
          visible: true,
          visibleLeft: true,
          visibleRight: true
        }
      }
    },
    rightToolsProps: {
      type: Object,
      default: () => {
        return {
          line: true,
          copy: true,
          clean: true,
          fixed: true,
          download: true,
          status: true
        }
      }
    },
    rightMenuX: Function,
    rightMenuY: Function
  },
  data() {
    return {
      client: null,
      fixedLog: false,
      status: this.$enum.LOG_TAIL_STATUS.WAITING.value,
      scroll: 0,
      downloadUrl: null,
      lines: 0,
      linesThresholdTemp: undefined,
      linesThreshold: -1,
      closeCode: null,
      closeReason: null
    }
  },
  methods: {
    openTail() {
      this.$nextTick(() => {
        this.$api.getTailToken({
          type: this.tailType,
          relId: this.relId
        }).then(({ data }) => {
          this.initLogTailView(data)
        })
      })
    },
    initLogTailView(data) {
      // 打开websocket
      this.client = new WebSocket(this.$api.fileTail({ token: data.token }))
      this.client.onopen = () => {
        this.status = this.$enum.LOG_TAIL_STATUS.RUNNABLE.value
      }
      this.client.onerror = () => {
        this.status = this.$enum.LOG_TAIL_STATUS.ERROR.value
      }
      this.client.onclose = (e) => {
        console.log('log close:', e.code, e.reason)
        this.closeCode = e.code
        this.closeReason = e.reason
        this.status = this.$enum.LOG_TAIL_STATUS.CLOSE.value
        this.$emit('close')
      }
      this.client.onmessage = event => {
        this.lines++
        if (this.linesThreshold !== -1 && this.lines > this.linesThreshold) {
          this.$refs.logContainer.removeChild(this.$refs.logContainer.childNodes[0])
          this.lines--
        }
        let msg
        if (event.data.length) {
          msg = event.data
        } else {
          msg = ' '
        }
        const pre = document.createElement('pre')
        pre.className = 'log-line'
        pre.innerHTML = this.stainKeywords(msg)
        this.$refs.logContainer.appendChild(pre)
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
      this.lines = 0
    },
    close() {
      if (this.client) {
        this.client.close()
      }
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
    },
    async loadDownloadUrl() {
      try {
        const downloadUrl = await this.$api.getFileDownloadToken({
          type: this.downloadType,
          id: this.relId
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
    },
    stainKeywords(msg) {
      msg = this.$utils.cleanXss(msg)
      // 高亮
      for (const stainKey in stain) {
        msg = msg.replace(stain[stainKey].reg, (keyword) => {
          return `<span class="${stain[stainKey].class}">${keyword}</span>`
        })
      }
      // url
      msg = msg.replace(urlPattern, (keyword) => {
        return `<a target="_blank" class="color-url" href="${keyword}">${keyword}</a>`
      })
      return msg
    },
    saveLinesThreshold() {
      this.linesThreshold = this.linesThresholdTemp
      this.$storage.set(this.$storage.keys.LINES_THRESHOLD, this.linesThreshold)
      const effect = this.lines - this.linesThreshold
      if (effect > 0) {
        for (let i = 0; i < effect; i++) {
          this.$refs.logContainer.removeChild(this.$refs.logContainer.childNodes[0])
          this.lines--
        }
      }
      this.$message.success('已生效')
    },
    clickRightMenuItem(key) {
      rightMenuHandler[key].call(this)
    }
  },
  mounted() {
    if (this.rightToolsProps.line) {
      const _lineThreshold = ~~this.$storage.get(this.$storage.keys.LINES_THRESHOLD) || 1000
      this.linesThreshold = _lineThreshold
      this.linesThresholdTemp = _lineThreshold
      this.$storage.set(this.$storage.keys.LINES_THRESHOLD, _lineThreshold)
    } else {
      this.linesThreshold = -1
    }
  },
  beforeDestroy() {
    this.clear()
    this.close()
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
  padding-bottom: 8px;

  .log-tools-fixed-left {
    min-width: 20%;
  }

  .log-tools-fixed-right {
    min-width: 50%;
    display: flex;
    align-items: center;
    justify-content: flex-end;

    .line-tool-container {
      width: 160px;
    }

    .log-fixed-wrapper {
      display: flex;
      align-items: center;

      .log-fixed-label {
        margin-right: 4px;
      }

      .log-fixed-switch {
        margin: 2px 12px 0 0;
      }
    }
  }
}

.log-container {
  overflow-y: auto;
  white-space: pre;
  padding: 8px;
  border-radius: 4px;
  width: 100%;
  background: #212529;
  font-size: 13px;
  line-height: 17px;
}

/deep/ .log-line {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  color: lightgrey;

  .color-debug {
    color: #757575;
    font-weight: 700;
  }

  .color-info {
    color: #58C612;
    font-weight: 700;
  }

  .color-warn {
    color: #FFCA28;
    font-weight: 700;
  }

  .color-error {
    color: #DD2C00;
    font-weight: 700;
  }

  .color-date {
    color: #80DEEA;
    font-weight: 700;
  }

  .color-url {
    text-decoration: underline;
  }
}

</style>
