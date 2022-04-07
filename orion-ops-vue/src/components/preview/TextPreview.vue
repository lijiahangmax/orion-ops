<template>
  <a-modal v-model="visible"
           v-drag-modal
           :width="450"
           :footer="null"
           :destroyOnClose="true"
           @close="close">
    <div>{{ value }}</div>
    <!-- 标题 -->
    <template #title>
      <span>预览</span>
      <a @click="$copy(value)" title="复制">
        <a-icon class="copy-icon-right" type="copy"/>
      </a>
    </template>
  </a-modal>
</template>

<script>
export default {
  name: 'TextPreview',
  data() {
    return {
      visible: false,
      value: null
    }
  },
  methods: {
    preview(value) {
      this.visible = true
      this.value = value
    },
    close() {
      this.visible = false
      this.value = null
    }
  }
}
</script>

<style scoped>

</style>
