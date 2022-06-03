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
            <span class="log-fixed-label normal-label ml8 usn">固定</span>
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
                <a-badge class="pointer usn"
                         :status="$enum.LOG_TAIL_STATUS.RUNNABLE.status"
                         :text="$enum.LOG_TAIL_STATUS.RUNNABLE.label"/>
              </a-popconfirm>
            </template>
            <!-- 状态 其他 -->
            <template v-else>
              <a-badge class="usn"
                       :status="$enum.valueOf($enum.LOG_TAIL_STATUS, status).status"
                       :text="$enum.valueOf($enum.LOG_TAIL_STATUS, status).label"/>
            </template>
          </div>
        </div>
      </div>
      <!-- 日志容器 -->
      <div class="log-container" ref="logContainer" :style="appendStyle">
        <!-- 右键菜单 -->
        <a-dropdown v-model="visibleRightMenu" :trigger="['contextmenu']">
          <!-- 日志终端 -->
          <div class="log-terminal" ref="logTerminal" @click="clickTerminal"/>
          <!-- 下拉菜单 -->
          <template #overlay>
            <a-menu @click="clickRightMenu">
              <a-menu-item key="copy">
                <span class="right-menu-item"><a-icon type="copy"/>复制</span>
              </a-menu-item>
              <a-menu-item key="selectAll">
                <span class="right-menu-item"><a-icon type="profile"/>全选</span>
              </a-menu-item>
              <a-menu-item key="clear">
                <span class="right-menu-item"><a-icon type="stop"/>清空</span>
              </a-menu-item>
              <a-menu-item key="toTop">
                <span class="right-menu-item"><a-icon type="vertical-align-top"/>去顶部</span>
              </a-menu-item>
              <a-menu-item key="toBottom">
                <span class="right-menu-item"><a-icon type="vertical-align-bottom"/>去底部</span>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
    </div>
    <!-- 事件 -->
    <div class="logger-view-event-container">
      <!-- 搜索框 -->
      <TerminalSearch ref="search" :searchPlugin="plugin.search"/>
    </div>
  </div>
</template>

<script>

import { Terminal } from 'xterm'
import { FitAddon } from 'xterm-addon-fit'
import { SearchAddon } from 'xterm-addon-search'
import { WebLinksAddon } from 'xterm-addon-web-links'

import 'xterm/css/xterm.css'
import TerminalSearch from '@/components/terminal/TerminalSearch'

/**
 * 右键菜单操作
 */
const rightMenuHandler = {
  selectAll() {
    this.term.selectAll()
  },
  copy() {
    this.copySelection(false)
  },
  clear() {
    this.clear()
  },
  toTop() {
    this.term.scrollToTop()
  },
  toBottom() {
    this.term.scrollToBottom()
  }
}

export default {
  name: 'LogAppender',
  components: { TerminalSearch },
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
          copy: true,
          clean: true,
          fixed: true,
          download: true,
          status: true
        }
      }
    }
  },
  data() {
    return {
      client: null,
      fixedLog: true,
      status: this.$enum.LOG_TAIL_STATUS.WAITING.value,
      downloadUrl: null,
      visibleRightMenu: false,
      term: null,
      termConfig: {
        rightClickSelectsWord: true,
        disableStdin: true,
        cursorStyle: 'bar',
        cursorBlink: true,
        fastScrollModifier: 'shift',
        fontSize: 13,
        rendererType: 'canvas',
        fontFamily: 'courier-new, courier, monospace',
        lineHeight: 1.08,
        convertEol: true,
        theme: {
          foreground: '#FFFFFF',
          background: '#212529'
        }
      },
      plugin: {
        fit: null,
        search: null,
        links: null
      },
      token: {}
    }
  },
  methods: {
    openTail() {
      this.$nextTick(() => {
        this.$api.getTailToken({
          type: this.tailType,
          relId: this.relId
        }).then(({ data }) => {
          this.token = data
          this.initLogTailView(data)
        })
      })
    },
    initLogTailView(data) {
      // 打开日志模块
      this.term = new Terminal(this.termConfig)
      this.term.open(this.$refs.logTerminal)
      // 需要先设置一下 不然modal会闪一下
      this.term.resize(80, 5)
      // 注册自适应组件
      this.plugin.fit = new FitAddon(this.termConfig)
      this.term.loadAddon(this.plugin.fit)
      // 注册搜索组件
      this.plugin.search = new SearchAddon()
      this.term.loadAddon(this.plugin.search)
      // 注册 url link组件
      this.plugin.links = new WebLinksAddon()
      this.term.loadAddon(this.plugin.links)
      // 注册自适应监听器
      window.addEventListener('resize', this.fitTerminal)
      // 注册快捷键
      this.term.attachCustomKeyEventHandler((ev) => {
        // 注册全选键 ctrl + a
        if (ev.keyCode === 65 && ev.ctrlKey && ev.type === 'keydown') {
          setTimeout(() => {
            this.term.selectAll()
          }, 10)
        }
        // 注册复制键 ctrl + c
        if (ev.keyCode === 67 && ev.ctrlKey && ev.type === 'keydown') {
          this.copySelection(false)
        }
        // 注册搜索键 ctrl + shift + f
        if (ev.keyCode === 70 && ev.ctrlKey && ev.shiftKey && ev.type === 'keydown') {
          this.$refs.search.open()
        }
      })
      setTimeout(() => {
        // fit 这里就是调用两次 不然modal的字体有毛病
        this.fitTerminal()
        this.fitTerminal()
        // 建立连接
        this.initSocket(data)
      }, 50)
    },
    initSocket(data) {
      // 打开websocket
      this.client = new WebSocket(this.$api.fileTail({ token: data.token }))
      this.client.onopen = () => {
        this.status = this.$enum.LOG_TAIL_STATUS.RUNNABLE.value
        this.$emit('open')
      }
      this.client.onerror = () => {
        this.status = this.$enum.LOG_TAIL_STATUS.ERROR.value
      }
      this.client.onclose = (e) => {
        console.log('closed', e.code, e.reason)
        this.status = this.$enum.LOG_TAIL_STATUS.CLOSE.value
        if (e.code > 4000 && e.code < 5000) {
          // 自定义错误信息
          this.term.write(`\x1b[93m${e.reason}\x1b[0m`)
        }
        this.$emit('close')
      }
      this.client.onmessage = async event => {
        this.term.write(await event.data.text())
        if (!this.fixedLog) {
          this.term.scrollToBottom()
        }
      }
    },
    fitTerminal() {
      setTimeout(() => {
        this.plugin.fit && this.plugin.fit.fit()
      }, 40)
    },
    clickTerminal() {
      this.visibleRightMenu = false
    },
    copy() {
      this.term.selectAll()
      this.$copy(this.term.getSelection())
      this.term.clearSelection()
    },
    copySelection(tips = undefined) {
      this.$copy(this.term.getSelection(), tips)
    },
    clear() {
      this.term && this.term.clear()
    },
    close() {
      this.client && this.client.close()
    },
    clickRightMenu({ key }) {
      this.visibleRightMenu = false
      rightMenuHandler[key].call(this)
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
    }
  },
  beforeDestroy() {
    this.clear()
    this.close()
    this.term && this.term.dispose()
    window.removeEventListener('resize', this.fitTerminal)
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
    white-space: nowrap;
  }

  .log-tools-fixed-right {
    min-width: 50%;
    display: flex;
    align-items: center;
    justify-content: flex-end;

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
  border-radius: 4px;
  width: 100%;
  background: #212529;
  padding: 4px 0 0 4px;

  .log-terminal {
    width: 100%;
    height: 100%;
  }
}

</style>
