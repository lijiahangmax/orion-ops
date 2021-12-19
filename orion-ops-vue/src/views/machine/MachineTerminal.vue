<template>
  <a-layout id="terminal-layout-container">
    <!-- 机器列表 -->
    <MachineListMenu ref="machineList"
                     :selectedMachine="selectedMachine"
                     :hideBack="true"
                     :query="{status: $enum.ENABLE_STATUS.ENABLE.value}"
                     @chooseMachine="addTerminal"/>
    <!-- main -->
    <a-layout>
      <a-layout-content id="terminal-content-fixed-right" :style="{'overflow': machineTabs.length ? 'auto' : 'hidden'}">
        <!-- tabs -->
        <a-tabs v-model="activeKey"
                v-if="machineTabs.length"
                :hide-add="true"
                type="editable-card"
                @edit="removeTab"
                :tabBarStyle="{margin: 0}">
          <a-tab-pane v-for="machineTab in machineTabs" :key="machineTab.key" :tab="machineTab.name">
            <!-- 终端 -->
            <TerminalXterm :ref="'terminal' + machineTab.key" :machineId="machineTab.machineId"/>
          </a-tab-pane>
        </a-tabs>
        <!-- 无终端承载页 -->
        <div v-else>
          <TerminalBanner/>
        </div>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script>
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
      selectedMachine: []
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
