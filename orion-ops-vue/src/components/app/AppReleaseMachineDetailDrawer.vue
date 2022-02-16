<template>
  <a-drawer title="机器详情"
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
        <a-descriptions-item label="机器名称" :span="3">
          {{ detail.machineName }}
        </a-descriptions-item>
        <a-descriptions-item label="机器主机" :span="3">
          {{ detail.machineHost }}
        </a-descriptions-item>
        <a-descriptions-item label="发布状态" :span="3">
          <a-tag :color="$enum.valueOf($enum.ACTION_STATUS, detail.status).color">
            {{ $enum.valueOf($enum.ACTION_STATUS, detail.status).label }}
          </a-tag>
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
        <a-descriptions-item label="日志" :span="3" v-if="statusHolder.visibleActionLog(detail.status)">
          <a v-if="detail.downloadUrl" @click="clearDownloadUrl(detail)" target="_blank" :href="detail.downloadUrl">下载</a>
          <a v-else @click="loadDownloadUrl(detail, $enum.FILE_DOWNLOAD_TYPE.APP_RELEASE_MACHINE_LOG.value)">获取操作日志</a>
        </a-descriptions-item>
      </a-descriptions>
      <!-- 发布操作 -->
      <a-divider>发布操作</a-divider>
      <a-list :dataSource="detail.actions">
        <template v-slot:renderItem="item">
          <a-list-item>
            <a-descriptions size="middle">
              <a-descriptions-item label="操作名称" :span="3">
                {{ item.actionName }}
              </a-descriptions-item>
              <a-descriptions-item label="操作类型" :span="3">
                <a-tag>{{ $enum.valueOf($enum.RELEASE_ACTION_TYPE, item.actionType).label }}</a-tag>
              </a-descriptions-item>
              <a-descriptions-item label="操作状态" :span="3">
                <a-tag :color="$enum.valueOf($enum.ACTION_STATUS, item.status).color">
                  {{ $enum.valueOf($enum.ACTION_STATUS, item.status).label }}
                </a-tag>
              </a-descriptions-item>
              <a-descriptions-item label="开始时间" :span="3" v-if="item.startTime !== null">
                {{ item.startTime | formatDate }} ({{ item.startTimeAgo }})
              </a-descriptions-item>
              <a-descriptions-item label="结束时间" :span="3" v-if="item.endTime !== null">
                {{ item.endTime | formatDate }} ({{ item.endTimeAgo }})
              </a-descriptions-item>
              <a-descriptions-item label="持续时间" :span="3" v-if="item.used !== null">
                {{ `${item.keepTime}  (${item.used}ms)` }}
              </a-descriptions-item>
              <a-descriptions-item label="退出码" :span="3" v-if="item.exitCode !== null">
              <span :style="{'color': item.exitCode === 0 ? '#4263EB' : '#E03131'}">
                {{ item.exitCode }}
              </span>
              </a-descriptions-item>
              <a-descriptions-item label="命令" :span="3" v-if="item.actionType === $enum.RELEASE_ACTION_TYPE.COMMAND.value">
                <a @click="preview(item.actionCommand)">预览</a>
              </a-descriptions-item>
              <a-descriptions-item label="日志" :span="3" v-if="statusHolder.visibleActionLog(item.status)">
                <a v-if="item.downloadUrl" @click="clearDownloadUrl(item)" target="_blank" :href="item.downloadUrl">下载</a>
                <a v-else @click="loadDownloadUrl(item, $enum.FILE_DOWNLOAD_TYPE.APP_RELEASE_ACTION_LOG.value)">获取操作日志</a>
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
import EditorPreview from '@/components/preview/EditorPreview'
import _filters from '@/lib/filters'

function statusHolder() {
  return {
    visibleActionLog: (status) => {
      return status === this.$enum.ACTION_STATUS.RUNNABLE.value ||
        status === this.$enum.ACTION_STATUS.FINISH.value ||
        status === this.$enum.ACTION_STATUS.FAILURE.value ||
        status === this.$enum.ACTION_STATUS.TERMINATED.value
    }
  }
}

export default {
  name: 'AppReleaseMachineDetailDrawer',
  components: {
    EditorPreview
  },
  data() {
    return {
      visible: false,
      loading: true,
      pollId: null,
      detail: {},
      statusHolder: statusHolder.call(this)
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
      this.$api.getAppReleaseMachineDetail({
        releaseMachineId: id
      }).then(({ data }) => {
        this.loading = false
        data.downloadUrl = null
        this.$utils.defineArrayKey(data.actions, 'downloadUrl')
        this.detail = data
        // 轮询状态
        if (data.status === this.$enum.ACTION_STATUS.WAIT.value ||
          data.status === this.$enum.ACTION_STATUS.RUNNABLE.value) {
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
      if (this.detail.status !== this.$enum.ACTION_STATUS.WAIT.value &&
        this.detail.status !== this.$enum.ACTION_STATUS.RUNNABLE.value) {
        clearInterval(this.pollId)
        return
      }
      this.$api.getAppReleaseMachineStatus({
        releaseMachineId: this.detail.id
      }).then(({ data }) => {
        this.detail.status = data.status
        this.detail.used = data.used
        this.detail.keepTime = data.keepTime
        this.detail.startTime = data.startTime
        this.detail.startTimeAgo = data.startTimeAgo
        this.detail.endTime = data.endTime
        this.detail.endTimeAgo = data.endTimeAgo
        if (data.actions && data.actions.length) {
          for (const action of data.actions) {
            this.detail.actions.filter(s => s.id === action.id).forEach(s => {
              s.status = action.status
              s.keepTime = action.keepTime
              s.used = action.used
              s.startTime = action.startTime
              s.startTimeAgo = action.startTimeAgo
              s.endTime = action.endTime
              s.endTimeAgo = action.endTimeAgo
              s.exitCode = action.exitCode
            })
          }
        }
      })
    },
    async loadDownloadUrl(record, type) {
      try {
        const downloadUrl = await this.$api.getFileDownloadToken({
          type,
          id: record.id
        })
        record.downloadUrl = this.$api.fileDownloadExec({ token: downloadUrl.data })
      } catch (e) {
        // ignore
      }
    },
    clearDownloadUrl(record) {
      setTimeout(() => {
        record.downloadUrl = null
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
    ..._filters
  },
  beforeDestroy() {
    this.pollId !== null && clearInterval(this.pollId)
    this.pollId = null
  }
}
</script>

<style scoped>

</style>
