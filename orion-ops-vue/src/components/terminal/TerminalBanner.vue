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
    const banner = '\n\n\n\n\n\n\n\n\n\n' +
      '\t\t\t\t\t _____   ____    ______  _____   __  __           _____   ____    ____\r\n' +
      '\t\t\t\t\t/\\  __`\\/\\  _`\\ /\\__  _\\/\\  __`\\/\\ \\/\\ \\         /\\  __`\\/\\  _`\\ /\\  _`\\\r\n' +
      '\t\t\t\t\t\\ \\ \\/\\ \\ \\ \\L\\ \\/_/\\ \\/\\ \\ \\/\\ \\ \\ `\\\\ \\        \\ \\ \\/\\ \\ \\ \\L\\ \\ \\,\\L\\_\\\r\n' +
      '\t\t\t\t\t \\ \\ \\ \\ \\ \\ ,  /  \\ \\ \\ \\ \\ \\ \\ \\ \\ , ` \\  ______\\ \\ \\ \\ \\ \\ ,__/\\/_\\__ \\\r\n' +
      '\t\t\t\t\t  \\ \\ \\_\\ \\ \\ \\\\ \\  \\_\\ \\_\\ \\ \\_\\ \\ \\ \\`\\ \\/\\______\\ \\ \\_\\ \\ \\ \\/   /\\ \\L\\ \\\r\n' +
      '\t\t\t\t\t   \\ \\_____\\ \\_\\ \\_\\/\\_____\\ \\_____\\ \\_\\ \\_\\/______/\\ \\_____\\ \\_\\   \\ `\\____\\\r\n' +
      '\t\t\t\t\t    \\/_____/\\/_/\\/ /\\/_____/\\/_____/\\/_/\\/_/         \\/_____/\\/_/    \\/_____/\n\r\n' +
      '\r\n\t\t\t\t\t     双击左侧机器打开终端...'
    term.write(banner)
    term.focus()
  }
}
</script>

<style scoped>

</style>
