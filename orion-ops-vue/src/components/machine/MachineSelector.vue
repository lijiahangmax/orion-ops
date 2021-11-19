<template>
  <a-select v-model="id"
            :disabled="disabled"
            placeholder="全部"
            @change="$emit('change', id)"
            allowClear>
    <a-select-option v-for="machine in machineList"
                     :value="machine.id"
                     :key="machine.id">
      {{ machine.name }}
    </a-select-option>
  </a-select>
</template>

<script>
export default {
  name: 'MachineSelector',
  props: {
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      id: undefined,
      machineList: []
    }
  },
  watch: {
    id(e) {
      this.$emit('change', e)
    }
  },
  methods: {
    reset() {
      this.id = undefined
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
    }
  }
}
</script>

<style scoped>

</style>
