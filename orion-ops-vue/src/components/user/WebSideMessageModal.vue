<template>
  <a-modal v-model="visible"
           :width="500"
           :maskClosable="false"
           :title="$enum.valueOf($enum.valueOf($enum.MESSAGE_CLASSIFY, data.classify).type, data.type).label"
           okText="跳转"
           cancelText="关闭"
           @ok="redirect"
           @cancel="close">
    <div v-if="data.message" v-html="$utils.replaceStainKeywords(data.message)"/>
  </a-modal>
</template>

<script>
export default {
  name: 'WebSideMessageModal',
  data() {
    return {
      visible: false,
      loading: false,
      data: {}
    }
  },
  methods: {
    open(data) {
      this.data = data
      this.visible = true
    },
    close() {
      this.visible = false
    },
    redirect() {
      const type = this.$enum.valueOf(this.$enum.MESSAGE_CLASSIFY, this.data.classify).type
      const redirect = this.$enum.valueOf(type, this.data.type).redirect
      this.$router.push(redirect.substring(1))
    }
  }
}
</script>

<style scoped>

</style>
