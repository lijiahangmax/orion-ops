<template>
  <div class="terminal-body" :style="{height, background: options.theme.background}">
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
    <!-- 搜索框 -->
    <TerminalSearch ref="search" :searchPlugin="plugin.search" @close="focus"/>
  </div>
</template>

<script>
import { debounce } from 'lodash'
import { Terminal } from 'xterm'
import { SearchAddon } from 'xterm-addon-search'
import { FitAddon } from 'xterm-addon-fit'
import { WebLinksAddon } from 'xterm-addon-web-links'
import { copyToClipboard, getClipboardText } from '@/lib/utils'
import { TERMINAL_CLIENT_OPERATOR, TERMINAL_STATUS, WS_PROTOCOL } from '@/lib/enum'
import 'xterm/css/xterm.css'
import TerminalSearch from '@/components/terminal/TerminalSearch'

/**
 * 默认配置
 */
const options = {
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
}

/**
 * 客户端操作处理器
 */
const clientHandler = {
  onopen() {
    // 发送认证信息 xx|cols|rows|loginToken
    const loginToken = this.$storage.get(this.$storage.keys.LOGIN_TOKEN)
    const body = `${TERMINAL_CLIENT_OPERATOR.CONNECT.value}|${this.term.cols}|${this.term.rows}|${loginToken}`
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
      case WS_PROTOCOL.OK.value:
        this.term.write(msg.substring(2, len))
        break
      case WS_PROTOCOL.CONNECTED.value:
        this.onConnected()
        break
      case WS_PROTOCOL.PING.value:
        this.sendPong()
        break
      default:
        break
    }
  },
  onerror() {
    this.status = TERMINAL_STATUS.ERROR.value
    this.$message.error('无法连接至服务器', 2)
    this.term.write('\r\n\x1b[91mfailed to establish connection\x1b[0m')
  },
  onclose(e) {
    this.status = TERMINAL_STATUS.DISCONNECTED.value
    this.term.write('\r\n\x1b[91m' + e.reason + '\x1b[0m')
    this.term.setOption('cursorBlink', false)
    // 关闭窗口大小监听器
    window.removeEventListener('resize', this.debouncedWindowResize)
    // 关闭心跳
    this.pingThread && clearInterval(this.pingThread)
    this.pingThread = null
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
    this.doCopy()
  },
  paste() {
    getClipboardText().then(clipText => {
      this.term.paste(clipText)
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
  name: 'TerminalBody',
  components: {
    TerminalSearch
  },
  props: {
    height: {
      type: String,
      default: '100%'
    }
  },
  data: function() {
    return {
      term: null,
      client: null,
      plugin: {
        fit: null,
        search: null,
        links: null
      },
      status: null,
      pingThread: null,
      accessToken: null,
      enableWebLink: null,
      options: { ...options },
      visibleRightMenu: false,
      debouncedWindowResize: debounce(this.fitTerminal, 100)
    }
  },
  watch: {
    status(e) {
      this.$emit('changeStatus', e)
    }
  },
  methods: {
    init(options) {
      // terminal选项
      this.status = TERMINAL_STATUS.NOT_CONNECT.value
      this.options.theme.background = options.backgroundColor
      this.options.theme.foreground = options.fontColor
      this.options.fontSize = options.fontSize
      this.options.fontFamily = options.fontFamily
      this.accessToken = options.accessToken
      this.enableWebLink = options.enableWebLink
      // 初始化
      this.$nextTick(() => {
        // 打开terminal
        this.term = new Terminal(this.options)
        this.term.open(this.$refs.terminal)
        // 注册自适应组件
        this.plugin.fit = new FitAddon()
        this.term.loadAddon(this.plugin.fit)
        // 注册搜索组件
        this.plugin.search = new SearchAddon()
        this.term.loadAddon(this.plugin.search)
        // 注册 url link组件
        if (this.enableWebLink === 1) {
          this.plugin.links = new WebLinksAddon()
          this.term.loadAddon(this.plugin.links)
        }
        this.fitTerminal()
        // 建立连接
        this.initSocket()
      })
    },
    initSocket() {
      // 打开websocket
      this.client = new WebSocket(this.$api.terminal({ token: this.accessToken }))
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
      const dimensions = this.plugin.fit && this.plugin.fit.proposeDimensions()
      if (!dimensions) {
        return
      }
      if (dimensions?.cols && dimensions?.rows) {
        this.term.resize(dimensions.cols, dimensions.rows)
      }
    },
    onConnected() {
      this.status = TERMINAL_STATUS.CONNECTED.value
      // 注册心跳
      this.pingThread = setInterval(() => this.sendPing(), 15000)
      // 注册 terminal 事件
      this.term.onResize(event => this.sendResize(event.cols, event.rows))
      this.term.onData(event => this.sendKey(event))
      // 注册自定义快捷键
      this.term.attachCustomKeyEventHandler(event => {
        event.returnvalue = false
        window.event.returnvalue = false
        // 注册搜索键 ctrl + shift + c
        if (event.keyCode === 67 && event.ctrlKey && event.shiftKey && event.type === 'keydown') {
          this.doCopy()
        }
        // 注册搜索键 ctrl + shift + f
        if (event.keyCode === 70 && event.ctrlKey && event.shiftKey && event.type === 'keydown') {
          this.$refs.search.open()
        }
      })
      // 注册窗口大小监听器
      window.addEventListener('resize', this.debouncedWindowResize)
      this.term.focus()
    },
    clickTerminal() {
      this.visibleRightMenu = false
    },
    clickRightMenuItem({ key }) {
      this.visibleRightMenu = false
      rightMenuHandler[key].call(this)
    },
    doCopy() {
      copyToClipboard(this.term.getSelection())
      this.term.clearSelection()
      this.term.focus()
    },
    sendResize(cols, rows) {
      // 防抖
      if (this.status !== TERMINAL_STATUS.CONNECTED.value) {
        return
      }
      // xx|cols|rows
      const body = `${TERMINAL_CLIENT_OPERATOR.RESIZE.value}|${cols}|${rows}`
      this.client.send(body)
    },
    sendKey(e) {
      if (this.status !== TERMINAL_STATUS.CONNECTED.value) {
        return
      }
      const body = `${TERMINAL_CLIENT_OPERATOR.KEY.value}|${e}`
      this.client.send(body)
    },
    sendPing() {
      this.client.send(TERMINAL_CLIENT_OPERATOR.PING.value)
    },
    sendPong() {
      this.client.send(TERMINAL_CLIENT_OPERATOR.PONG.value)
    },
    focus() {
      this.term.focus()
    },
    dispose() {
      // 关闭 terminal
      this.term && this.term.dispose()
      this.plugin.fit && this.plugin.fit.dispose()
      this.plugin.search && this.plugin.search.dispose()
      this.plugin.links && this.plugin.links.dispose()
      // 关闭 websocket
      this.client && this.client.readyState === 1 && this.client.close()
      this.client = null
      // 清除 ping
      this.pingThread && clearInterval(this.pingThread)
      this.pingThread = null
    }
  },
  created() {
    document.onkeydown = () => {
      // 禁用 ctrl + shift + c
      if (window.event.ctrlKey && window.event.shiftKey && window.event.keyCode === 67) {
        return false
      }
    }
  }
}
</script>

<style lang="less" scoped>
.terminal-body {
  background: #212529;
  width: 100%;

  .terminal {
    width: 100%;
    height: 100%;
  }
}
</style>
