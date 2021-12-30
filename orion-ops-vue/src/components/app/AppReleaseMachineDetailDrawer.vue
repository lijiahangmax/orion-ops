<template>
  <a-drawer title="机器详情"
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
        <a-descriptions-item label="日志" :span="3" v-if="statusHolder.visibleActionLog(detail.status)">
          <a>获取日志文件</a>
        </a-descriptions-item>
      </a-descriptions>
      <!-- 发布操作 -->
      <a-divider>发布操作</a-divider>
      <a-list :dataSource="detail.actions">
        <a-list-item slot="renderItem" slot-scope="item">
          <a-descriptions size="middle">
            <a-descriptions-item label="操作名称" :span="3">
              {{ item.name }}
            </a-descriptions-item>
            <a-descriptions-item label="操作类型" :span="3">
              <a-tag>{{ $enum.valueOf($enum.RELEASE_ACTION_TYPE, item.type).label }}</a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="操作状态" :span="3">
              <a-tag :color="$enum.valueOf($enum.ACTION_STATUS, item.status).color">
                {{ $enum.valueOf($enum.ACTION_STATUS, item.status).label }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="开始时间" :span="3" v-if="item.startTime !== null">
              {{
                item.startTime | formatDate({
                  date: item.startTime,
                  pattern: 'yyyy-MM-dd HH:mm:ss'
                })
              }} ({{ item.startTimeAgo }})
            </a-descriptions-item>
            <a-descriptions-item label="结束时间" :span="3" v-if="item.endTime !== null">
              {{
                item.endTime | formatDate({
                  date: item.endTime,
                  pattern: 'yyyy-MM-dd HH:mm:ss'
                })
              }} ({{ item.endTimeAgo }})
            </a-descriptions-item>
            <a-descriptions-item label="持续时间" :span="3" v-if="item.used !== null">
              {{ `${item.keepTime}  (${item.used}ms)` }}
            </a-descriptions-item>
            <a-descriptions-item label="退出码" :span="3" v-if="item.exitCode !== null">
              <span :style="{'color': item.exitCode === 0 ? '#4263EB' : '#E03131'}">
                {{ item.exitCode }}
              </span>
            </a-descriptions-item>
            <a-descriptions-item label="命令" :span="3" v-if="item.type === $enum.RELEASE_ACTION_TYPE.COMMAND.value">
              <a @click="preview(item.command)">预览</a>
            </a-descriptions-item>
            <a-descriptions-item label="日志" :span="3" v-if="statusHolder.visibleActionLog(item.status)">
              <a>获取日志文件</a>
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
      detail: {},
      statusHolder: statusHolder.call(this)
    }
  },
  methods: {
    open(id) {
      this.detail = {}
      this.visible = true
      this.loading = true
      this.$api.getAppReleaseMachineDetail({
        releaseMachineId: id
      }).then(({ data }) => {
        this.loading = false
        this.detail = data
      }).catch(() => {
        this.loading = false
      })
    },
    preview(command) {
      this.$refs.preview.preview(command)
    },
    onClose() {
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

</style>
