<template>
  <a-modal v-model="visible"
           title="发布审核"
           :width="550">
    <a-spin :spinning="loading">
      <div class="audit-description">
        <span class="description-label">审核描述 :</span>
        <a-textarea class="description-area" v-model="description" :maxLength="64"/>
      </div>
    </a-spin>
    <div slot="footer">
      <a-button @click="audit(false)" :disabled="loading">驳回</a-button>
      <a-button type="primary" @click="audit(true)" :disabled="loading">通过</a-button>
    </div>
  </a-modal>
</template>

<script>

export default {
  name: 'AppReleaseAuditModal',
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
      this.$api.auditAppRelease({
        id: this.id,
        status: res ? this.$enum.AUDIT_STATUS.RESOLVE.value : this.$enum.AUDIT_STATUS.REJECT.value,
        reason: this.description
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
