<template>
  <div class="machine-list-container">
    <!-- 切换视图提示 -->
    <a-tooltip v-if="changeViewTips"
               placement="right"
               :title="changeViewTips"
               :defaultVisible="true"
               :autoAdjustOverflow="false">
      <span class="change-view-trigger"/>
    </a-tooltip>
    <!-- 机器列表 -->
    <MachineListView v-if="listView" @openTerminal="openTerminal"/>
    <!-- 机器分组 -->
    <MachineGroupView v-else/>
    <!-- 终端最小化 -->
    <div class="terminal-minimize-container">
      <a-card v-for="minimizeTerminal of minimizeTerminalArr"
              :key="minimizeTerminal.terminalId"
              :title="`${minimizeTerminal.name} (${minimizeTerminal.host})`"
              class="terminal-minimize-item pointer"
              size="small"
              @click="maximizeTerminal(minimizeTerminal.terminalId)">
        <!-- 关闭按钮 -->
        <template #extra>
          <a-icon class="ml4 pointer"
                  type="close"
                  title="关闭"
                  @click.stop="closeMinimizeTerminal(minimizeTerminal.terminalId)"/>
        </template>
      </a-card>
    </div>
    <!-- 事件 -->
    <div class="machine-list-event-container">
      <!-- 终端模态框 -->
      <div v-if="openTerminalArr.length">
        <TerminalModal v-for="openTerminal of openTerminalArr"
                       :key="openTerminal.terminalId"
                       :ref='`terminalModal${openTerminal.terminalId}`'
                       :visibleMinimize="true"
                       @close="closedTerminal"
                       @minimize="minimizeTerminal"/>
      </div>
    </div>
  </div>
</template>

<script>
import MachineListView from '@/views/machine/MachineListView'
import MachineGroupView from '@/views/machine/MachineGroupView'
import TerminalModal from '@/components/terminal/TerminalModal'

export default {
  name: 'MachineList',
  components: {
    TerminalModal,
    MachineGroupView,
    MachineListView
  },
  data() {
    return {
      changeViewTips: undefined,
      listView: true,
      openTerminalArr: [],
      minimizeTerminalArr: []
    }
  },
  methods: {
    onHeaderEvent(event) {
      this[event] && this[event]()
    },
    changeView() {
      this.listView = !this.listView
      this.$storage.set(this.$storage.keys.MACHINE_VIEW, this.listView ? 'list' : 'group')
      this.showChangeViewTips()
    },
    showChangeViewTips() {
      const listView = this.listView
      this.changeViewTips = listView ? '点击切换为分组视图' : '点击切换为列表视图'
      setTimeout(() => {
        if (listView === this.listView) {
          this.changeViewTips = undefined
        }
      }, 3000)
    },
    openTerminal(e, record) {
      if (!e.ctrlKey) {
        e.preventDefault()
        // 打开模态框
        const now = Date.now()
        this.openTerminalArr.push({
          name: record.name,
          terminalId: now
        })
        this.$nextTick(() => {
          this.$refs[`terminalModal${now}`][0].open(record, now)
        })
        return false
      } else {
        // 跳转页面
        return true
      }
    },
    closedTerminal(terminalId) {
      for (let i = 0; i < this.openTerminalArr.length; i++) {
        if (this.openTerminalArr[i].terminalId === terminalId) {
          this.openTerminalArr.splice(i, 1)
        }
      }
    },
    minimizeTerminal(m) {
      this.minimizeTerminalArr.push(m)
    },
    maximizeTerminal(terminalId) {
      const refs = this.$refs[`terminalModal${terminalId}`]
      for (let i = 0; i < this.minimizeTerminalArr.length; i++) {
        if (this.minimizeTerminalArr[i].terminalId === terminalId) {
          this.minimizeTerminalArr.splice(i, 1)
        }
      }
      refs && refs[0] && refs[0].maximize()
    },
    closeMinimizeTerminal(terminalId) {
      const refs = this.$refs[`terminalModal${terminalId}`]
      for (let i = 0; i < this.minimizeTerminalArr.length; i++) {
        if (this.minimizeTerminalArr[i].terminalId === terminalId) {
          this.minimizeTerminalArr.splice(i, 1)
        }
      }
      refs && refs[0] && refs[0].close()
    },
    closeAllMinimizeTerminal() {
      for (const { terminalId } of this.minimizeTerminalArr) {
        const refs = this.$refs[`terminalModal${terminalId}`]
        refs && refs[0] && refs[0].close()
      }
    }
  },
  created() {
    // 读取视图
    const view = this.$storage.get(this.$storage.keys.MACHINE_VIEW)
    this.listView = !view || view === 'list'
  },
  mounted() {
    // 显示提示
    setTimeout(() => {
      this.showChangeViewTips()
    }, 100)
  },
  beforeDestroy() {
    // 关闭所有最小化的终端
    this.closeAllMinimizeTerminal()
  }
}
</script>

<style lang="less" scoped>
.change-view-trigger {
  width: 72px;
  height: 48px;
  display: inline-block;
  position: absolute;
  top: -66px;
}

.terminal-minimize-container {
  position: fixed;
  right: 16px;
  bottom: 16px;
  z-index: 10;
  display: flex;
  flex-wrap: wrap-reverse;
  justify-content: flex-end;

  .terminal-minimize-item {
    width: 200px;
    box-shadow: 0 3px 4px #DEE2E6;
    border-radius: 4px;
    margin: 1px 0.5px;
  }

}

</style>
