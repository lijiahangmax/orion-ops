<template>
  <div class="machine-group-view-container">
    <!-- 机器组树 -->
    <MachineEditableTree class="gray-box-shadow"
                         ref="tree"
                         :machines="machines"
                         :visibleEdit="true"
                         @reloadMachine="reloadMachine"/>
    <!-- 表格 -->
    <div class="machine-group-table gray-box-shadow">
      {{ filterMachines }}
    </div>
  </div>
</template>

<script>
import { defineArrayKey } from '@/lib/utils'

import MachineEditableTree from '@/components/machine/MachineEditableTree'

export default {
  name: 'MachineGroupView',
  components: { MachineEditableTree },
  data() {
    return {
      loading: false,
      machines: [],
      filterMachines: []
    }
  },
  methods: {
    initData() {
      this.loading = true
      this.$api.getMachineList({
        limit: 10000,
        queryGroup: true
      }).then(({ data }) => {
        this.loading = false
        const rows = data.rows || []
        defineArrayKey(rows, 'visible', true)
        this.machines = rows
      }).then(() => {
        this.$refs.tree.initData()
      }).catch(() => {
        this.loading = false
      })
    },
    reloadMachine(machines) {
      this.filterMachines = machines
    }
  },
  mounted() {
    this.initData()
  }
}
</script>

<style lang="less" scoped>
.machine-group-view-container {
  display: flex;
}

.machine-group-table {
  background-color: #FFF;
  border-radius: 2px;
  margin-left: 8px;
  padding: 8px;
  width: 75%;
}
</style>
