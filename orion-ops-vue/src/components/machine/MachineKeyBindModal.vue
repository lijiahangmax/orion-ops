<template>
  <a-modal v-model="visible"
           :title="title"
           :width="368"
           :okButtonProps="{props: {disabled: loading}}"
           :bodyStyle="{padding: '16px'}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="bind"
           @cancel="close">
    <a-spin :spinning="loading">
      <span class="normal-label">机器密钥</span>
      <a-select class="key-selector"
                v-model="keyId"
                placeholder="选择密钥"
                allowClear>
        <a-select-option v-for="key of keys" :key="key.id" :value="key.id">
          {{ key.name }}
        </a-select-option>
      </a-select>
    </a-spin>
  </a-modal>
</template>

<script>

export default {
  name: 'MachineKeyBindModal',
  data: function() {
    return {
      visible: false,
      id: null,
      keyId: null,
      title: '',
      loading: false,
      keys: []
    }
  },
  watch: {
    visible(e) {
      if (!e) {
        return
      }
      if (this.keys.length) {
        return
      }
      this.loadMachineKeys()
    }
  },
  methods: {
    open(id, keyId, name) {
      this.visible = true
      this.id = id
      this.keyId = keyId
      this.title = `密钥绑定 ${name}`
    },
    loadMachineKeys() {
      this.loading = true
      this.$api.getMachineKeyList({
        limit: 10000
      }).then(({ data }) => {
        this.keys = data.rows
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    bind() {
      if (!this.keyId) {
        this.$message.error('请选择绑定的密钥')
        return
      }
      this.$api.bindMachineKey({
        id: this.keyId,
        machineIdList: [this.id]
      }).then(() => {
        this.$message.success('绑定成功')
        this.loading = false
        this.visible = false
        this.$emit('bindSuccess', this.id, this.keyId)
      }).catch(() => {
        this.loading = false
      })
    },
    close() {
      this.visible = false
      this.loading = false
    }
  }
}
</script>

<style scoped>
.key-selector {
  width: 248px;
  margin-left: 8px;
}
</style>
