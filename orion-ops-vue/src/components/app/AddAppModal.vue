<template>
  <a-modal v-model="visible"
           v-drag-modal
           :title="title"
           :width="640"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="check"
           @cancel="close">
    <!-- 表单 -->
    <AddAppForm ref="from"
                @close="close"
                @loading="l => loading = l"
                @added="() => $emit('added')"
                @updated="() => $emit('updated')"/>
  </a-modal>
</template>

<script>
import AddAppForm from './AddAppForm'

export default {
  name: 'AddAppModal',
  components: {
    AddAppForm
  },
  data: function() {
    return {
      visible: false,
      loading: false,
      title: null
    }
  },
  methods: {
    add() {
      this.title = '添加应用'
      this.visible = true
      this.$nextTick(() => {
        this.$refs.from.add()
      })
    },
    update(id) {
      this.title = '修改应用'
      this.visible = true
      this.$nextTick(() => {
        this.$refs.from.update(id)
      })
    },
    check() {
      this.$refs.from.check()
    },
    close() {
      this.visible = false
    }
  }
}
</script>

<style scoped>
</style>
