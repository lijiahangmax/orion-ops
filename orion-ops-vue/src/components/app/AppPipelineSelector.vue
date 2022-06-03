<template>
  <a-select v-model="id"
            :disabled="disabled"
            :placeholder="placeholder"
            @change="$emit('change', id)"
            allowClear>
    <a-select-option v-for="pipeline in pipelineList"
                     :value="pipeline.id"
                     :key="pipeline.id">
      {{ pipeline.name }}
    </a-select-option>
  </a-select>
</template>

<script>
export default {
  name: 'AppPipelineSelector',
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
      pipelineList: []
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
    // 读取当前环境
    const activeProfile = this.$storage.get(this.$storage.keys.ACTIVE_PROFILE)
    if (!activeProfile) {
      this.$message.warning('请先维护应用环境')
      return
    }
    const pipelineListRes = await this.$api.getAppPipelineList({
      profileId: JSON.parse(activeProfile).id,
      limit: 10000
    })
    if (pipelineListRes.data && pipelineListRes.data.rows && pipelineListRes.data.rows.length) {
      for (const row of pipelineListRes.data.rows) {
        this.pipelineList.push({
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
