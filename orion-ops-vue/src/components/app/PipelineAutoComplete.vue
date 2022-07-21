<template>
  <a-auto-complete v-model="value"
                   :disabled="disabled"
                   :placeholder="placeholder"
                   @change="change"
                   @search="search"
                   allowClear>
    <template #dataSource>
      <a-select-option v-for="pipeline in visiblePipeline"
                       :key="pipeline.id"
                       :value="JSON.stringify(pipeline)"
                       @click="choose">
        {{ pipeline.name }}
      </a-select-option>
    </template>
  </a-auto-complete>
</template>

<script>
export default {
  name: 'PipelineAutoComplete',
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
      pipelineList: [],
      visiblePipeline: [],
      value: undefined
    }
  },
  methods: {
    async loadData(profileId) {
      this.pipelineList = []
      this.visiblePipeline = []
      const { data } = await this.$api.getAppPipelineList({
        profileId,
        limit: 10000
      })
      if (data && data.rows && data.rows.length) {
        for (const row of data.rows) {
          this.pipelineList.push({
            id: row.id,
            name: row.name
          })
        }
        this.visiblePipeline = this.pipelineList
      }
    },
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
        this.visiblePipeline = this.pipelineList
        return
      }
      this.visiblePipeline = this.pipelineList.filter(s => s.name.toLowerCase().includes(value.toLowerCase()))
    },
    reset() {
      this.value = undefined
      this.visiblePipeline = this.pipelineList
    }
  }
}
</script>

<style scoped>

</style>
