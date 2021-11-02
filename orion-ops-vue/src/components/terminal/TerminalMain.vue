<template>
  <div class="terminal-main">
    <!-- terminal -->
    <div class="terminal-content">
      <div id="terminal" @contextmenu.prevent="openRightMenu"></div>
    </div>
    <!-- 事件 -->
    <div class="terminal-event-container">
      <!-- 右键菜单 -->
      <div id="right-menu" ref="rightMenu">
        <a-dropdown :trigger="['click']">
          <span id="right-menu-trigger"></span>
          <a-menu slot="overlay" @click="clickRightMenuItem">
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
        </a-dropdown>
      </div>
      <!-- 搜索框 -->
      <div id="search-card" v-show="search.visible">
        <a-card title="搜索" size="small">
          <a slot="extra" @click="closeSearch">
            <a-icon type="close"/>
          </a>
          <a-input class="search-input" ref="searchInput" placeholder="请输入查找内容"
                   v-model="search.value" @keyup.enter.native="searchKeywords(true)">
          </a-input>
          <div class="search-options">
            <a-row>
              <a-col :span="12">
                <a-checkbox v-model="search.regex">
                  正则匹配
                </a-checkbox>
              </a-col>
              <a-col :span="12">
                <a-checkbox v-model="search.words">
                  单词全匹配
                </a-checkbox>
              </a-col>
              <a-col :span="12">
                <a-checkbox v-model="search.matchCase">
                  区分大小写
                </a-checkbox>
              </a-col>
              <a-col :span="12">
                <a-checkbox v-model="search.incremental">
                  增量查找
                </a-checkbox>
              </a-col>
            </a-row>
          </div>
          <div class="search-buttons">
            <a-button class="terminal-search-button search-button-prev" type="primary" @click="searchKeywords(false)">上一个</a-button>
            <a-button class="terminal-search-button search-button-next" type="primary" @click="searchKeywords(true)">下一个</a-button>
          </div>
        </a-card>
      </div>
    </div>
  </div>
</template>

<script>
import { Terminal } from 'xterm'
import { FitAddon } from 'xterm-addon-fit'
import { SearchAddon } from 'xterm-addon-search'
import { WebLinksAddon } from 'xterm-addon-web-links'

import 'xterm/css/xterm.css'

/**
 * 打开终端
 */
async function openTerminal() {
  this.loading = this.$message.loading('建立连接中...', 10)
  await this.$api.accessTerminal({ machineId: this.machineId })
    .then(({ data }) => {
      // terminal-config
      this.terminalConfig.accessToken = data.accessToken
      this.terminalConfig.theme.background = data.backgroundColor
      this.terminalConfig.theme.foreground = data.fontColor
      this.terminalConfig.fontSize = data.fontSize
      this.terminalConfig.rows = this.getRows()
      // ref-bar
      this.$emit('dispatchAccessData', data)
    })
  // 注册terminal事件
  this.term = new Terminal(this.terminalConfig)
  this.term.open(document.getElementById('terminal'))
  this.term.onResize(event => terminalEventHandler.onResize.call(this, event.cols, event.rows))
  this.term.onKey(event => terminalEventHandler.onKey.call(this, event))
  terminalEventHandler.registerCustomerKey.call(this)

  // 注册terminal自适应组件
  this.plugin.fit = new FitAddon()
  this.term.loadAddon(this.plugin.fit)
  // 注册terminal搜索组件
  this.plugin.search = new SearchAddon()
  this.term.loadAddon(this.plugin.search)
  // 注册url link组件
  this.plugin.links = new WebLinksAddon()
  this.term.loadAddon(this.plugin.links)

  // 打开websocket
  this.client = new WebSocket(this.$api.terminal(this.terminalConfig.accessToken))
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
}

/**
 * 终端操作处理器
 */
const terminalEventHandler = {
  onResize(cols, rows) {
    // 调整大小
    if (this.status !== this.$enum.TERMINAL_STATUS.CONNECTED.value) {
      return
    }
    this.terminalConfig.cols = cols
    this.terminalConfig.rows = rows
    terminalOperator.resize.call(this, cols, rows)
  },
  onKey(event) {
    // 输入
    terminalOperator.key.call(this, event.key)
  },
  registerCustomerKey() {
    // 注册自定义按键
    const _this = this
    this.term.attachCustomKeyEventHandler(function(ev) {
      // 注册复制键 ctrl + insert
      if (ev.keyCode === 45 && ev.ctrlKey && ev.type === 'keydown') {
        terminalEventHandler.copy.call(_this)
      }
      // 注册粘贴键 shift + insert
      if (ev.keyCode === 45 && ev.shiftKey && ev.type === 'keydown') {
        terminalEventHandler.paste.call(_this)
      }
      // 注册粘贴键 ctrl + shift + v
      if (ev.keyCode === 86 && ev.ctrlKey && ev.shiftKey && ev.type === 'keydown') {
        terminalEventHandler.paste.call(_this)
      }
      // 注册搜索键 ctrl + shift + f
      if (ev.keyCode === 70 && ev.ctrlKey && ev.shiftKey && ev.type === 'keydown') {
        const visible = _this.search.visible
        _this.search.visible = !visible
        if (!visible) {
          _this.$nextTick(() => {
            _this.$refs.searchInput.focus()
          })
        }
      }
    })
  },
  copy() {
    // 复制
    this.$utils.copyToClipboard(this.term.getSelection())
    this.term.clearSelection()
    this.term.focus()
  },
  paste() {
    // 粘贴
    this.$utils.getClipboardText().then(clipText => {
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
    this.loading()
    // 自适应窗口
    this.plugin.fit.fit()
    this.terminalConfig.cols = parseInt(this.term.cols)
    // 建立连接
    terminalOperator.connect.call(this)
    // 注册窗口大小监听器
    window.addEventListener('resize', this.windowChange)
    // 注册心跳
    const _this = this
    this.pingThread = setInterval(function() {
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
    this.loading()
    this.$message.error('无法连接至服务器', 2)
    this.term.write('\x1B[1;3;31m\r\nfailed to establish connection\x1B[0m')
  },
  onclose(e) {
    console.log('close')
    this.status = this.$enum.TERMINAL_STATUS.DISCONNECTED.value
    this.term.write('\x1B[1;3;31m\r\n' + e.reason + '\x1B[0m')
    // 关闭窗口大小监听器
    window.removeEventListener('resize', this.windowChange)
    // 关闭心跳
    if (this.pingThread) {
      clearInterval(this.pingThread)
    }
  }
}

/**
 * 终端操作
 */
const terminalOperator = {
  connect() {
    console.log('connect')
    this.client.send(JSON.stringify({
      operate: 'connect',
      body: {
        loginToken: this.$storage.get(this.$storage.keys.LOGIN_TOKEN),
        rows: this.terminalConfig.rows,
        cols: this.terminalConfig.cols,
        width: document.getElementById('terminal').offsetWidth,
        height: document.getElementById('terminal').offsetHeight
      }
    }))
  },
  key(e) {
    if (this.status !== this.$enum.TERMINAL_STATUS.CONNECTED.value) {
      return
    }
    this.client.send(JSON.stringify({
      operate: 'key',
      body: e
    }))
  },
  resize(cols, rows) {
    if (this.status !== this.$enum.TERMINAL_STATUS.CONNECTED.value) {
      return
    }
    console.log('resize', cols, rows)
    this.client.send(JSON.stringify({
      operate: 'resize',
      body: {
        rows: rows,
        cols: cols,
        width: document.getElementById('terminal').offsetWidth,
        height: document.getElementById('terminal').offsetHeight
      }
    }))
  },
  disconnect() {
    console.log('disconnect')
    this.client.send(JSON.stringify({
      operate: 'disconnect',
      body: {}
    }))
  },
  ping() {
    console.log('ping')
    this.client.send(JSON.stringify({
      operate: 'ping',
      body: {}
    }))
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
    this.search.visible = true
    this.$nextTick(() => {
      this.$refs.searchInput.focus()
    })
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
    case '010':
      this.status = this.$enum.TERMINAL_STATUS.CONNECTED.value
      this.term.focus()
      break
    case '100':
      this.term.write(msg.substring(4, len))
      break
    default:
      break
  }
}

export default {
  name: 'TerminalMain',
  props: {
    machineId: Number
  },
  data: function() {
    return {
      loading: null,
      term: null,
      client: null,
      plugin: {
        fit: null,
        search: null,
        links: null
      },
      search: {
        visible: false,
        value: '',
        regex: false,
        words: false,
        matchCase: false,
        incremental: false
      },
      status: 0,
      pingThread: null,
      terminalConfig: {
        accessToken: null,
        rows: 36,
        cols: 180,
        cursorStyle: 'bar',
        cursorBlink: true,
        fastScrollModifier: 'shift',
        fontSize: 14,
        theme: {
          background: '#212529',
          foreground: '#FFFFFF'
        }
      }
    }
  },
  watch: {
    status: function() {
      this.$emit('terminalStatusChange', this.status)
    }
  },
  methods: {
    openTerminal() {
      // 打开终端
      openTerminal.call(this)
    },
    getRows() {
      return parseInt((document.body.clientHeight - 42) / (this.terminalConfig.fontSize + 2))
    },
    windowChange() {
      this.plugin.fit.fit()
      this.terminalConfig.cols = parseInt(this.term.cols)
      this.terminalConfig.rows = this.getRows()
      this.term.resize(this.terminalConfig.cols, this.terminalConfig.rows)
    },
    openRightMenu(e) {
      if (e.button === 2) {
        document.getElementById('right-menu-trigger').click()
        this.$refs.rightMenu.style.top = (e.offsetY + 30) + 'px'
        this.$refs.rightMenu.style.left = (e.offsetX + 10) + 'px'
        this.$refs.rightMenu.style.display = 'block'
      } else {
        this.$refs.rightMenu.style.display = 'none'
      }
    },
    clickRightMenuItem({ key }) {
      rightMenuHandler[key].call(this)
    },
    writerCommand(command) {
      if (command) {
        terminalOperator.key.call(this, command)
      }
    },
    closeSearch() {
      this.search.visible = false
      this.search.value = ''
    },
    disconnect() {
      terminalOperator.disconnect.call(this)
    },
    searchKeywords(direction) {
      if (!this.search.value) {
        return
      }
      const option = {
        regex: this.search.regex,
        wholeWord: this.search.words,
        caseSensitive: this.search.matchCase,
        incremental: this.search.incremental
      }
      let res
      if (direction) {
        res = this.plugin.search.findNext(this.search.value, option)
      } else {
        res = this.plugin.search.findPrevious(this.search.value, option)
      }
      if (!res) {
        this.$message.info('未查询到匹配项', 0.3)
      }
    }
  }
}
</script>

<style scoped>

#right-menu {
  position: absolute;
  z-index: 10;
  color: #d0ebff
}

.right-menu-item {
  padding: 0 14px;
}

#search-card {
  position: fixed;
  top: 50px;
  right: 20px;
  z-index: 200;
  width: 290px;
}

#search-card .search-input {
  width: 260px;
}

.search-options {
  margin: 12px 0;
}

.search-buttons {
  margin-top: 5px;
  display: flex;
  justify-content: flex-end;
}

.terminal-search-button {
  margin-left: 10px;
}

</style>
