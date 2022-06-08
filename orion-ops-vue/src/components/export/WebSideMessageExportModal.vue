<template>
  <a-modal v-model="visible"
           title="站内信 导出"
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
          <!-- 导出分类 -->
          <div class="data-export-param mb16">
            <span class="normal-label export-label">导出分类</span>
            <a-select class="param-input"
                      placeholder="全部"
                      @change="(e) => classify = e">
              <a-select-option v-for="classify in $enum.MESSAGE_CLASSIFY" :key="classify.value" :value="classify.value">
                {{ classify.label }}
              </a-select-option>
            </a-select>
          </div>
          <!-- 导出状态 -->
          <div class="data-export-param mb16">
            <span class="normal-label export-label">阅读状态</span>
            <a-select class="param-input"
                      placeholder="全部"
                      @change="(e) => status = e">
              <a-select-option v-for="readStatus in $enum.READ_STATUS" :key="readStatus.value" :value="readStatus.value">
                {{ readStatus.label }}
              </a-select-option>
            </a-select>
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

export default {
  name: 'WebSideMessageExportModal',
  data: function() {
    return {
      visible: false,
      loading: false,
      protectPassword: undefined,
      classify: undefined,
      status: undefined
    }
  },
  methods: {
    open() {
      this.protectPassword = undefined
      this.classify = undefined
      this.status = undefined
      this.loading = false
      this.visible = true
    },
    exportData() {
      this.loading = true
      this.$api.exportWebSideMessage({
        protectPassword: this.protectPassword,
        classify: this.classify,
        status: this.status
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
