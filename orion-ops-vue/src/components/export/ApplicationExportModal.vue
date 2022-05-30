<template>
  <a-modal v-model="visible"
           title="应用信息 导出"
           okText="导出"
           :width="400"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="exportData"
           @cancel="close">
    <a-spin :spinning="loading">
      <div class="data-export-container">
        <!-- 导出参数 -->
        <div class="data-export-params">
          <!-- 文档密码 -->
          <div class="data-export-param">
            <span class="normal-label export-label">文档密码</span>
            <a-input class="param-input"
                     v-model="protectPassword"
                     :maxLength="10"
                     placeholder="导出文档的密码(数字及字母)"/>
          </div>
        </div>
      </div>
    </a-spin>
  </a-modal>
</template>

<script>
export default {
  name: 'ApplicationExportModal',
  data: function() {
    return {
      visible: false,
      loading: false,
      protectPassword: undefined
    }
  },
  methods: {
    open() {
      this.exportPassword = false
      this.protectPassword = undefined
      this.loading = false
      this.visible = true
    },
    exportData() {
      this.loading = true
      this.$api.exportApplication({
        protectPassword: this.protectPassword
      }).then((e) => {
        this.loading = false
        this.visible = false
        this.$message.success('导出成功, 片刻后自动下载')
        this.$utils.downloadFile(e)
      }).catch(() => {
        this.loading = false
        this.$message.error('导出失败')
      })
    },
    close() {
      this.visible = false
      this.loading = false
    }
  }
}
</script>

<style lang="less" scoped>

.data-export-container {
  width: 100%;
}

.export-label {
  width: 64px;
  text-align: end;
}

.data-export-param {
  width: 100%;
  display: flex;
  align-items: center;
}

.param-input {
  margin-left: 8px;
  width: 236px;
}
</style>
