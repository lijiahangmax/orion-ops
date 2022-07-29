<template>
  <a-modal v-model="visible"
           :footer="null"
           :closable="false"
           :keyboard="false"
           :maskClosable="false"
           :forceRender="true"
           :destroyOnClose="true"
           :dialogStyle="{top: '16px', padding: 0}"
           :bodyStyle="{padding: '0'}"
           :width="width">
    <!-- 头 -->
    <template #title>
      <div class="terminal-watcher-header">
        <div class="terminal-watcher-header-left">
          终端监视
          <span v-if="record">
            | {{ record.machineName }}
            <a-tooltip title="点击复制">
              (<span class="pointer" @click="$copy(record.machineHost, true)">{{ record.machineHost }}</span>)
            </a-tooltip>
            | {{ record.username }}
          </span>
        </div>
        <div class="terminal-watcher-header-right">
          <!-- 关闭 -->
          <a-icon class="close-icon" type="close" title="关闭" @click="close"/>
        </div>
      </div>
    </template>
    <!-- 终端 -->
    <div class="terminal-watcher-wrapper">
      <div class="terminal" ref="terminal"/>
    </div>
  </a-modal>
</template>

<script>
import { Terminal } from 'xterm'
import { formatDate } from '@/lib/filters'
import 'xterm/css/xterm.css'
import { TERMINAL_OPERATOR, WS_PROTOCOL } from '@/lib/enum'

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
        this.$message.success('已连接, 等待客户端操作')
        this.term.focus()
        break
      case WS_PROTOCOL.OK.value:
        this.term.write(msg.substring(2, len))
        break
      default:
        break
    }
  },
  onerror() {
    this.$message.error('当前会话无法连接', 2)
    this.term.write('\r\n\x1b[91mcurrent session cannot be connected\x1b[0m')
  },
  onclose(e) {
    this.term.write('\r\n\x1b[91m' + e.reason + '\x1b[0m')
  }
}

export default {
  name: 'TerminalWatcherModal',
  data() {
    return {
      visible: false,
      record: null,
      term: null,
      client: null,
      width: 'max-content'
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
          cursorBlink: true,
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
    close() {
      this.visible = false
      setTimeout(() => {
        this.record = null
        this.term && this.term.dispose()
        this.term = null
        this.width = 'max-content'
      }, 200)
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

  .terminal-watcher-header-right {
    display: flex;
    align-items: center;
  }

  .speed-select {
    width: 68px;
    margin: 0 8px 0 4px;
  }

  .close-icon {
    transition: .2s;
  }

  .close-icon:hover {
    color: #1890FF;
  }
}

.terminal-watcher-video {
  height: calc(100vh - 81px)
}

/deep/ .ant-modal-header {
  padding: 8px 16px;
  border-radius: 2px 2px 0 0;
}

/deep/ .asciinema-player {
  border-radius: 0 0 2px 2px;
}
</style>
