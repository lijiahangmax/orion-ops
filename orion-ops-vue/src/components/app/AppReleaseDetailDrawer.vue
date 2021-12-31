<template>
  <a-drawer title="发布详情"
            placement="right"
            :visible="visible"
            :maskStyle="{opacity: 0, animation: 'none', '-webkit-animation': 'none'}"
            :width="430"
            @close="onClose">
    <!-- 加载中 -->
    <div v-if="loading">
      <a-skeleton active :paragraph="{rows: 12}"/>
    </div>
    <!-- 加载完成 -->
    <div v-else>
      <!-- 发布信息 -->
      <a-descriptions size="middle">
        <a-descriptions-item label="发布标题" :span="3">
          {{ detail.title }}
        </a-descriptions-item>
        <a-descriptions-item label="应用名称" :span="3">
          {{ detail.appName }}
        </a-descriptions-item>
        <a-descriptions-item label="环境名称" :span="3">
          {{ detail.profileName }}
        </a-descriptions-item>
        <a-descriptions-item label="构建序列" :span="3">
          <a-tag color="#5C7CFA">
            #{{ detail.buildSeq }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="发布描述" :span="3" v-if="detail.description != null">
          {{ detail.description }}
        </a-descriptions-item>
        <a-descriptions-item label="发布状态" :span="3">
          <a-tag :color="$enum.valueOf($enum.RELEASE_STATUS, detail.status).color">
            {{ $enum.valueOf($enum.RELEASE_STATUS, detail.status).label }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="发布类型" :span="3">
          {{ $enum.valueOf($enum.RELEASE_TYPE, detail.type).label }}
          /
          {{ $enum.valueOf($enum.RELEASE_SERIAL_TYPE, detail.serializer).label }}
        </a-descriptions-item>
        <a-descriptions-item label="创建用户" :span="3">
          {{ detail.createUserName }}
        </a-descriptions-item>
        <a-descriptions-item label="创建时间" :span="3" v-if="detail.createTime !== null">
          {{
            detail.createTime | formatDate({
              date: detail.createTime,
              pattern: 'yyyy-MM-dd HH:mm:ss'
            })
          }} ({{ detail.createTimeAgo }})
        </a-descriptions-item>
        <a-descriptions-item label="审核用户" :span="3" v-if=" detail.auditUserName !== null">
          {{ detail.auditUserName }}
        </a-descriptions-item>
        <a-descriptions-item label="审核时间" :span="3" v-if="detail.auditTime !== null">
          {{
            detail.auditTime | formatDate({
              date: detail.auditTime,
              pattern: 'yyyy-MM-dd HH:mm:ss'
            })
          }} ({{ detail.auditTimeAgo }})
        </a-descriptions-item>
        <a-descriptions-item label="审核批注" :span="3" v-if="detail.auditReason !== null">
          {{ detail.auditReason }}
        </a-descriptions-item>
        <a-descriptions-item label="发布用户" :span="3" v-if=" detail.releaseUserName !== null">
          {{ detail.releaseUserName }}
        </a-descriptions-item>
        <a-descriptions-item label="开始时间" :span="3" v-if="detail.startTime !== null">
          {{
            detail.startTime | formatDate({
              date: detail.startTime,
              pattern: 'yyyy-MM-dd HH:mm:ss'
            })
          }} ({{ detail.startTimeAgo }})
        </a-descriptions-item>
        <a-descriptions-item label="结束时间" :span="3" v-if="detail.endTime !== null">
          {{
            detail.endTime | formatDate({
              date: detail.endTime,
              pattern: 'yyyy-MM-dd HH:mm:ss'
            })
          }} ({{ detail.endTimeAgo }})
        </a-descriptions-item>
        <a-descriptions-item label="持续时间" :span="3" v-if="detail.used !== null">
          {{ `${detail.keepTime} (${detail.used}ms)` }}
        </a-descriptions-item>
      </a-descriptions>
      <!-- 发布机器 -->
      <a-divider>发布机器</a-divider>
      <a-list class="machine-list-container" size="small" bordered :dataSource="detail.machines">
        <span class="span-blue" slot="header">共 {{ detail.machines.length }} 台机器</span>
        <a-list-item slot="renderItem" slot-scope="item">
          <span>{{ item.machineName }}</span>
          <div>
            <a @click="$copy(item.machineHost)">{{ item.machineHost }}</a>
            <a-tag :color="$enum.valueOf($enum.ACTION_STATUS, item.status).color" style="margin: 0 0 0 8px">
              {{ $enum.valueOf($enum.ACTION_STATUS, item.status).label }}
            </a-tag>
          </div>
        </a-list-item>
      </a-list>
      <!-- 发布操作 -->
      <a-divider>发布操作</a-divider>
      <a-list size="small" :dataSource="detail.actions">
        <a-list-item slot="renderItem" slot-scope="item">
          <a-descriptions size="middle">
            <a-descriptions-item label="操作名称" :span="3">
              {{ item.name }}
            </a-descriptions-item>
            <a-descriptions-item label="操作类型" :span="3">
              <a-tag>{{ $enum.valueOf($enum.RELEASE_ACTION_TYPE, item.type).label }}</a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="命令" :span="3" v-if="item.type === $enum.RELEASE_ACTION_TYPE.COMMAND.value">
              <a @click="preview(item.command)">预览</a>
            </a-descriptions-item>
          </a-descriptions>
        </a-list-item>
      </a-list>
    </div>
    <!-- 事件 -->
    <div class="detail-event">
      <EditorPreview ref="preview"/>
    </div>
  </a-drawer>
</template>

<script>
import _utils from '@/lib/utils'
import EditorPreview from '@/components/preview/EditorPreview'

export default {
  name: 'AppReleaseDetailDrawer',
  components: {
    EditorPreview
  },
  data() {
    return {
      visible: false,
      loading: true,
      pollId: null,
      detail: {}
    }
  },
  methods: {
    open(id) {
      // 关闭轮询状态
      if (this.pollId) {
        clearInterval(this.pollId)
      }
      this.detail = {}
      this.visible = true
      this.loading = true
      this.$api.getAppReleaseDetail({
        id
      }).then(({ data }) => {
        this.loading = false
        this.detail = data
        // 轮询状态
        if (data.status === this.$enum.RELEASE_STATUS.WAIT_AUDIT.value ||
          data.status === this.$enum.RELEASE_STATUS.AUDIT_REJECT.value ||
          data.status === this.$enum.RELEASE_STATUS.WAIT_RUNNABLE.value ||
          data.status === this.$enum.RELEASE_STATUS.RUNNABLE.value) {
          this.pollId = setInterval(this.pollStatus, 5000)
        }
      }).catch(() => {
        this.loading = false
      })
    },
    pollStatus() {
      if (!this.detail || !this.detail.status) {
        return
      }
      if (this.detail.status !== this.$enum.RELEASE_STATUS.WAIT_AUDIT.value &&
        this.detail.status !== this.$enum.RELEASE_STATUS.AUDIT_REJECT.value &&
        this.detail.status !== this.$enum.RELEASE_STATUS.WAIT_RUNNABLE.value &&
        this.detail.status !== this.$enum.RELEASE_STATUS.RUNNABLE.value) {
        clearInterval(this.pollId)
        return
      }
      this.$api.getAppReleaseStatus({
        id: this.detail.id
      }).then(({ data }) => {
        this.detail.status = data.status
        this.detail.used = data.used
        this.detail.keepTime = data.keepTime
        this.detail.startTime = data.startTime
        this.detail.startTimeAgo = data.startTimeAgo
        this.detail.endTime = data.endTime
        this.detail.endTimeAgo = data.endTimeAgo
        if (data.machines && data.machines.length) {
          for (const machine of data.machines) {
            this.detail.machines.filter(s => s.id === machine.id).forEach(s => {
              s.status = machine.status
              s.keepTime = machine.keepTime
              s.used = machine.used
              s.startTime = machine.startTime
              s.startTimeAgo = machine.startTimeAgo
              s.endTime = machine.endTime
              s.endTimeAgo = machine.endTimeAgo
            })
          }
        }
      })
    },
    preview(command) {
      this.$refs.preview.preview(command)
    },
    onClose() {
      this.visible = false
      // 关闭轮询状态
      if (this.pollId) {
        clearInterval(this.pollId)
        this.pollId = null
      }
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

<style lang="less" scoped>
.machine-list-container {

  /deep/ .ant-list-header {
    padding: 8px;
  }

  /deep/ .ant-list-item {
    padding-right: 8px;
    padding-left: 8px;
  }
}
</style>
