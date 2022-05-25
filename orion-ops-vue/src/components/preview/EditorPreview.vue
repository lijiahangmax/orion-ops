<template>
  <a-modal v-model="visible"
           :width="650"
           :footer="null"
           :destroyOnClose="true"
           @close="close">
    <!-- 编辑器 -->
    <Editor v-bind="editorConfig" :value="value" :height="350" :readOnly="true"/>
    <!-- 标题 -->
    <template #title>
      <span>{{ title }}</span>
      <a @click="$copy(value)" title="复制">
        <a-icon class="copy-icon-right" type="copy"/>
      </a>
    </template>
  </a-modal>
</template>

<script>
import Editor from '@/components/editor/Editor'

export default {
  name: 'EditorPreview',
  components: {
    Editor
  },
  props: {
    editorConfig: {
      type: Object,
      default: () => {
        return {}
      }
    },
    title: {
      type: String,
      default: '预览'
    }
  },
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
