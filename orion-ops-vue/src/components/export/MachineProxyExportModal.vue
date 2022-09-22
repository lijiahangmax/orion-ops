<template>
  <a-modal v-model="visible"
           title="机器代理 导出"
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
          <!-- 密码导出 -->
          <div class="data-export-param mb16">
            <span class="normal-label export-label">导出密码</span>
            <a-checkbox class="param-input" v-model="exportPassword">是否导出密码 (密文, 仅用于导入)</a-checkbox>
          </div>
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
import { downloadFile } from '@/lib/utils'
import { EXPORT_TYPE } from '@/lib/enum'

export default {
  name: 'MachineProxyExportModal',
  data: function() {
    return {
      visible: false,
      loading: false,
      exportPassword: false,
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
      this.$api.exportData({
        exportType: EXPORT_TYPE.MACHINE_PROXY.value,
        exportPassword: this.exportPassword ? 1 : 2,
        protectPassword: this.protectPassword
      }).then((e) => {
        this.loading = false
        this.visible = false
        this.$message.success('导出成功, 片刻后自动下载')
        downloadFile(e)
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
