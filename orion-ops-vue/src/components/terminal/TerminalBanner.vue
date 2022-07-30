<template>
  <!-- banner 终端 -->
  <div id="banner-terminal" ref="bannerTerminal"></div>
</template>

<script>
import { Terminal } from 'xterm'

export default {
  name: 'TerminalBanner',
  data() {
    return {
      term: undefined,
      machines: [],
      curr: 0,
      nameMax: 0,
      hostMax: 0
    }
  },
  methods: {
    renderMachines(machines) {
      this.machines = machines
      // 拼接提示
      this.term.write('\t\t\x1b[94;1m✔  上下选择机器后回车打开终端\x1b[0m\n')
      // 隐藏光标
      this.term.write('\x1b[?25l')
      // 计算最长长度
      this.nameMax = Math.max.apply(null, this.machines.map(s => this.getLength(s.name))) + 3
      this.hostMax = Math.max.apply(null, this.machines.map(s => this.getLength(s.host))) + 5
      // 加载机器信息
      for (let i = 0; i < this.machines.length; i++) {
        this.term.write('\n ' + this.getMachineLine(i, i === 0))
      }
      // 鼠标上移
      this.term.write(`\x1B[${this.machines.length - 1}A`)
      // 监听事件
      this.term.onKey(event => this.onKeyboard(event))
    },
    getMachineLine(i, selected) {
      const machine = this.machines[i]
      const line = `${this.padString(machine.name, this.nameMax)} ${this.padString('(' + machine.host + ')', this.hostMax)}`
      let stainLine
      if (selected) {
        stainLine = `\t\t\x1b[46;1m👉  ${line} \x1b[0m`
      } else {
        stainLine = `\t\t\x1b[95;1m   ${line} \x1b[0m`
      }
      return stainLine
    },
    onKeyboard(event) {
      // 上移
      if (event.domEvent.code === 'ArrowUp') {
        if (this.curr === 0) {
          return
        }
        // 清除行
        this.term.write('\x1b[1000D\x1b[K')
        // 重新写入
        this.term.write(' ' + this.getMachineLine(this.curr, false))
        this.curr -= 1
        this.term.write(event.key)
        this.term.write('\x1b[1000D\x1b[K')
        this.term.write(' ' + this.getMachineLine(this.curr, true))
      }
      // 下移
      if (event.domEvent.code === 'ArrowDown') {
        if (this.curr === this.machines.length - 1) {
          return
        }
        // 清除行
        this.term.write('\x1b[1000D\x1b[K')
        // 重新写入
        this.term.write(' ' + this.getMachineLine(this.curr, false))
        this.curr += 1
        this.term.write(event.key)
        this.term.write('\x1b[1000D\x1b[K')
        this.term.write(' ' + this.getMachineLine(this.curr, true))
      }
      // 回车
      if (event.domEvent.code === 'Enter') {
        this.$emit('open', this.machines[this.curr].id)
      }
    },
    getLength(s) {
      let len = 0
      for (let i = 0; i < s.length; i++) {
        len += s.codePointAt(i) > 255 ? 2 : 1
      }
      return len
    },
    padString(s, len) {
      return s + ' '.repeat(len - this.getLength(s))
    },
    focus() {
      this.term.focus()
    }
  },
  mounted() {
    this.term = new Terminal({
      cols: 360,
      rows: 96,
      cursorStyle: 'bar',
      cursorBlink: true,
      fontSize: 14,
      convertEol: true,
      theme: {
        foreground: '#FFFFFF',
        background: '#212529'
      }
    })
    this.term.open(this.$refs.bannerTerminal)
    // 输出banner
    const banner = '\n\n\n\n\n \x1b[96;1m ' +
      '\t\t\t\t\t\t _____   ____    ______  _____   __  __           _____   ____    ____\n' +
      '\t\t\t\t\t\t/\\  __`\\/\\  _`\\ /\\__  _\\/\\  __`\\/\\ \\/\\ \\         /\\  __`\\/\\  _`\\ /\\  _`\\\n' +
      '\t\t\t\t\t\t\\ \\ \\/\\ \\ \\ \\L\\ \\/_/\\ \\/\\ \\ \\/\\ \\ \\ `\\\\ \\        \\ \\ \\/\\ \\ \\ \\L\\ \\ \\,\\L\\_\\\n' +
      '\t\t\t\t\t\t \\ \\ \\ \\ \\ \\ ,  /  \\ \\ \\ \\ \\ \\ \\ \\ \\ , ` \\  ______\\ \\ \\ \\ \\ \\ ,__/\\/_\\__ \\\n' +
      '\t\t\t\t\t\t  \\ \\ \\_\\ \\ \\ \\\\ \\  \\_\\ \\_\\ \\ \\_\\ \\ \\ \\`\\ \\/\\______\\ \\ \\_\\ \\ \\ \\/   /\\ \\L\\ \\\n' +
      '\t\t\t\t\t\t   \\ \\_____\\ \\_\\ \\_\\/\\_____\\ \\_____\\ \\_\\ \\_\\/______/\\ \\_____\\ \\_\\   \\ `\\____\\\n' +
      '\t\t\t\t\t\t    \\/_____/\\/_/\\/ /\\/_____/\\/_____/\\/_/\\/_/         \\/_____/\\/_/    \\/_____/\n' +
      '\n\x1b[0m' +
      '\n\t\t\x1b[94;1m✔  双击左侧机器打开终端\x1b[0m\n'
    this.term.write(banner)
    this.term.focus()
  }
}
</script>

<style scoped>

</style>
