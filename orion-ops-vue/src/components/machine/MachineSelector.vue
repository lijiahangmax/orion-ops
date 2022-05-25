<template>
  <a-select v-model="id"
            :disabled="disabled"
            :placeholder="placeholder"
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
    },
    placeholder: {
      type: String,
      default: '全部'
    },
    value: {
      type: Number,
      default: undefined
    },
    query: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    return {
      id: undefined,
      machineList: []
    }
  },
  watch: {
    value(e) {
      this.id = e
    }
  },
  methods: {
    reset() {
      this.id = undefined
    }
  },
  async created() {
    const machineListRes = await this.$api.getMachineList({
      limit: 10000,
      ...this.query
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
