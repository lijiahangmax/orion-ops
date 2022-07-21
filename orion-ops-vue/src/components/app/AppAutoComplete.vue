<template>
  <a-auto-complete v-model="value"
                   :disabled="disabled"
                   :placeholder="placeholder"
                   @change="change"
                   @search="search"
                   allowClear>
    <template #dataSource>
      <a-select-option v-for="app in visibleApp"
                       :key="app.id"
                       :value="JSON.stringify(app)"
                       @click="choose">
        {{ app.name }}
      </a-select-option>
    </template>
  </a-auto-complete>
</template>

<script>
export default {
  name: 'AppAutoComplete',
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
      appList: [],
      visibleApp: [],
      value: undefined
    }
  },
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
      this.value = val
    },
    choose() {
      this.$nextTick(() => {
        this.$emit('choose')
      })
    },
    search(value) {
      if (!value) {
        this.visibleApp = this.appList
        return
      }
      this.visibleApp = this.appList.filter(s => s.name.toLowerCase().includes(value.toLowerCase()))
    },
    reset() {
      this.value = undefined
      this.visibleApp = this.appList
    }
  },
  async created() {
    const { data } = await this.$api.getAppList({
      limit: 10000
    })
    if (data && data.rows && data.rows.length) {
      for (const row of data.rows) {
        this.appList.push({
          id: row.id,
          name: row.name
        })
      }
      this.visibleApp = this.appList
    }
  }
}
</script>

<style scoped>

</style>
