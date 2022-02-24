<template>
  <a-auto-complete v-model="value"
                   :disabled="disabled"
                   :placeholder="placeholder"
                   @change="change"
                   @search="search"
                   allowClear>
    <template #dataSource>
      <a-select-option v-for="machine in visibleMachines" :key="machine.id" :value="JSON.stringify(machine)">
        {{ machine.name }}
      </a-select-option>
    </template>
  </a-auto-complete>
</template>

<script>
export default {
  name: 'MachineAutoComplete',
  props: {
    disabled: {
      type: Boolean,
      default: false
    },
    placeholder: {
      type: String,
      default: '全部'
    }
  },
  data() {
    return {
      machineList: [],
      visibleMachines: [],
      value: undefined
    }
  },
  watch: {},
  methods: {
    change(value) {
      let id
      let val = value
      try {
        const v = JSON.parse(value)
        if (typeof v === 'object') {
          id = v.id
          val = v.name
        }
      } catch (e) {
      }
      this.$emit('change', id, val)
    },
    search(value) {
      if (!value) {
        this.visibleMachines = this.machineList
        return
      }
      this.visibleMachines = this.machineList.filter(s => s.name.toLowerCase().includes(value.toLowerCase()))
    },
    reset() {
      this.value = undefined
    }
  },
  async created() {
    const machineListRes = await this.$api.getMachineList({ limit: 10000 })
    if (machineListRes.data && machineListRes.data.rows && machineListRes.data.rows.length) {
      for (const row of machineListRes.data.rows) {
        this.machineList.push({
          id: row.id,
          name: row.name,
          host: row.host
        })
      }
      this.visibleMachines = this.machineList
    }
  }
}
</script>

<style scoped>

</style>
