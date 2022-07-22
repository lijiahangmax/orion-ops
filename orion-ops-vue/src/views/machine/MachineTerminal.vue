<template>
  <a-layout id="terminal-layout-container">
    <!-- 机器列表 -->
    <MachineListMenu ref="machineList"
                     theme="light"
                     :selectedMachine="selectedMachine"
                     :query="machineQuery"
                     @chooseMachine="addTerminal"
                     @changeCollapse="fit"/>
    <!-- main -->
    <a-layout>
      <a-layout-content id="terminal-content-fixed-right" :style="{'overflow': machineTabs.length ? 'auto' : 'hidden'}">
        <!-- tabs -->
        <a-tabs v-model="activeKey"
                v-if="machineTabs.length"
                :hideAdd="true"
                type="editable-card"
                @edit="removeTab"
                :tabBarStyle="{margin: 0}">
          <a-tab-pane v-for="machineTab in machineTabs" :key="machineTab.key" :forceRender="true">
            <!-- tab -->
            <template #tab>
              <span class="usn">{{ machineTab.name }}</span>
            </template>
            <!-- 终端 -->
            <TerminalXterm :ref="'terminal' + machineTab.key"
                           wrapperHeight="calc(100vh - 40px)"
                           terminalHeight="calc(100vh - 80px)"
                           :machineId="machineTab.machineId"
                           :visibleHeader="true"/>
          </a-tab-pane>
        </a-tabs>
        <!-- 无终端承载页 -->
        <div v-show="!machineTabs.length">
          <TerminalBanner ref="banner" @open="addTerminal"/>
        </div>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script>
import { ENABLE_STATUS } from '@/lib/enum'
import MachineListMenu from '@/components/machine/MachineListMenu'
import TerminalXterm from '@/components/terminal/TerminalXterm'
import TerminalBanner from '@/components/terminal/TerminalBanner'

export default {
  name: 'MachineTerminal',
  components: {
    MachineListMenu,
    TerminalXterm,
    TerminalBanner
  },
  data() {
    return {
      activeKey: null,
      machineTabs: [],
      newTabIndex: 0,
      selectedMachine: [],
      machineQuery: { status: ENABLE_STATUS.ENABLE.value }
    }
  },
  watch: {
    activeKey(k) {
      if (k === null) {
        document.title = '终端'
        return
      }
      const machineTabs = this.machineTabs.filter(machineTab => machineTab.key === k)
      if (machineTabs.length) {
        document.title = `${machineTabs[0].name} | ${machineTabs[0].host}`
      }
      const $ref = this.$refs['terminal' + k]
      if ($ref && $ref.length) {
        setTimeout(() => {
          // 先上再下 防止界面在最下进度条在最上面
          $ref[0].focus()
          $ref[0].toTop()
          $ref[0].toBottom()
        }, 50)
      }
    }
  },
  methods: {
    addTerminal(id) {
      const filterMachines = this.$refs.machineList.list.filter(m => m.id === id)
      if (filterMachines.length) {
        this.activeKey = this.newTabIndex++
        this.machineTabs.push({
          key: this.activeKey,
          name: filterMachines[0].name,
          host: filterMachines[0].host,
          machineId: id
        })
      }
    },
    removeTab(targetKey) {
      let activeKey = this.activeKey
      let lastIndex
      this.machineTabs.forEach((machineTab, i) => {
        if (machineTab.key === targetKey) {
          lastIndex = i - 1
        }
      })
      const $ref = this.$refs['terminal' + targetKey]
      if ($ref && $ref.length) {
        $ref[0].disconnect()
        $ref[0].dispose()
      }
      const machineTabs = this.machineTabs.filter(machineTab => machineTab.key !== targetKey)
      if (machineTabs.length && activeKey === targetKey) {
        if (lastIndex >= 0) {
          activeKey = machineTabs[lastIndex].key
        } else {
          activeKey = machineTabs[0].key
        }
      }
      if (!machineTabs.length) {
        activeKey = null
      }
      this.machineTabs = machineTabs
      this.activeKey = activeKey
    },
    fit() {
      for (const machineTab of this.machineTabs) {
        const $ref = this.$refs['terminal' + machineTab.key]
        if ($ref && $ref.length) {
          setTimeout(() => {
            $ref[0].fitTerminal()
          }, 300)
        }
      }
    }
  },
  created() {
    if (this.$route.params.id) {
      this.selectedMachine.push(parseInt(this.$route.params.id))
    }
    window.onbeforeunload = function() {
      return confirm('系统可能不会保存您所做的更改')
    }
  },
  async mounted() {
    await this.$refs.machineList.getMachineList()
    if (this.selectedMachine.length) {
      this.addTerminal(this.selectedMachine[0])
    }
    this.$refs.banner.renderMachines(this.$refs.machineList.list)
  }
}
</script>

<style lang="less" scoped>

#terminal-layout-container {
  height: 100vh;
}

#terminal-content-fixed-right {
  background-color: #FFF;
}

</style>
