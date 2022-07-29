<template>
  <div class="terminal-main" :style="{height: terminalHeight, background: this.options.theme.background}">
    <!-- terminal -->
    <div class="terminal-content">
      <!-- 右键菜单 -->
      <a-dropdown v-model="visibleRightMenu" :trigger="['contextmenu']">
        <!-- 终端 -->
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
            <a-menu-item key="paste">
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
    <!-- 事件 -->
    <div class="terminal-event-container">
      <!-- 搜索框 -->
      <TerminalSearch ref="search" :searchPlugin="plugin.search" @close="focus"/>
    </div>
  </div>
</template>

<script>
import { debounce } from 'lodash'
import { Terminal } from 'xterm'
import { SearchAddon } from 'xterm-addon-search'
import { WebLinksAddon } from 'xterm-addon-web-links'
import { copyToClipboard, fitDimensions, getClipboardText } from '@/lib/utils'
import { TERMINAL_OPERATOR, TERMINAL_STATUS, WS_PROTOCOL } from '@/lib/enum'

import 'xterm/css/xterm.css'
import TerminalSearch from '@/components/terminal/TerminalSearch'

/**
 * 初始化 terminal
 */
function initTerminal() {
  const init = () => {
    // 打开terminal
    this.term = new Terminal(this.options)
    this.term.open(this.$refs.terminal)
    // 需要先设置一下 不然modal会闪一下
    this.term.resize(1, 1)
    // 注册terminal事件
    this.term.onResize(event => this.sendResize(event.cols, event.rows))
    this.term.onData(event => this.sendKey(event))
    // 注册自定义快捷键
    this.registerCustomerKey()
    // 注册窗口大小监听器
    window.addEventListener('resize', this.debouncedWindowResize)
    // 注册搜索组件
    this.plugin.search = new SearchAddon()
    this.term.loadAddon(this.plugin.search)
    // 注册 url link组件
    if (this.setting.enableWebLink === 1) {
      this.plugin.links = new WebLinksAddon()
      this.term.loadAddon(this.plugin.links)
    }
    // 调整大小 因为 modal 必须调整两次 cols 会多1
    setTimeout(() => {
      this.fitTerminal()
      this.fitTerminal()
    }, 40)
    // 建立连接
    setTimeout(() => {
      this.initSocket()
    }, 80)
  }
  if (this.isModal) {
    // modal 有个加载过程
    setTimeout(init, 220)
  } else {
    init()
  }
}

/**
 * 客户端操作处理器
 */
const clientHandler = {
  onopen() {
    this.$emit('initFinish', true)
    // 建立连接
    this.connect()
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
        this.term.focus()
        // 注册心跳
        this.pingThread = setInterval(() => this.sendPing(), 30000)
        break
      case WS_PROTOCOL.OK.value:
        this.term.write(msg.substring(2, len))
        break
      default:
        break
    }
  },
  onerror() {
    this.status = TERMINAL_STATUS.ERROR.value
    this.$emit('initFinish', false)
    this.$message.error('无法连接至服务器', 2)
    this.term.write('\r\n\x1b[91mfailed to establish connection\x1b[0m')
  },
  onclose(e) {
    this.status = TERMINAL_STATUS.DISCONNECTED.value
    this.term.write('\r\n\x1b[91m' + e.reason + '\x1b[0m')
    // 关闭窗口大小监听器
    window.removeEventListener('resize', this.debouncedWindowResize)
    // 关闭心跳
    if (this.pingThread) {
      clearInterval(this.pingThread)
      this.pingThread = null
    }
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
  name: 'TerminalMain',
  components: {
    TerminalSearch
  },
  props: {
    machineId: Number,
    terminalHeight: String,
    isModal: Boolean
  },
  data: function() {
    return {
      term: null,
      client: null,
      plugin: {
        search: null,
        links: null
      },
      status: 0,
      pingThread: null,
      setting: {
        accessToken: null,
        enableWebLink: 2
      },
      options: {
        cursorStyle: 'bar',
        cursorBlink: true,
        fastScrollModifier: 'shift',
        fontSize: 14,
        rendererType: 'canvas',
        fontFamily: 'courier-new, courier, monospace',
        theme: {
          foreground: '#FFFFFF',
          background: '#212529'
        }
      },
      visibleRightMenu: false,
      debouncedWindowResize: debounce(this.fitTerminal, 100)
    }
  },
  watch: {
    status(e) {
      this.$emit('terminalStatusChange', e)
    }
  },
  methods: {
    init(options, setting) {
      this.options.fontSize = options.fontSize || this.options.fontSize
      this.options.fontFamily = options.fontFamily || this.options.fontFamily
      this.options.theme.foreground = options.fontColor || this.options.theme.foreground
      this.options.theme.background = options.backgroundColor || this.options.theme.background
      this.setting.accessToken = setting.accessToken
      this.setting.enableWebLink = setting.enableWebLink
      initTerminal.call(this)
    },
    initSocket() {
      // 打开websocket
      this.client = new WebSocket(this.$api.terminal({ token: this.setting.accessToken }))
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
    fitTerminal() {
      const dimensions = fitDimensions(this.term, this.$refs.terminal)
      if (dimensions?.cols && dimensions?.rows) {
        this.term.resize(dimensions.cols, dimensions.rows)
      }
    },
    clickTerminal() {
      this.visibleRightMenu = false
    },
    clickRightMenuItem({ key }) {
      this.visibleRightMenu = false
      rightMenuHandler[key].call(this)
    },
    writerCommand(command) {
      if (command) {
        this.sendKey(command)
      }
    },
    connect() {
      // xx|cols|rows|loginToken
      const loginToken = this.$storage.get(this.$storage.keys.LOGIN_TOKEN)
      const body = `${TERMINAL_OPERATOR.CONNECT.value}|${this.term.cols}|${this.term.rows}|${loginToken}`
      this.client.send(body)
    },
    sendResize(cols, rows) {
      // 防抖
      if (this.status !== TERMINAL_STATUS.CONNECTED.value) {
        return
      }
      console.log('resize', cols, rows)
      // xx|cols|rows
      const body = `${TERMINAL_OPERATOR.RESIZE.value}|${cols}|${rows}`
      this.client.send(body)
    },
    sendKey(e) {
      if (this.status !== TERMINAL_STATUS.CONNECTED.value) {
        return
      }
      const body = `${TERMINAL_OPERATOR.KEY.value}|${e}`
      this.client.send(body)
    },
    sendPing() {
      this.client.send(TERMINAL_OPERATOR.PING.value)
    },
    disconnect() {
      this.pingThread && clearInterval(this.pingThread)
      this.client && this.client.readyState === 1 && this.client.send(TERMINAL_OPERATOR.DISCONNECT.value)
    },
    registerCustomerKey() {
      // 注册自定义按键
      this.term.attachCustomKeyEventHandler((ev) => {
        // 注册全选键 ctrl + a
        // if (ev.keyCode === 65 && ev.ctrlKey && ev.type === 'keydown') {
        //   setTimeout(() => {
        //     this.term.selectAll()
        //   }, 10)
        // }
        // 注册搜索键 ctrl + shift + f
        if (ev.keyCode === 70 && ev.ctrlKey && ev.shiftKey && ev.type === 'keydown') {
          this.$refs.search.open()
        }
      })
    },
    focus() {
      this.term.focus()
    }
  }
}
</script>

<style lang="less" scoped>
.terminal-main {
  background: #212529;
  width: 100%;

  .terminal-content {
    height: 100%;

    .terminal {
      height: 100%;
    }
  }
}
</style>
