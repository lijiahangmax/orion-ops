<template>
  <a-modal v-model="visible"
           title="详情"
           width="700px"
           :maskClosable="false"
           :destroyOnClose="true">
    <a-spin :spinning="loading">
      <div id="machine-descriptions">
        <a-descriptions bordered size="middle">
          <a-descriptions-item label="机器名称" :span="3">
            {{ detail.name }}
          </a-descriptions-item>
          <a-descriptions-item label="主机" :span="2">
            {{ detail.host }}
          </a-descriptions-item>
          <a-descriptions-item label="端口" :span="1">
            {{ detail.sshPort }}
          </a-descriptions-item>
          <a-descriptions-item label="用户名" :span="3">
            {{ detail.username }}
          </a-descriptions-item>
          <a-descriptions-item label="唯一标识" :span="3">
            <span class="span-blue">
              {{ detail.tag }}
            </span>
          </a-descriptions-item>
          <a-descriptions-item label="认证方式" :span="2">
            <a-tag v-if="detail.authType">
              {{ $enum.valueOf($enum.MACHINE_AUTH_TYPE, detail.authType).label }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="状态" :span="1">
            <a-badge
              v-if="detail.status"
              :status='$enum.valueOf($enum.ENABLE_STATUS, detail.status).status'
              :text="$enum.valueOf($enum.ENABLE_STATUS, detail.status).label"/>
          </a-descriptions-item>
          <a-descriptions-item label="描述" :span="3">
            {{ detail.description }}
          </a-descriptions-item>
          <a-descriptions-item label="代理" v-if="detail.proxyType" :span="3">
            {{ `${detail.proxyHost}:${detail.proxyPort}` }}
            <a-tag :color="$enum.valueOf($enum.MACHINE_PROXY_TYPE, detail.proxyType).color">
              {{ $enum.valueOf($enum.MACHINE_PROXY_TYPE, detail.proxyType).label }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item v-if="detail.createTime" label="创建时间" :span="3">
            {{ detail.createTime | formatDate }}
          </a-descriptions-item>
          <a-descriptions-item v-if="detail.updateTime" label="修改时间" :span="3">
            {{ detail.updateTime | formatDate }}
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </a-spin>
    <template #footer>
      <a-button @click="close">关闭</a-button>
    </template>
  </a-modal>
</template>

<script>
import _filters from '@/lib/filters'

export default {
  name: 'MachineDetailModal',
  data: function() {
    return {
      loading: false,
      visible: false,
      detail: {}
    }
  },
  methods: {
    open(id) {
      this.loading = true
      this.$api.getMachineDetail({ id })
        .then(({ data }) => {
          this.loading = false
          this.visible = true
          this.detail = data
        })
        .catch(() => {
          this.loading = false
        })
    },
    close() {
      this.visible = false
      this.loading = false
      this.detail = {}
    }
  },
  filters: {
    ..._filters
  }
}
</script>

<style scoped>
#machine-descriptions /deep/ table th {
  padding: 14px;
  width: 95px;
}

#machine-descriptions /deep/ table td {
  padding: 14px 8px;
}

</style>
