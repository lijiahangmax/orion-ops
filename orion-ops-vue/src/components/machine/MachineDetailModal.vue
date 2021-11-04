<template>
  <a-modal v-model="visible" title="详情" width="700px">
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
        <a-descriptions-item label="tag" :span="3">
          <a-tag color="#20C997">
            {{ detail.tag }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="认证方式" :span="2">
          <a-tag v-if="detail.authType" :color="$enum.valueOf($enum.MACHINE_AUTH_TYPE,detail.authType).color">
            {{ $enum.valueOf($enum.MACHINE_AUTH_TYPE, detail.authType).label }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="状态" :span="1">
          <a-badge
            v-if="detail.status"
            :status='$enum.valueOf($enum.MACHINE_STATUS, detail.status)["badge-status"]'
            :text="$enum.valueOf($enum.MACHINE_STATUS, detail.status).label"/>
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
          {{
            detail.createTime | formatDate({
              date: detail.createTime,
              pattern: 'yyyy-MM-dd HH:mm:ss'
            })
          }}
        </a-descriptions-item>
        <a-descriptions-item v-if="detail.updateTime" label="修改时间" :span="3">
          {{
            detail.updateTime | formatDate({
              date: detail.updateTime,
              pattern: 'yyyy-MM-dd HH:mm:ss'
            })
          }}
        </a-descriptions-item>
      </a-descriptions>
    </div>
    <div slot="footer">
      <a-button @click="close">关闭</a-button>
    </div>
  </a-modal>
</template>

<script>
import _utils from '@/lib/utils'

export default {
  name: 'MachineDetailModal',
  data: function() {
    return {
      visible: false,
      detail: {}
    }
  },
  methods: {
    open(id) {
      this.visible = true
      this.$api.getMachineDetail({ id })
        .then(({ data }) => {
          this.detail = data
        })
    },
    close() {
      this.visible = false
    }
  },
  filters: {
    formatDate(origin, {
      date,
      pattern
    }) {
      return _utils.dateFormat(new Date(date), pattern)
    }
  }
}
</script>

<style scoped>
#machine-descriptions /deep/ table th {
  padding: 14px;
  width: 95px;
  font-size: 16px;
}

#machine-descriptions /deep/ table td {
  font-size: 15px;
}

</style>