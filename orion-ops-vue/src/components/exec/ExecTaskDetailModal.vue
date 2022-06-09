<template>
  <a-modal v-model="visible"
           title="详情"
           width="700px"
           :dialogStyle="{top: '16px'}"
           :maskClosable="false"
           :destroyOnClose="true">
    <a-spin :spinning="loading">
      <div id="exec-task-descriptions">
        <a-descriptions bordered size="middle">
          <a-descriptions-item label="执行主机" :span="2">
            {{ `${detail.machineName} (${detail.machineHost})` }}
          </a-descriptions-item>
          <a-descriptions-item label="执行用户" :span="1">
            {{ detail.username }}
          </a-descriptions-item>
          <a-descriptions-item label="执行命令" :span="3">
            <Editor :height="300" :readOnly="true" :value="detail.command"/>
          </a-descriptions-item>
          <a-descriptions-item v-if="detail.description" label="执行描述" :span="3">
            {{ detail.description }}
          </a-descriptions-item>
          <a-descriptions-item v-if="detail.createTime" label="创建时间" :span="2">
            {{ detail.createTime | formatDate }} ({{ detail.createTimeAgo }})
          </a-descriptions-item>
          <a-descriptions-item label="状态" :span="1">
            <a-tag v-if="detail.status" :color="detail.status | formatExecStatus('color')">
              {{ detail.status | formatExecStatus('label') }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item v-if="detail.startDate" label="开始时间" :span="detail.exitCode === null ? 3 : 2">
            {{ detail.startDate | formatDate }} ({{ detail.startDateAgo }})
          </a-descriptions-item>
          <a-descriptions-item v-if="detail.exitCode !== null" label="退出码" :span="1">
            <span :style="{'color': detail.exitCode === 0 ? '#4263EB' : '#E03131'}">
              {{ detail.exitCode }}
            </span>
          </a-descriptions-item>
          <a-descriptions-item v-if="detail.endDate" label="结束时间" :span="2">
            {{ detail.endDate | formatDate }} ({{ detail.endDateAgo }})
          </a-descriptions-item>
          <a-descriptions-item v-if="detail.used" label="用时" :span="1">
            {{ `${detail.keepTime} (${detail.used}ms)` }}
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </a-spin>
    <template #footer>
      <a-button class="mr8" type="primary" @click="() => $copy(detail.command)">复制命令</a-button>
      <a-button @click="close">关闭</a-button>
    </template>
  </a-modal>
</template>

<script>
import { formatDate } from '@/lib/filters'
import { enumValueOf, BATCH_EXEC_STATUS } from '@/lib/enum'
import Editor from '@/components/editor/Editor'

export default {
  name: 'ExecTaskDetailModal',
  components: {
    Editor
  },
  data() {
    return {
      loading: false,
      visible: false,
      detail: {}
    }
  },
  methods: {
    open(id) {
      this.loading = true
      this.visible = true
      this.$api.getExecDetail({ id })
        .then(({ data }) => {
          this.loading = false
          this.detail = data
        })
        .catch(() => {
          this.loading = false
        })
    },
    close() {
      this.loading = false
      this.visible = false
      this.detail = {}
    }
  },
  filters: {
    formatDate,
    formatExecStatus(status, f) {
      return enumValueOf(BATCH_EXEC_STATUS, status)[f]
    }
  }
}
</script>

<style scoped>
#exec-task-descriptions /deep/ table th {
  padding: 14px;
  width: 95px;
}

#exec-task-descriptions /deep/ table td {
  padding: 14px 8px;
}

</style>
