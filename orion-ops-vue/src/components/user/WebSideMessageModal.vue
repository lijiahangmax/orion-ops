<template>
  <a-modal v-model="visible"
           :width="500"
           :maskClosable="false"
           :title="$enum.valueOf($enum.valueOf($enum.MESSAGE_CLASSIFY, data.classify).type, data.type).label"
           :destroyOnClose="true"
           okText="跳转"
           cancelText="关闭"
           @ok="redirect"
           @cancel="close">
    <div v-if="data.message" v-html="stainMessage"/>
  </a-modal>
</template>

<script>
import { replaceStainKeywords } from '@/lib/utils'

export default {
  name: 'WebSideMessageModal',
  data() {
    return {
      visible: false,
      loading: false,
      data: {}
    }
  },
  computed: {
    stainMessage() {
      if (this.data.message) {
        return replaceStainKeywords(this.data.message)
      } else {
        return this.data.message
      }
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
