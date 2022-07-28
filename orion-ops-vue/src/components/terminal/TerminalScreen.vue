<template>
  <a-modal v-model="visible"
           :footer="null"
           :closable="false"
           :keyboard="false"
           :maskClosable="false"
           :forceRender="true"
           :destroyOnClose="true"
           :dialogStyle="{top: '16px', padding: 0}"
           :bodyStyle="{padding: '0', background: '#121314'}"
           width="min-content">
    <!-- 头 -->
    <template #title>
      <div class="terminal-screen-header">
        <div class="terminal-screen-header-left">
          终端回放
          <span v-if="record">
            | {{ record.machineName }}
            <a-tooltip title="点击复制">
              (<span class="pointer" @click="$copy(record.machineHost, true)">{{ record.machineHost }}</span>)
            </a-tooltip>
            | {{ record.username }}
          </span>
        </div>
        <div class="terminal-screen-header-right">
          <!-- 倍速 -->
          <div class="speed-wrapper">
            <span class="normal-label">倍速</span>
            <a-select class="speed-select" :defaultValue="1" size="small" @change="changeSpeed">
              <a-select-option :value="0.75">
                0.75x
              </a-select-option>
              <a-select-option :value="1">
                1x
              </a-select-option>
              <a-select-option :value="1.5">
                1.5x
              </a-select-option>
              <a-select-option :value="2">
                2x
              </a-select-option>
              <a-select-option :value="3">
                3x
              </a-select-option>
            </a-select>
          </div>
          <!-- 关闭 -->
          <a-icon class="close-icon" type="close" title="关闭" @click="close"/>
        </div>
      </div>
    </template>
    <!-- 播放器 -->
    <div class="terminal-screen-wrapper">
      <div class="terminal-clear" ref="clear"/>
      <div class="terminal-screen-video" ref="video"/>
    </div>
  </a-modal>
</template>

<script>
import * as Player from 'asciinema-player'
import 'asciinema-player/dist/bundle/asciinema-player.css'
import { formatDate } from '@/lib/filters'

const videoConfig = {
  autoPlay: true,
  preload: true,
  idleTimeLimit: 2,
  poster: 'npt:0',
  fit: 'height'
}

export default {
  name: 'TerminalScreen',
  data() {
    return {
      visible: false,
      record: null,
      player: null,
      speed: 1,
      file: null
    }
  },
  methods: {
    open(record) {
      this.$api.getTerminalScreen({
        id: record.id
      }).then(({ data }) => {
        this.file = 'data:text/plain;base64,' + data
        this.visible = true
        this.record = record
        this.$nextTick(() => this.initPlayer())
      })
    },
    openFile(file) {
      this.file = file
      this.visible = true
      this.$nextTick(() => this.initPlayer())
    },
    changeSpeed(v) {
      if (v === this.speed) {
        return
      }
      this.speed = v
      // 防止闪烁
      this.$refs.clear.style.width = this.$refs.video.offsetWidth + 'px'
      const curr = this.player.getCurrentTime()
      this.player && this.player.dispose()
      this.initPlayer()
      setTimeout(() => {
        this.player.seek(curr)
      })
    },
    initPlayer() {
      this.player = Player.create(this.file, this.$refs.video, {
        ...videoConfig,
        speed: this.speed
      })
    },
    close() {
      this.visible = false
      setTimeout(() => {
        this.record = null
        this.player && this.player.dispose()
        this.player = null
        this.file = null
        this.speed = 1
      }, 200)
    }
  },
  filters: {
    formatDate
  }
}
</script>

<style lang="less" scoped>

.terminal-screen-header {
  display: flex;
  justify-content: space-between;
  font-size: 14px;

  .terminal-screen-header-right {
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

.terminal-clear {
  height: 1px;

}

.terminal-screen-video {
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
