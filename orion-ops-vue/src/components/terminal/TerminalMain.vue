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
import { FitAddon } from 'xterm-addon-fit'
import { SearchAddon } from 'xterm-addon-search'
import { WebLinksAddon } from 'xterm-addon-web-links'
import { WebglAddon } from 'xterm-addon-webgl'
import { copyToClipboard, fitDimensions, getClipboardText } from '@/lib/utils'

import 'xterm/css/xterm.css'
import TerminalSearch from '@/components/terminal/TerminalSearch'

/**
 * 初始化terminal
 */
function initTerminal() {
  // 打开terminal
  this.term = new Terminal(this.options)
  this.term.open(this.$refs.terminal)
  // 需要先设置一下 不然modal会闪一下
  this.term.resize(80, 5)
  // 注册terminal事件
  this.term.onResize(event => terminalEventHandler.onResize.call(this, event.cols, event.rows))
  this.term.onData(event => terminalEventHandler.onData.call(this, event))
  terminalEventHandler.registerCustomerKey.call(this)

  // 注册自适应组件
  this.plugin.fit = new FitAddon()
  this.term.loadAddon(this.plugin.fit)
  // 如果不是modal 这里fit没问题
  // this.plugin.fit.fit()
  // 注册搜索组件
  this.plugin.search = new SearchAddon()
  this.term.loadAddon(this.plugin.search)
  // 注册 url link组件
  if (this.setting.enableWebLink === 1) {
    this.plugin.links = new WebLinksAddon()
    this.term.loadAddon(this.plugin.links)
  }
  // 注册 webgl web加速组件
  if (this.setting.enableWebGL === 1) {
    this.plugin.webgl = new WebglAddon()
    try {
      this.term.loadAddon(this.plugin.webgl)
    } catch (e) {
      console.error('loading addon webgl error', e)
    }
  }

  setTimeout(() => {
    // fit 这里就是调用两次 不然modal的字体有毛病
    this.fitTerminal()
    this.fitTerminal()
    // 建立连接
    this.initSocket()
  }, 50)
}

/**
 * 终端操作处理器
 */
const terminalEventHandler = {
  onResize(cols, rows) {
    // 调整大小
    terminalOperator.resize.call(this, cols, rows)
  },
  onData(event) {
    // 输入
    terminalOperator.key.call(this, event)
  },
  registerCustomerKey() {
    // 注册自定义按键
    this.term.attachCustomKeyEventHandler((ev) => {
      // 注册复制键 ctrl + insert
      // if (ev.keyCode === 45 && ev.ctrlKey && ev.type === 'keydown') {
      //   terminalEventHandler.copy.call(this)
      // }
      // 注册粘贴键 shift + insert
      // if (ev.keyCode === 45 && ev.shiftKey && ev.type === 'keydown') {
      //   terminalEventHandler.paste.call(this)
      // }
      // 注册粘贴键 ctrl + shift + v
      // if (ev.keyCode === 86 && ev.ctrlKey && ev.shiftKey && ev.type === 'keydown') {
      //   terminalEventHandler.paste.call(this)
      // }
      // 注册全选键 ctrl + a
      if (ev.keyCode === 65 && ev.ctrlKey && ev.type === 'keydown') {
        setTimeout(() => {
          this.term.selectAll()
        }, 10)
      }
      // 注册搜索键 ctrl + shift + f
      if (ev.keyCode === 70 && ev.ctrlKey && ev.shiftKey && ev.type === 'keydown') {
        this.$refs.search.open()
      }
    })
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
      terminalOperator.key.call(this, clipText)
      this.term.focus()
    })
  }
}

/**
 * 客户端操作处理器
 */
const clientHandler = {
  onopen() {
    console.log('open')
    this.status = this.$enum.TERMINAL_STATUS.UNAUTHORIZED.value
    this.$emit('initFinish', true)
    // 建立连接
    terminalOperator.connect.call(this)
    // 注册窗口大小监听器
    window.addEventListener('resize', this.debouncedWindowResize)
    // 注册心跳
    const _this = this
    this.pingThread = setInterval(() => {
      terminalOperator.ping.call(_this)
    }, 30000)
  },
  onmessage(e) {
    // 解析协议
    parseProtocol.call(this, e.data)
  },
  onerror() {
    console.log('error')
    this.status = this.$enum.TERMINAL_STATUS.ERROR.value
    this.$emit('initFinish', false)
    this.$message.error('无法连接至服务器', 2)
    this.term.write('\r\n\x1b[91mfailed to establish connection\x1b[0m')
  },
  onclose(e) {
    console.log('close')
    this.status = this.$enum.TERMINAL_STATUS.DISCONNECTED.value
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
 * 终端操作
 */
const terminalOperator = {
  connect() {
    console.log('connect')
    // xx|cols|rows|width|height|loginToken
    const width = parseInt(document.getElementsByClassName('terminal')[0].offsetWidth)
    const height = parseInt(document.getElementsByClassName('terminal')[0].offsetHeight)
    const loginToken = this.$storage.get(this.$storage.keys.LOGIN_TOKEN)
    const body = `${this.$enum.TERMINAL_OPERATOR.CONNECT.value}|${this.term.cols}|${this.term.rows}|${width}|${height}|${loginToken}`
    this.client.send(body)
  },
  resize(cols, rows) {
    // 防抖
    if (this.status !== this.$enum.TERMINAL_STATUS.CONNECTED.value) {
      return
    }
    console.log('resize', cols, rows)
    // xx|cols|rows|width|height
    const width = parseInt(document.getElementsByClassName('terminal')[0].offsetWidth)
    const height = parseInt(document.getElementsByClassName('terminal')[0].offsetHeight)
    const body = `${this.$enum.TERMINAL_OPERATOR.RESIZE.value}|${cols}|${rows}|${width}|${height}`
    this.client.send(body)
  },
  key(e) {
    if (this.status !== this.$enum.TERMINAL_STATUS.CONNECTED.value) {
      return
    }
    const body = `${this.$enum.TERMINAL_OPERATOR.KEY.value}|${e}`
    this.client.send(body)
  },
  disconnect() {
    console.log('disconnect')
    this.pingThread && clearInterval(this.pingThread)
    this.client && this.client.send(this.$enum.TERMINAL_OPERATOR.DISCONNECT.value)
  },
  ping() {
    console.log('ping')
    this.client.send(this.$enum.TERMINAL_OPERATOR.PING.value)
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
    terminalEventHandler.copy.call(this)
  },
  paste() {
    terminalEventHandler.paste.call(this)
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

/**
 * 解析协议
 */
function parseProtocol(msg) {
  if (!this.term) {
    return
  }
  const code = msg.substring(0, 3)
  const len = msg.length
  switch (code) {
    case this.$enum.WS_PROTOCOL.ACK.value:
      this.status = this.$enum.TERMINAL_STATUS.UNAUTHORIZED.value
      this.term.focus()
      break
    case this.$enum.WS_PROTOCOL.CONNECTED.value:
      this.status = this.$enum.TERMINAL_STATUS.CONNECTED.value
      this.term.focus()
      break
    case this.$enum.WS_PROTOCOL.OK.value:
      this.term.write(msg.substring(4, len))
      break
    default:
      console.log(this.$enum.valueOf(this.$enum.WS_PROTOCOL, code).label)
      break
  }
}

export default {
  name: 'TerminalMain',
  components: {
    TerminalSearch
  },
  props: {
    machineId: Number,
    terminalHeight: String
  },
  data: function() {
    return {
      term: null,
      client: null,
      plugin: {
        fit: null,
        search: null,
        links: null,
        webgl: null
      },
      status: 0,
      pingThread: null,
      setting: {
        accessToken: null,
        enableWebLink: 2,
        enableWebGL: 2
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
      this.setting.enableWebGL = setting.enableWebGL
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
      setTimeout(() => {
        const dimensions = fitDimensions(this.term, this.$refs.terminal)
        if (dimensions?.cols && dimensions?.rows) {
          this.term.resize(dimensions.cols, dimensions.rows)
        }
      }, 40)
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
        terminalOperator.key.call(this, command)
      }
    },
    disconnect() {
      terminalOperator.disconnect.call(this)
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
