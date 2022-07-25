<template>
  <a-drawer title="构建详情"
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
      <!-- 构建信息 -->
      <a-descriptions size="middle">
        <a-descriptions-item label="应用名称" :span="3">
          {{ detail.appName }}
        </a-descriptions-item>
        <a-descriptions-item label="环境名称" :span="3">
          {{ detail.profileName }}
        </a-descriptions-item>
        <a-descriptions-item label="构建序列" :span="3">
          <span class="span-blue">#{{ detail.seq }}</span>
        </a-descriptions-item>
        <a-descriptions-item label="构建仓库" :span="3" v-if="detail.repoName != null">
          {{ detail.repoName }}
        </a-descriptions-item>
        <a-descriptions-item label="构建分支" :span="3" v-if="detail.branchName != null">
          <a-icon type="branches"/>
          {{ detail.branchName }}
        </a-descriptions-item>
        <a-descriptions-item label="commitId" :span="3" v-if="detail.commitId != null">
          {{ detail.commitId }}
        </a-descriptions-item>
        <a-descriptions-item label="构建描述" :span="3" v-if="detail.description != null">
          {{ detail.description }}
        </a-descriptions-item>
        <a-descriptions-item label="构建状态" :span="3">
          <a-tag :color="detail.status | formatBuildStatus('color')">
            {{ detail.status | formatBuildStatus('label') }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="构建用户" :span="3">
          {{ detail.createUserName }}
        </a-descriptions-item>
        <a-descriptions-item label="修改时间" :span="3">
          {{ detail.updateTime | formatDate }}
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
        <a-descriptions-item label="日志" :span="3" v-if="detail.status !== BUILD_STATUS.WAIT.value">
          <a v-if="detail.logUrl" @click="clearDownloadUrl(detail,'logUrl')" target="_blank" :href="detail.logUrl">下载</a>
          <a v-else @click="loadDownloadUrl(detail, FILE_DOWNLOAD_TYPE.APP_BUILD_LOG.value,'logUrl')">获取日志文件</a>
        </a-descriptions-item>
        <a-descriptions-item label="产物" :span="3" v-if="detail.status === BUILD_STATUS.FINISH.value">
          <a v-if="detail.downloadUrl" @click="clearDownloadUrl(detail)" target="_blank" :href="detail.downloadUrl">下载</a>
          <a v-else @click="loadDownloadUrl(detail, FILE_DOWNLOAD_TYPE.APP_BUILD_BUNDLE.value)">获取产物文件</a>
        </a-descriptions-item>
      </a-descriptions>
      <!-- 构建操作 -->
      <a-divider>构建操作</a-divider>
      <a-list :dataSource="detail.actions">
        <template v-slot:renderItem="item">
          <a-list-item>
            <a-descriptions size="middle">
              <a-descriptions-item label="操作名称" :span="3">
                {{ item.actionName }}
              </a-descriptions-item>
              <a-descriptions-item label="操作类型" :span="3">
                <a-tag>{{ item.actionType | formatActionType('label') }}</a-tag>
              </a-descriptions-item>
              <a-descriptions-item label="操作状态" :span="3">
                <a-tag :color="item.status | formatActionStatus('color')">
                  {{ item.status | formatActionStatus('label') }}
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
              <a-descriptions-item label="命令" :span="3" v-if="item.actionType === BUILD_ACTION_TYPE.COMMAND.value">
                <a @click="preview(item.actionCommand)">预览</a>
              </a-descriptions-item>
              <a-descriptions-item label="日志" :span="3" v-if="statusHolder.visibleActionLog(item.status)">
                <a v-if="item.downloadUrl" @click="clearDownloadUrl(item)" target="_blank" :href="item.downloadUrl">下载</a>
                <a v-else @click="loadDownloadUrl(item, FILE_DOWNLOAD_TYPE.APP_ACTION_LOG.value)">获取日志文件</a>
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
import { defineArrayKey } from '@/lib/utils'
import { formatDate } from '@/lib/filters'
import { ACTION_STATUS, BUILD_ACTION_TYPE, BUILD_STATUS, enumValueOf, FILE_DOWNLOAD_TYPE } from '@/lib/enum'
import EditorPreview from '@/components/preview/EditorPreview'

const statusHolder = {
  visibleActionLog: (status) => {
    return status === ACTION_STATUS.RUNNABLE.value ||
      status === ACTION_STATUS.FINISH.value ||
      status === ACTION_STATUS.FAILURE.value ||
      status === ACTION_STATUS.TERMINATED.value
  }
}

export default {
  name: 'AppBuildDetailDrawer',
  components: {
    EditorPreview
  },
  data() {
    return {
      FILE_DOWNLOAD_TYPE,
      ACTION_STATUS,
      BUILD_STATUS,
      BUILD_ACTION_TYPE,
      visible: false,
      loading: true,
      pollId: null,
      detail: {},
      statusHolder
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
      this.$api.getAppBuildDetail({
        id
      }).then(({ data }) => {
        this.loading = false
        data.logUrl = null
        data.downloadUrl = null
        defineArrayKey(data.actions, 'downloadUrl')
        this.detail = data
        // 轮询状态
        if (data.status === BUILD_STATUS.WAIT.value || data.status === BUILD_STATUS.RUNNABLE.value) {
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
      if (this.detail.status !== BUILD_STATUS.WAIT.value && this.detail.status !== BUILD_STATUS.RUNNABLE.value) {
        clearInterval(this.pollId)
        this.pollId = null
        return
      }
      this.$api.getAppBuildStatus({
        id: this.detail.id
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
    async loadDownloadUrl(record, type, field = 'downloadUrl') {
      try {
        const downloadUrl = await this.$api.getFileDownloadToken({
          type,
          id: record.id
        })
        record[field] = this.$api.fileDownloadExec({ token: downloadUrl.data })
      } catch (e) {
        // ignore
      }
    },
    clearDownloadUrl(record, field = 'downloadUrl') {
      setTimeout(() => {
        record[field] = null
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
    formatActionStatus(status, f) {
      return enumValueOf(ACTION_STATUS, status)[f]
    },
    formatBuildStatus(status, f) {
      return enumValueOf(BUILD_STATUS, status)[f]
    },
    formatActionType(type, f) {
      return enumValueOf(BUILD_ACTION_TYPE, type)[f]
    }
  }
}
</script>

<style scoped>

</style>
