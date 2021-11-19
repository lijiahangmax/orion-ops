<template>
  <a-select
    v-model="value"
    :disabled="disabled"
    mode="multiple"
    allowClear
    style="width: 100%"
    placeholder="请选择机器"
    option-label-prop="label">
    <a-select-option v-for="machine in machineList" :key="machine.id" :value="machine.id" :label="machine.name">
      {{ `${machine.name} (${machine.host})` }}
    </a-select-option>
  </a-select>
</template>

<script>
export default {
  name: 'MachineMultiSelector',
  props: {
    query: {
      type: Object,
      default: () => {
        return {}
      }
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      machineList: [],
      value: []
    }
  },
  watch: {
    value(e) {
      this.$emit('change', e)
    }
  },
  methods: {
    reset() {
      this.value = []
    }
  },
  async created() {
    const machineListRes = await this.$api.getMachineList({
      ...this.query,
      limit: 10000
    })
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
