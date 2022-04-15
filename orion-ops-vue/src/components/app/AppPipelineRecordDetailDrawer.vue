<template>
  <a-drawer title="执行详情"
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
      <!-- 流水线信息 -->
      <a-descriptions size="middle">
        <a-descriptions-item label="流水线名称" :span="3">
          {{ detail.pipelineName }}
        </a-descriptions-item>
        <a-descriptions-item label="执行标题" :span="3">
          {{ detail.title }}
        </a-descriptions-item>
        <a-descriptions-item label="环境名称" :span="3">
          {{ detail.profileName }}
        </a-descriptions-item>
        <a-descriptions-item label="执行描述" :span="3" v-if="detail.description != null">
          {{ detail.description }}
        </a-descriptions-item>
        <a-descriptions-item label="执行状态" :span="3">
          <a-tag :color="$enum.valueOf($enum.PIPELINE_STATUS, detail.status).color">
            {{ $enum.valueOf($enum.PIPELINE_STATUS, detail.status).label }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="调度时间" :span="3" v-if="detail.timedExecTime !== null">
          {{ detail.timedExecTime | formatDate }}
        </a-descriptions-item>
        <a-descriptions-item label="创建用户" :span="3">
          {{ detail.createUserName }}
        </a-descriptions-item>
        <a-descriptions-item label="创建时间" :span="3" v-if="detail.createTime !== null">
          {{ detail.createTime | formatDate }} ({{ detail.createTimeAgo }})
        </a-descriptions-item>
        <a-descriptions-item label="审核用户" :span="3" v-if="detail.auditUserName !== null">
          {{ detail.auditUserName }}
        </a-descriptions-item>
        <a-descriptions-item label="审核时间" :span="3" v-if="detail.auditTime !== null">
          {{ detail.auditTime | formatDate }} ({{ detail.auditTimeAgo }})
        </a-descriptions-item>
        <a-descriptions-item label="审核批注" :span="3" v-if="detail.auditReason !== null">
          {{ detail.auditReason }}
        </a-descriptions-item>
        <a-descriptions-item label="执行用户" :span="3" v-if=" detail.execUserName !== null">
          {{ detail.execUserName }}
        </a-descriptions-item>
        <a-descriptions-item label="开始时间" :span="3" v-if="detail.execStartTime !== null">
          {{ detail.execStartTime | formatDate }} ({{ detail.execStartTimeAgo }})
        </a-descriptions-item>
        <a-descriptions-item label="结束时间" :span="3" v-if="detail.execEndTime !== null">
          {{ detail.execEndTime | formatDate }} ({{ detail.execEndTimeAgo }})
        </a-descriptions-item>
        <a-descriptions-item label="持续时间" :span="3" v-if="detail.used !== null">
          {{ `${detail.keepTime} (${detail.used}ms)` }}
        </a-descriptions-item>
      </a-descriptions>
      <!-- 流水线操作 -->
      <a-divider>流水线操作</a-divider>
      <a-list size="small" :dataSource="detail.details">
        <template v-slot:renderItem="item">
          <a-list-item>
            <a-descriptions size="middle">
              <a-descriptions-item label="执行操作" :span="3">
               <span class="span-blue">
                 {{ $enum.valueOf($enum.STAGE_TYPE, item.stageType).label }}
               </span>
                {{ item.appName }}
              </a-descriptions-item>
              <a-descriptions-item label="执行状态" :span="3">
                <a-tag :color="$enum.valueOf($enum.PIPELINE_DETAIL_STATUS, item.status).color">
                  {{ $enum.valueOf($enum.PIPELINE_DETAIL_STATUS, item.status).label }}
                </a-tag>
              </a-descriptions-item>
              <a-descriptions-item label="构建分支" :span="3" v-if="item.stageType === $enum.STAGE_TYPE.BUILD.value && item.config.branchName">
                <a-icon type="branches"/>
                {{ item.config.branchName }}
                <a-tooltip v-if="item.config.commitId">
                  <template #title>
                    {{ item.config.commitId }}
                  </template>
                  <span class="span-blue">
                    #{{ item.config.commitId.substring(0, 7) }}
                  </span>
                </a-tooltip>
              </a-descriptions-item>
              <a-descriptions-item label="发布版本" :span="3" v-if="item.stageType === $enum.STAGE_TYPE.RELEASE.value">
                <span class="span-blue">
                  {{ item.config.buildSeq ? `#${item.config.buildSeq}` : '最新版本' }}
                </span>
              </a-descriptions-item>
              <a-descriptions-item label="发布机器" :span="3" v-if="item.stageType === $enum.STAGE_TYPE.RELEASE.value">
                <span v-if="item.config.machineIdList && item.config.machineIdList.length" class="span-blue">
                  {{ item.config.machineList.map(s => s.name).join(', ') }}
                </span>
                <span v-else class="span-blue">全部机器</span>
              </a-descriptions-item>
              <a-descriptions-item label="开始时间" :span="3" v-if="item.startTime !== null">
                {{ item.startTime | formatDate }} ({{ item.startTimeAgo }})
              </a-descriptions-item>
              <a-descriptions-item label="结束时间" :span="3" v-if="item.endTime !== null">
                {{ item.endTime | formatDate }} ({{ item.endTimeAgo }})
              </a-descriptions-item>
              <a-descriptions-item label="持续时间" :span="3" v-if="item.used !== null">
                {{ `${detail.keepTime} (${detail.used}ms)` }}
              </a-descriptions-item>
            </a-descriptions>
          </a-list-item>
        </template>
      </a-list>
    </div>
  </a-drawer>
</template>

<script>
import _filters from '@/lib/filters'

export default {
  name: 'AppPipelineRecordDetailDrawer',
  data() {
    return {
      visible: false,
      loading: true,
      detail: {}
    }
  },
  methods: {
    open(id) {
      this.detail = {}
      this.visible = true
      this.loading = true
      this.$api.getAppPipelineRecordDetail({
        id
      }).then(({ data }) => {
        this.loading = false
        this.detail = data
      }).catch(() => {
        this.loading = false
      })
    },
    onClose() {
      this.visible = false
    }
  },
  filters: {
    ..._filters
  }
}
</script>

<style lang="less" scoped>
</style>
