<template>
  <a-modal v-model="visible"
           title="执行审核"
           :maskClosable="false"
           :destroyOnClose="true"
           :width="550">
    <a-spin :spinning="loading">
      <div class="audit-description">
        <span class="description-label normal-label mr8">审核描述</span>
        <a-textarea class="description-area" v-model="description" :maxLength="64"/>
      </div>
    </a-spin>
    <!-- 操作 -->
    <template #footer>
      <a-button @click="audit(false)" :disabled="loading">驳回</a-button>
      <a-button type="primary" @click="audit(true)" :disabled="loading">通过</a-button>
    </template>
  </a-modal>
</template>

<script>
import { AUDIT_STATUS } from '@/lib/enum'

export default {
  name: 'AppPipelineExecAuditModal',
  data() {
    return {
      id: null,
      visible: false,
      loading: false,
      description: null
    }
  },
  methods: {
    open(id) {
      this.visible = true
      this.loading = false
      this.id = id
    },
    audit(res) {
      if (!res && !this.description) {
        this.$message.warning('请输入驳回描述')
        return
      }
      this.loading = true
      this.$api.auditAppPipelineTask({
        id: this.id,
        auditStatus: res ? AUDIT_STATUS.RESOLVE.value : AUDIT_STATUS.REJECT.value,
        auditReason: this.description
      }).then(() => {
        this.$message.success('审核完成')
        this.$emit('audit', this.id, res)
        this.close()
      }).catch(() => {
        this.loading = false
      })
    },
    close() {
      this.visible = false
      this.loading = false
      this.description = null
      this.id = null
    }
  }
}
</script>

<style lang="less" scoped>

.audit-description {
  display: flex;
  align-items: baseline;

  .description-label {
    width: 80px;
  }

  .description-area {
    height: 60px;
  }
}

</style>
