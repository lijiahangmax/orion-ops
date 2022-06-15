<template>
  <a-drawer title="发布详情"
            placement="right"
            :visible="visible"
            :maskStyle="{opacity: 0, animation: 'none'}"
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
          <span class="span-blue">#{{ detail.buildSeq }}</span>
        </a-descriptions-item>
        <a-descriptions-item label="发布描述" :span="3" v-if="detail.description != null">
          {{ detail.description }}
        </a-descriptions-item>
        <a-descriptions-item label="发布状态" :span="3">
          <a-tag :color="detail.status | formatReleaseStatus('color')">
            {{ detail.status | formatReleaseStatus('label') }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="发布类型" :span="3">
          {{ detail.type | formatReleaseType('label') }}
          -
          {{ detail.serializer | formatSerialType('label') }}
          <span v-if="detail.serializer === SERIAL_TYPE.SERIAL.value">
            ({{ detail.exceptionHandler | formatExceptionHandler('label') }})
          </span>
        </a-descriptions-item>
        <a-descriptions-item label="调度时间" :span="3" v-if="detail.timedReleaseTime !== null">
          {{ detail.timedReleaseTime | formatDate }}
        </a-descriptions-item>
        <a-descriptions-item label="创建用户" :span="3">
          {{ detail.createUserName }}
        </a-descriptions-item>
        <a-descriptions-item label="创建时间" :span="3" v-if="detail.createTime !== null">
          {{ detail.createTime | formatDate }}
        </a-descriptions-item>
        <a-descriptions-item label="审核用户" :span="3" v-if="detail.auditUserName !== null">
          {{ detail.auditUserName }}
        </a-descriptions-item>
        <a-descriptions-item label="审核时间" :span="3" v-if="detail.auditTime !== null">
          {{ detail.auditTime | formatDate }}
        </a-descriptions-item>
        <a-descriptions-item label="审核批注" :span="3" v-if="detail.auditReason !== null">
          {{ detail.auditReason }}
        </a-descriptions-item>
        <a-descriptions-item label="发布用户" :span="3" v-if=" detail.releaseUserName !== null">
          {{ detail.releaseUserName }}
        </a-descriptions-item>
        <a-descriptions-item label="开始时间" :span="3" v-if="detail.startTime !== null">
          {{ detail.startTime | formatDate }} ({{ detail.startTimeAgo }})
        </a-descriptions-item>
        <a-descriptions-item label="结束时间" :span="3" v-if="detail.endTime !== null">
          {{ detail.endTime | formatDate }} ({{ detail.endTimeAgo }})
        </a-descriptions-item>
        <a-descriptions-item label="持续时间" :span="3" v-if="detail.used !== null">
          {{ `${detail.keepTime} (${detail.used}ms)` }}
        </a-descriptions-item>
      </a-descriptions>
      <!-- 发布机器 -->
      <a-divider>发布机器</a-divider>
      <a-list class="machine-list-container" size="small" bordered :dataSource="detail.machines">
        <template #header>
          <span class="span-blue">共 {{ detail.machines.length }} 台机器</span>
        </template>
        <template v-slot:renderItem="item">
          <a-list-item>
            <span>{{ item.machineName }}</span>
            <div>
              <a @click="$copy(item.machineHost)">{{ item.machineHost }}</a>
              <a-tag :color="item.status | formatActionStatus('color')" style="margin: 0 0 0 8px">
                {{ item.status | formatActionStatus('label') }}
              </a-tag>
            </div>
          </a-list-item>
        </template>
      </a-list>
      <!-- 发布操作 -->
      <a-divider>发布操作</a-divider>
      <a-list size="small" :dataSource="detail.actions">
        <template v-slot:renderItem="item">
          <a-list-item>
            <a-descriptions size="middle">
              <a-descriptions-item label="操作名称" :span="3">
                {{ item.name }}
              </a-descriptions-item>
              <a-descriptions-item label="操作类型" :span="3">
                <a-tag>{{ item.type | formatReleaseActionType('label') }}</a-tag>
              </a-descriptions-item>
              <a-descriptions-item label="命令" :span="3" v-if="item.type === RELEASE_ACTION_TYPE.COMMAND.value">
                <a @click="preview(item.command)">预览</a>
              </a-descriptions-item>
            </a-descriptions>
          </a-list-item>
        </template>
      </a-list>
    </div>
    <!-- 事件 -->
    <div class="detail-event">
      <EditorPreview ref="preview"/>
    </div>
  </a-drawer>
</template>

<script>
import { formatDate } from '@/lib/filters'
import { enumValueOf, ACTION_STATUS, EXCEPTION_HANDLER_TYPE, RELEASE_ACTION_TYPE, RELEASE_STATUS, RELEASE_TYPE, SERIAL_TYPE } from '@/lib/enum'
import EditorPreview from '@/components/preview/EditorPreview'

export default {
  name: 'AppReleaseDetailDrawer',
  components: {
    EditorPreview
  },
  data() {
    return {
      RELEASE_ACTION_TYPE,
      SERIAL_TYPE,
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
        this.pollId = null
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
        if (data.status === RELEASE_STATUS.WAIT_AUDIT.value ||
          data.status === RELEASE_STATUS.AUDIT_REJECT.value ||
          data.status === RELEASE_STATUS.WAIT_RUNNABLE.value ||
          data.status === RELEASE_STATUS.WAIT_SCHEDULE.value ||
          data.status === RELEASE_STATUS.RUNNABLE.value) {
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
      if (this.detail.status !== RELEASE_STATUS.WAIT_AUDIT.value &&
        this.detail.status !== RELEASE_STATUS.AUDIT_REJECT.value &&
        this.detail.status !== RELEASE_STATUS.WAIT_RUNNABLE.value &&
        this.detail.status !== RELEASE_STATUS.WAIT_SCHEDULE.value &&
        this.detail.status !== RELEASE_STATUS.RUNNABLE.value) {
        clearInterval(this.pollId)
        this.pollId = null
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
    formatDate,
    formatReleaseStatus(status, f) {
      return enumValueOf(RELEASE_STATUS, status)[f]
    },
    formatReleaseType(type, f) {
      return enumValueOf(RELEASE_TYPE, type)[f]
    },
    formatSerialType(type, f) {
      return enumValueOf(SERIAL_TYPE, type)[f]
    },
    formatExceptionHandler(type, f) {
      return enumValueOf(EXCEPTION_HANDLER_TYPE, type)[f]
    },
    formatReleaseActionType(status, f) {
      return enumValueOf(RELEASE_ACTION_TYPE, status)[f]
    },
    formatActionStatus(status, f) {
      return enumValueOf(ACTION_STATUS, status)[f]
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
