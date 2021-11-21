<template>
  <a-modal v-model="visible" :width="450">
    <div>{{ value }}</div>
    <!-- 标题 -->
    <div slot="title">
      <span>预览</span>
      <a @click="$copy(value)">
        <a-icon class="copy-icon-right" type="copy"/>
      </a>
    </div>
    <!-- 页脚 -->
    <div slot="footer">
      <a-button @click="close">关闭</a-button>
    </div>
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
