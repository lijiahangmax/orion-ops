<template>
  <a-modal v-model="visible"
           title="操作日志 导出"
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
              <a-select-option v-for="classify in EVENT_CLASSIFY" :key="classify.value" :value="classify.value">
                {{ classify.label }}
              </a-select-option>
            </a-select>
          </div>
          <!-- 选择用户 -->
          <div class="data-export-param mb16" v-if="$isAdmin() && manager">
            <span class="normal-label export-label">选择用户</span>
            <UserSelector class="param-input"
                          placeholder="全部"
                          :disabled="onlyMyself"
                          @change="(e) => userId = e"/>
          </div>
          <!-- 只看自己 -->
          <div class="data-export-param mb16" v-if="$isAdmin() && manager">
            <span class="normal-label export-label">只看自己</span>
            <a-checkbox class="param-input" v-model="onlyMyself"/>
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
import { EVENT_CLASSIFY } from '@/lib/enum'
import UserSelector from '@/components/user/UserSelector'

export default {
  name: 'EventLogExportExportModal',
  components: { UserSelector },
  props: {
    manager: Boolean
  },
  data: function() {
    return {
      EVENT_CLASSIFY,
      visible: false,
      loading: false,
      protectPassword: undefined,
      classify: undefined,
      onlyMyself: undefined,
      userId: undefined
    }
  },
  methods: {
    open() {
      this.protectPassword = undefined
      this.classify = undefined
      this.onlyMyself = undefined
      this.userId = undefined
      this.loading = false
      this.visible = true
    },
    exportData() {
      this.loading = true
      this.$api.exportEventLog({
        protectPassword: this.protectPassword,
        classify: this.classify,
        userId: this.userId,
        onlyMyself: this.manager ? (this.onlyMyself ? 1 : 2) : 1
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
