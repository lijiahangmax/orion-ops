<template>
  <!-- banner 终端 -->
  <div id="banner-terminal" ref="bannerTerminal"></div>
</template>

<script>
import { Terminal } from 'xterm'

export default {
  name: 'TerminalBanner',
  data() {
    return {}
  },
  mounted() {
    const term = new Terminal({
      cols: 360,
      rows: 96,
      cursorStyle: 'bar',
      cursorBlink: true,
      fontSize: 14,
      theme: {
        foreground: '#FFFFFF',
        background: '#212529'
      }
    })
    term.open(this.$refs.bannerTerminal)
    // 输出banner
    const banner = '\n\n\n\n\n\n\n\n\n\n \x1b[97;1m ' +
      '\t\t\t\t\t _____   ____    ______  _____   __  __           _____   ____    ____\r\n' +
      '\t\t\t\t\t/\\  __`\\/\\  _`\\ /\\__  _\\/\\  __`\\/\\ \\/\\ \\         /\\  __`\\/\\  _`\\ /\\  _`\\\r\n' +
      '\t\t\t\t\t\\ \\ \\/\\ \\ \\ \\L\\ \\/_/\\ \\/\\ \\ \\/\\ \\ \\ `\\\\ \\        \\ \\ \\/\\ \\ \\ \\L\\ \\ \\,\\L\\_\\\r\n' +
      '\t\t\t\t\t \\ \\ \\ \\ \\ \\ ,  /  \\ \\ \\ \\ \\ \\ \\ \\ \\ , ` \\  ______\\ \\ \\ \\ \\ \\ ,__/\\/_\\__ \\\r\n' +
      '\t\t\t\t\t  \\ \\ \\_\\ \\ \\ \\\\ \\  \\_\\ \\_\\ \\ \\_\\ \\ \\ \\`\\ \\/\\______\\ \\ \\_\\ \\ \\ \\/   /\\ \\L\\ \\\r\n' +
      '\t\t\t\t\t   \\ \\_____\\ \\_\\ \\_\\/\\_____\\ \\_____\\ \\_\\ \\_\\/______/\\ \\_____\\ \\_\\   \\ `\\____\\\r\n' +
      '\t\t\t\t\t    \\/_____/\\/_/\\/ /\\/_____/\\/_____/\\/_/\\/_/         \\/_____/\\/_/    \\/_____/\n\r\n' +
      '\r\n \x1b[0m' +
      '\r\n\t\t\t\t\t     \x1b[94;1m👉  TIPS: 双击左侧机器打开终端. 👈 \x1b[0m' +
      '\r\n\t\t\t\t\t     ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓' +
      '\r\n\t\t\t\t\t     ┃ \x1b[93;1m ctrl + insert (复制)\x1b[0m         ┃' +
      '\r\n\t\t\t\t\t     ┃                               ┃' +
      '\r\n\t\t\t\t\t     ┃ \x1b[93;1m shift + insert (粘贴)\x1b[0m        ┃' +
      '\r\n\t\t\t\t\t     ┃                               ┃' +
      '\r\n\t\t\t\t\t     ┃ \x1b[93;1m ctrl + shift + v (粘贴)\x1b[0m      ┃' +
      '\r\n\t\t\t\t\t     ┃                               ┃' +
      '\r\n\t\t\t\t\t     ┃ \x1b[93;1m ctrl + shift + f (搜索)\x1b[0m      ┃' +
      '\r\n\t\t\t\t\t     ┃                               ┃' +
      '\r\n\t\t\t\t\t     ┃ \x1b[93;1m shift + 鼠标滚轮 (快速移动)\x1b[0m  ┃' +
      '\r\n\t\t\t\t\t     ┃                               ┃' +
      '\r\n\t\t\t\t\t     ┃ \x1b[93;1m alt + 鼠标左键 (批量选择)\x1b[0m    ┃' +
      '\r\n\t\t\t\t\t     ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛' +
      '\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n'
    term.write(banner)
    term.focus()
  }
}
</script>

<style scoped>

</style>
