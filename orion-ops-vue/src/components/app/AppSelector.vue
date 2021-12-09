<template>
  <a-select v-model="id"
            :disabled="disabled"
            :placeholder="placeholder"
            @change="$emit('change', id)"
            allowClear>
    <a-select-option v-for="app in appList"
                     :value="app.id"
                     :key="app.id">
      {{ app.name }}
    </a-select-option>
  </a-select>
</template>

<script>
export default {
  name: 'AppSelector',
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
    }
  },
  data() {
    return {
      id: undefined,
      appList: []
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
    const appListRes = await this.$api.getAppList({ limit: 10000 })
    if (appListRes.data && appListRes.data.rows && appListRes.data.rows.length) {
      for (const row of appListRes.data.rows) {
        this.appList.push({
          id: row.id,
          name: row.name
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
