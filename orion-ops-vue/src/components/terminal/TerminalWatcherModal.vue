<template>
  <a-modal v-model="visible"
           v-drag-modal
           :footer="null"
           :closable="false"
           :keyboard="false"
           :maskClosable="false"
           :forceRender="true"
           :destroyOnClose="true"
           :dialogStyle="{top: '32px', padding: 0}"
           :bodyStyle="{padding: 0}"
           :width="width">
    <!-- 头信息 -->
    <template #title>
      <div class="terminal-watcher-header">
        <div class="terminal-watcher-header-left">
          <!-- 信息 -->
          <span class="terminal-info">
           终端监视
           <span v-if="record">
             | {{ record.machineName }}
             <a-tooltip title="点击复制">
               (<span class="pointer" @click="$copy(record.machineHost, true)">{{ record.machineHost }}</span>)
             </a-tooltip>
             | {{ record.username }}
           </span>
         </span>
          <!-- 发送同步  -->
          <a-tooltip v-if="!received && TERMINAL_STATUS.CONNECTED.value === status"
                     title="发送 Ctrl + L 刷新">
            <a-icon class="header-icon sync-icon" type="sync"
                    @click="sendClear"/>
          </a-tooltip>
        </div>
        <div class="terminal-watcher-header-right">
          <!-- 状态 -->
          <a-badge class="terminal-status-badge" :count="statusLabel" :numberStyle="statusStyle"/>
          <!-- 拖拽 -->
          <a-icon class="header-icon ant-modal-draggable mr8 ml4" title="拖拽" type="border-right"/>
          <!-- 关闭 -->
          <a-icon class="header-icon" type="close" title="关闭" @click="close"/>
        </div>
      </div>
    </template>
    <!-- 终端 -->
    <div class="terminal-watcher-wrapper">
      <!-- 右键菜单 -->
      <a-dropdown v-model="visibleRightMenu" :trigger="['contextmenu']">
        <div class="terminal" ref="terminal" @click="clickTerminal"/>
        <!-- 下拉菜单 -->
        <template #overlay>
          <a-menu @click="clickRightMenuItem">
            <a-menu-item key="selectAll">
              <span class="right-menu-item"><a-icon type="profile"/>全选</span>
            </a-menu-item>
            <a-menu-item key="copy">
              <span class="right-menu-item"><a-icon type="copy"/>复制</span>
            </a-menu-item>
            <a-menu-item key="paste" v-if="record && record.readonly !== 1">
              <span class="right-menu-item"><a-icon type="snippets"/>粘贴</span>
            </a-menu-item>
            <a-menu-item key="clear">
              <span class="right-menu-item"><a-icon type="stop"/>清空</span>
            </a-menu-item>
            <a-menu-item key="openSearch">
              <span class="right-menu-item"><a-icon type="search"/>搜索</span>
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
    <!-- 搜索框 -->
    <TerminalSearch ref="search" :searchPlugin="search" @close="focus"/>
  </a-modal>
</template>

<script>
import { Terminal } from 'xterm'
import { SearchAddon } from 'xterm-addon-search'
import { formatDate } from '@/lib/filters'
import 'xterm/css/xterm.css'
import { enumValueOf, TERMINAL_STATUS, TERMINAL_OPERATOR, WS_PROTOCOL } from '@/lib/enum'
import { copyToClipboard, getClipboardText } from '@/lib/utils'
import TerminalSearch from '@/components/terminal/TerminalSearch'

/**
 * 客户端操作处理器
 */
const clientHandler = {
  onopen() {
    // xx|loginToken
    const loginToken = this.$storage.get(this.$storage.keys.LOGIN_TOKEN)
    const body = `${TERMINAL_OPERATOR.CONNECT.value}|${loginToken}`
    this.client.send(body)
  },
  onmessage({ data: msg }) {
    // 解析协议
    if (!this.term) {
      return
    }
    const code = msg.substring(0, 1)
    const len = msg.length
    switch (code) {
      case WS_PROTOCOL.CONNECTED.value:
        this.status = TERMINAL_STATUS.CONNECTED.value
        this.$message.success('已连接')
        this.term.onData(event => this.sendKey(event))
        this.term.focus()
        break
      case WS_PROTOCOL.OK.value:
        this.received = true
        this.term.write(msg.substring(2, len))
        break
      default:
        break
    }
  },
  onerror() {
    this.status = TERMINAL_STATUS.ERROR.value
    this.$message.error('当前会话无法连接', 2)
    this.term.write('\r\n\x1b[91mcurrent session cannot be connected\x1b[0m')
  },
  onclose(e) {
    this.status = TERMINAL_STATUS.DISCONNECTED.value
    this.term.write('\r\n\x1b[91m' + e.reason + '\x1b[0m')
    this.term.setOption('cursorBlink', false)
  }
}

/**
 * 右键菜单操作
 */
const rightMenuHandler = {
  selectAll() {
    this.term.selectAll()
    this.term.focus()
  },
  copy() {
    // 复制
    copyToClipboard(this.term.getSelection())
    this.term.clearSelection()
    this.term.focus()
  },
  paste() {
    // 粘贴
    getClipboardText().then(clipText => {
      this.sendKey(clipText)
      this.term.focus()
    })
  },
  clear() {
    this.term.clear()
    this.term.clearSelection()
    this.term.focus()
  },
  toTop() {
    this.term.scrollToTop()
    this.term.focus()
  },
  toBottom() {
    this.term.scrollToBottom()
    this.term.focus()
  },
  openSearch() {
    this.$refs.search.open()
  }
}

export default {
  name: 'TerminalWatcherModal',
  components: { TerminalSearch },
  data() {
    return {
      visible: false,
      status: TERMINAL_STATUS.NOT_CONNECT.value,
      TERMINAL_STATUS,
      record: null,
      term: null,
      client: null,
      width: 'max-content',
      visibleRightMenu: false,
      search: null,
      received: false
    }
  },
  computed: {
    statusLabel: function() {
      return enumValueOf(TERMINAL_STATUS, this.status).label
    },
    statusStyle: function() {
      return {
        backgroundColor: enumValueOf(TERMINAL_STATUS, this.status).color
      }
    }
  },
  methods: {
    open(record) {
      this.visible = true
      this.record = record
      this.$nextTick(() => {
        this.term = new Terminal({
          cols: record.cols,
          rows: record.rows,
          fontSize: record.cols > 120 ? 13 : 14,
          cursorStyle: 'bar',
          cursorBlink: record.readonly !== 1,
          fastScrollModifier: 'shift',
          rendererType: 'canvas',
          fontFamily: 'courier-new, courier, monospace',
          theme: {
            foreground: '#FFFFFF',
            background: '#212529'
          }
        })
        this.term.open(this.$refs.terminal)
        this.width = (this.$refs.terminal.scrollWidth + 5) + 'px'
        // 注册搜索组件
        this.search = new SearchAddon()
        this.term.loadAddon(this.search)
        // 注册自定义按键
        this.term.attachCustomKeyEventHandler((ev) => {
          // 注册搜索键 ctrl + shift + f
          if (ev.keyCode === 70 && ev.ctrlKey && ev.shiftKey && ev.type === 'keydown') {
            this.$refs.search.open()
          }
        })
        // 建立连接
        this.initSocket()
      })
    },
    initSocket() {
      // 打开websocket
      this.client = new WebSocket(this.$api.terminalWatcher({ token: this.record.token }))
      this.client.onopen = event => {
        clientHandler.onopen.call(this, event)
      }
      this.client.onerror = event => {
        clientHandler.onerror.call(this, event)
      }
      this.client.onclose = event => {
        clientHandler.onclose.call(this, event)
      }
      this.client.onmessage = event => {
        clientHandler.onmessage.call(this, event)
      }
    },
    sendKey(e) {
      if (this.status !== TERMINAL_STATUS.CONNECTED.value) {
        return
      }
      if (this.record.readonly === 1) {
        this.$message.destroy()
        this.$message.warn('当前为只读模式')
        return
      }
      const body = `${TERMINAL_OPERATOR.KEY.value}|${e}`
      this.client.send(body)
    },
    sendClear() {
      if (this.status !== TERMINAL_STATUS.CONNECTED.value) {
        return
      }
      this.received = true
      this.client.send(TERMINAL_OPERATOR.CLEAR.value)
      this.term.focus()
    },
    clickTerminal() {
      this.visibleRightMenu = false
    },
    clickRightMenuItem({ key }) {
      this.visibleRightMenu = false
      rightMenuHandler[key].call(this)
    },
    close() {
      this.visible = false
      this.$refs.search.closeSearch()
      this.client && this.client.readyState === 1 && this.client.close()
      setTimeout(() => {
        this.record = null
        this.status = TERMINAL_STATUS.NOT_CONNECT.value
        this.term && this.term.dispose()
        this.search && this.search.dispose()
        this.term = null
        this.width = 'max-content'
        this.received = false
      }, 200)
    },
    focus() {
      this.term.focus()
    }
  },
  filters: {
    formatDate
  }
}
</script>

<style lang="less" scoped>

.terminal-watcher-header {
  display: flex;
  justify-content: space-between;
  font-size: 14px;

  .terminal-watcher-header-left, .terminal-watcher-header-right {
    display: flex;
    align-items: center;
  }

  .speed-select {
    width: 68px;
    margin: 0 8px 0 4px;
  }
}

.terminal-status-badge {
  margin-right: 8px;
}

.sync-icon {
  color: #9254DE;
  margin-left: 8px;
}

.header-icon {
  cursor: pointer;
  font-size: 18px;
  transition: .2s;
}

.header-icon:hover {
  color: #1890FF;
}

.terminal-watcher-video {
  height: calc(100vh - 81px)
}

::v-deep .ant-modal-header {
  padding: 8px 10px 8px 16px;
  border-radius: 2px 2px 0 0;
}

</style>
