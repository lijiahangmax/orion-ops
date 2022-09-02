<template>
  <div class="app-pipeline-task-container">
    <!-- 搜索列 -->
    <div class="table-search-columns">
      <a-form-model class="app-pipeline-task-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="流水线" prop="pipeline">
              <PipelineAutoComplete ref="pipelineSelector" @change="selectedPipeline" @choose="getList({})"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="标题" prop="title">
              <a-input v-model="query.title" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="状态" prop="status">
              <a-select v-model="query.status" placeholder="全部" @change="getList({})" allowClear>
                <a-select-option :value="status.value" v-for="status in PIPELINE_STATUS" :key="status.value">
                  {{ status.label }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="描述" prop="description">
              <a-input v-model="query.description" allowClear/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 工具栏 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <span class="table-title">任务列表</span>
        <a-divider v-show="selectedRowKeys.length" type="vertical"/>
        <a-popconfirm v-show="selectedRowKeys.length"
                      placement="topRight"
                      title="是否删除选中记录?"
                      ok-text="确定"
                      cancel-text="取消"
                      @confirm="remove(selectedRowKeys)">
          <a-button class="ml8" type="danger" icon="delete">删除</a-button>
        </a-popconfirm>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a-button v-if="query.profileId" class="ml16 mr8" type="primary" icon="caret-right" @click="openPipelineList">执行</a-button>
        <a-divider type="vertical"/>
        <a-icon type="delete" class="tools-icon" title="清理" @click="openClear"/>
        <a-icon type="search" class="tools-icon" title="查询" @click="getList({})"/>
        <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
      </div>
    </div>
    <!-- 表格 -->
    <div class="table-main-container table-scroll-x-auto">
      <a-table :columns="columns"
               :dataSource="rows"
               :pagination="pagination"
               :rowSelection="rowSelection"
               rowKey="id"
               @change="getList"
               @expand="expandDetails"
               :loading="loading"
               :expandedRowKeys.sync="expandedRowKeys"
               size="middle">
        <!-- 展开的详情列表 -->
        <template #expandedRowRender="record">
          <a-table
            v-if="record.details"
            :rowKey="(record, index) => index"
            :columns="innerColumns"
            :dataSource="record.details"
            :loading="record.loading"
            :pagination="false"
            size="middle">
            <!-- 操作 -->
            <template #stage="detail">
              <span class="span-blue">
                {{ detail.stageType | formatStageType('label') }}
              </span>
            </template>
            <!-- 配置 -->
            <template #config="detail">
              <!-- 构建 -->
              <div v-if="detail.stageType === STAGE_TYPE.BUILD.value && detail.config.branchName">
                <a-icon type="branches"/>
                {{ detail.config.branchName }}
                <a-tooltip v-if="detail.config.commitId">
                  <template #title>
                    {{ detail.config.commitId }}
                  </template>
                  <span class="span-blue">
                    #{{ detail.config.commitId.substring(0, 7) }}
                  </span>
                </a-tooltip>
              </div>
              <!-- 发布 -->
              <div v-if="detail.stageType === STAGE_TYPE.RELEASE.value">
                <!-- 发布版本 -->
                <span class="stage-config-release-version">
                  发布版本:
                  <span class="span-blue ml4">
                    {{ detail.config.buildSeq ? `#${detail.config.buildSeq}` : '最新版本' }}
                  </span>
                </span>
                <!-- 发布机器 -->
                <span class="stage-config-release-machine">
                  发布机器:
                  <a-tooltip v-if="detail.config.machineIdList && detail.config.machineIdList.length">
                    <template #title>
                      {{ detail.config.machineList.map(s => s.name).join(', ') }}
                    </template>
                    <span class="span-blue ml4">
                      {{ detail.config.machineList.map(s => s.name).join(', ') }}
                    </span>
                  </a-tooltip>
                  <span v-else class="span-blue ml4">全部机器</span>
                </span>
              </div>
            </template>
            <!-- 状态 -->
            <template #status="detail">
              <a-tag class="m0" :color="detail.status | formatPipelineDetailStatus('color')">
                {{ detail.status | formatPipelineDetailStatus('label') }}
              </a-tag>
            </template>
            <!-- 操作 -->
            <template #action="detail">
              <!-- 日志 -->
              <a-tooltip :disabled="!statusHolder.visibleDetailLog(detail.status)" title="ctrl 点击打开新页面">
                <a target="_blank"
                   :href="`#/app/${detail.stageType === STAGE_TYPE.BUILD.value ? 'build' : 'release'}/log/view/${detail.relId}`"
                   @click="openDetailLog($event, detail.relId, detail.stageType)">日志</a>
              </a-tooltip>
              <!-- 停止 -->
              <a-divider type="vertical" v-if="statusHolder.visibleDetailTerminate(detail.status)"/>
              <a-popconfirm v-if="statusHolder.visibleDetailTerminate(detail.status)"
                            title="是否要停止执行?"
                            placement="bottomLeft"
                            ok-text="确定"
                            cancel-text="取消"
                            @confirm="terminatePipelineDetail(record.id, detail.id)">
                <span class="span-blue pointer">停止</span>
              </a-popconfirm>
              <!-- 跳过 -->
              <a-divider type="vertical" v-if="statusHolder.visibleDetailSkip(record.status, detail.status)"/>
              <a-popconfirm v-if="statusHolder.visibleDetailSkip(record.status, detail.status)"
                            title="是否要跳过执行?"
                            placement="bottomLeft"
                            ok-text="确定"
                            cancel-text="取消"
                            @confirm="skipPipelineDetail(record.id, detail.id)">
                <span class="span-blue pointer">跳过</span>
              </a-popconfirm>
            </template>
          </a-table>
        </template>
        <!-- 执行标题 -->
        <template #pipelineTitle="record">
          <div class="timed-wrapper">
            <!-- 定时图标 -->
            <a-tooltip v-if="record.timedExec === TIMED_TYPE.TIMED.value">
              <template #title>
                调度时间: {{ record.timedExecTime | formatDate }}
              </template>
              <a-icon class="timed-icon" type="hourglass"/>
            </a-tooltip>
            <!-- 标题 -->
            {{ record.title }}
          </div>
        </template>
        <!-- 状态 -->
        <template #status="record">
          <a-tag class="m0" :color="record.status | formatPipelineStatus('color')">
            {{ record.status | formatPipelineStatus('label') }}
          </a-tag>
        </template>
        <!-- 创建时间 -->
        <template #createTime="record">
          {{ record.createTime | formatDate }}
        </template>
        <!-- 操作 -->
        <template #action="record">
          <!-- 审核 -->
          <span class="span-blue pointer" v-if="statusHolder.visibleAudit(record.status)" @click="openAudit(record.id)">审核</span>
          <a-divider type="vertical" v-if="statusHolder.visibleAudit(record.status)"/>
          <!-- 复制 -->
          <a-popconfirm v-if="statusHolder.visibleCopy(record.status)"
                        title="是否要复制当前任务?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="copyPipeline(record.id)">
            <span class="span-blue pointer">复制</span>
          </a-popconfirm>
          <!-- 执行 -->
          <a-divider type="vertical" v-if="statusHolder.visibleExec(record.status)"/>
          <a-popconfirm v-if="statusHolder.visibleExec(record.status)"
                        title="是否要之前当前任务?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="execPipeline(record)">
            <span class="span-blue pointer">执行</span>
          </a-popconfirm>
          <!-- 定时 -->
          <a-divider type="vertical" v-if="statusHolder.visibleTimed(record.status)"/>
          <span class="span-blue pointer" v-if="statusHolder.visibleTimed(record.status)" @click="openTimed(record)">定时</span>
          <!--          &lt;!&ndash; 日志 &ndash;&gt;-->
          <!--          <a-divider type="vertical" v-if="statusHolder.visibleLog(record.status)"/>-->
          <!--          <span class="span-blue pointer" v-if="statusHolder.visibleLog(record.status)" @click="openLog(record.id)">日志</span>-->
          <!-- 停止 -->
          <a-divider type="vertical" v-if="statusHolder.visibleTerminate(record.status)"/>
          <a-popconfirm v-if="statusHolder.visibleTerminate(record.status)"
                        title="是否要取消停止执行?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="terminatePipeline(record.id)">
            <span class="span-blue pointer">停止</span>
          </a-popconfirm>
          <!-- 详情 -->
          <a-divider type="vertical" v-if="statusHolder.visibleDetail(record.status)"/>
          <span class="span-blue pointer" v-if="statusHolder.visibleDetail(record.status)" @click="openDetail(record.id)">详情</span>
          <!-- 取消 -->
          <a-divider type="vertical" v-if="statusHolder.visibleCancel(record.status)"/>
          <a-popconfirm v-if="statusHolder.visibleCancel(record.status)"
                        title="是否要取消定时执行?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="cancelTimed(record)">
            <span class="span-blue pointer">取消</span>
          </a-popconfirm>
          <!-- 删除 -->
          <a-divider v-if="statusHolder.visibleDelete(record.status)" type="vertical"/>
          <a-popconfirm v-if="statusHolder.visibleDelete(record.status)"
                        title="确认删除当前执行记录吗?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="remove([record.id])">
            <span class="span-blue pointer">删除</span>
          </a-popconfirm>
        </template>
      </a-table>
    </div>
    <!-- 事件 -->
    <div class="app-pipeline-event">
      <!-- 流水线执行模态框 -->
      <AppPipelineExecModal ref="execModal" :visibleReselect="true" @submit="getList"/>
      <!-- 审核模态框 -->
      <AppPipelineExecAuditModal ref="auditModal" @audit="auditPipeline"/>
      <!-- 定时模态框 -->
      <AppPipelineExecTimedModal ref="execTimedModal" @updated="forceUpdateRows"/>
      <!-- 详情 -->
      <AppPipelineTaskDetailDrawer ref="detailDrawer"/>
      <!-- 构建日志 -->
      <AppBuildLogAppenderModal ref="buildAppender"/>
      <!-- 发布日志 -->
      <AppReleaseLogAppenderModal ref="releaseAppender"/>
      <!-- 清理模态框 -->
      <AppPipelineClearModal ref="clear" @clear="getList({})"/>
    </div>
  </div>
</template>

<script>
import { defineArrayKey } from '@/lib/utils'
import { formatDate } from '@/lib/filters'
import { enumValueOf, PIPELINE_DETAIL_STATUS, PIPELINE_STATUS, STAGE_TYPE, TIMED_TYPE } from '@/lib/enum'
import AppPipelineExecModal from '@/components/app/AppPipelineExecModal'
import PipelineAutoComplete from '@/components/app/PipelineAutoComplete'
import AppPipelineExecAuditModal from '@/components/app/AppPipelineExecAuditModal'
import AppPipelineExecTimedModal from '@/components/app/AppPipelineExecTimedModal'
import AppPipelineTaskDetailDrawer from '@/components/app/AppPipelineTaskDetailDrawer'
import AppBuildLogAppenderModal from '@/components/log/AppBuildLogAppenderModal'
import AppReleaseLogAppenderModal from '@/components/log/AppReleaseLogAppenderModal'
import AppPipelineClearModal from '@/components/clear/AppPipelineClearModal'

function statusHolder() {
  return {
    visibleCopy: (status) => {
      return true
    },
    visibleCancel: (status) => {
      return status === PIPELINE_STATUS.WAIT_SCHEDULE.value
    },
    visibleAudit: (status) => {
      return (status === PIPELINE_STATUS.WAIT_AUDIT.value ||
        status === PIPELINE_STATUS.AUDIT_REJECT.value) && this.$isAdmin()
    },
    visibleExec: (status) => {
      return status === PIPELINE_STATUS.WAIT_RUNNABLE.value ||
        status === PIPELINE_STATUS.WAIT_SCHEDULE.value
    },
    visibleTimed: (status) => {
      return status === PIPELINE_STATUS.WAIT_RUNNABLE.value ||
        status === PIPELINE_STATUS.WAIT_SCHEDULE.value
    },
    visibleDetail: (status) => {
      return true
    },
    visibleTerminate: (status) => {
      return status === PIPELINE_STATUS.RUNNABLE.value
    },
    visibleDelete: (status) => {
      return status !== PIPELINE_STATUS.RUNNABLE.value &&
        status !== PIPELINE_STATUS.WAIT_SCHEDULE.value
    },
    visibleLog: (status) => {
      return status === PIPELINE_STATUS.RUNNABLE.value ||
        status === PIPELINE_STATUS.FINISH.value ||
        status === PIPELINE_STATUS.FAILURE.value ||
        status === PIPELINE_STATUS.TERMINATED.value
    },
    visibleDetailLog: (status) => {
      return status === PIPELINE_DETAIL_STATUS.RUNNABLE.value ||
        status === PIPELINE_DETAIL_STATUS.FINISH.value ||
        status === PIPELINE_DETAIL_STATUS.FAILURE.value ||
        status === PIPELINE_DETAIL_STATUS.TERMINATED.value
    },
    visibleDetailTerminate: (status) => {
      return status === PIPELINE_DETAIL_STATUS.RUNNABLE.value
    },
    visibleDetailSkip: (status, detailStatus) => {
      return status === PIPELINE_STATUS.RUNNABLE.value &&
        detailStatus === PIPELINE_DETAIL_STATUS.WAIT.value
    }
  }
}

/**
 * 列
 */
const columns = [
  {
    title: '流水线名称',
    dataIndex: 'pipelineName',
    key: 'pipelineName',
    ellipsis: true
  },
  {
    title: '任务标题',
    key: 'title',
    ellipsis: true,
    scopedSlots: { customRender: 'pipelineTitle' }
  },
  {
    title: '状态',
    key: 'status',
    width: 130,
    align: 'center',
    sorter: (a, b) => a.status - b.status,
    scopedSlots: { customRender: 'status' }
  },
  {
    title: '持续时间',
    key: 'keepTime',
    dataIndex: 'keepTime',
    width: 100,
    sorter: (a, b) => (a.used || 0) - (b.used || 0)
  },
  {
    title: '创建时间',
    key: 'createTime',
    align: 'center',
    ellipsis: true,
    width: 160,
    sorter: (a, b) => a.createTime - b.createTime,
    scopedSlots: { customRender: 'createTime' }
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true
  },
  {
    title: '操作',
    key: 'action',
    width: 230,
    scopedSlots: { customRender: 'action' }
  }
]

/**
 * 展开的详情列
 */
const innerColumns = [
  {
    title: '操作',
    key: 'stage',
    width: 60,
    align: 'center',
    scopedSlots: { customRender: 'stage' }
  },
  {
    title: '应用名称',
    key: 'appName',
    dataIndex: 'appName',
    width: 260
  },
  {
    title: '配置',
    key: 'config',
    ellipsis: true,
    scopedSlots: { customRender: 'config' }
  },
  {
    title: '状态',
    key: 'status',
    align: 'center',
    width: 160,
    sorter: (a, b) => a.status - b.status,
    scopedSlots: { customRender: 'status' }
  },
  {
    title: '持续时间',
    key: 'keepTime',
    dataIndex: 'keepTime',
    width: 140,
    sorter: (a, b) => (a.used || 0) - (b.used || 0)
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'AppPipelineTask',
  components: {
    AppPipelineClearModal,
    AppReleaseLogAppenderModal,
    AppBuildLogAppenderModal,
    AppPipelineTaskDetailDrawer,
    AppPipelineExecTimedModal,
    AppPipelineExecAuditModal,
    PipelineAutoComplete,
    AppPipelineExecModal
  },
  computed: {
    rowSelection() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        columnWidth: '40px',
        onChange: e => {
          this.selectedRowKeys = e
        },
        getCheckboxProps: record => ({
          props: {
            disabled: record.status === PIPELINE_STATUS.RUNNABLE.value ||
              record.status === PIPELINE_STATUS.WAIT_SCHEDULE.value
          }
        })
      }
    }
  },
  data: function() {
    return {
      PIPELINE_STATUS,
      STAGE_TYPE,
      TIMED_TYPE,
      query: {
        profileId: undefined,
        pipelineId: undefined,
        pipelineName: undefined,
        title: undefined,
        status: undefined,
        description: undefined
      },
      rows: [],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showTotal: function(total) {
          return `共 ${total} 条`
        }
      },
      selectedRowKeys: [],
      expandedRowKeys: [],
      loading: false,
      pollId: null,
      columns,
      innerColumns,
      statusHolder: statusHolder.call(this)
    }
  },
  watch: {
    'query.profileId'(e) {
      this.$refs.pipelineSelector.loadData(e)
    }
  },
  methods: {
    chooseProfile({ id }) {
      this.query.profileId = id
      this.resetForm()
    },
    getList(page = this.pagination) {
      this.loading = true
      this.expandedRowKeys = []
      this.$api.getAppPipelineTaskList({
        ...this.query,
        page: page.current,
        limit: page.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
        defineArrayKey(data.rows, 'loading', false)
        defineArrayKey(data.rows, 'details', [])
        this.rows = data.rows || []
        this.pagination = pagination
        this.selectedRowKeys = []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    selectedPipeline(id, name) {
      if (id) {
        this.query.pipelineId = id
        this.query.pipelineName = undefined
      } else {
        this.query.pipelineId = undefined
        this.query.pipelineName = name
      }
      if (id === undefined && name === undefined) {
        this.getList({})
      }
    },
    expandDetails(expand, record) {
      if (!expand || record.details.length) {
        return
      }
      // 加载机器
      record.loading = true
      this.$api.getAppPipelineTaskDetails({
        id: record.id
      }).then(({ data }) => {
        record.loading = false
        record.details = data
      }).catch(() => {
        record.loading = false
      })
    },
    openPipelineList() {
      this.$refs.execModal.openPipelineList(this.query.profileId)
    },
    openAudit(id) {
      this.$refs.auditModal.open(id)
    },
    auditPipeline(id, res) {
      const match = this.rows.filter(s => s.id === id)
      if (match && match.length) {
        match[0].status = (res ? PIPELINE_STATUS.WAIT_RUNNABLE.value : PIPELINE_STATUS.AUDIT_REJECT.value)
      }
    },
    copyPipeline(id) {
      this.$api.copyAppPipelineTask({
        id
      }).then(() => {
        this.$message.success('已复制')
        this.getList({})
      })
    },
    execPipeline(record) {
      this.$api.execAppPipelineTask({
        id: record.id
      }).then(() => {
        this.$message.success('已开始执行')
        record.status = PIPELINE_STATUS.RUNNABLE.value
      })
    },
    openTimed(id) {
      this.$refs.execTimedModal.open(id)
    },
    cancelTimed(record) {
      this.$api.cancelAppPipelineTaskTimedExec({
        id: record.id
      }).then(() => {
        this.$message.success('已取消定时执行')
        record.status = PIPELINE_STATUS.WAIT_RUNNABLE.value
        record.timedExec = TIMED_TYPE.NORMAL.value
        record.timedExecTime = undefined
        // 强制刷新状态
        this.forceUpdateRows()
      })
    },
    openLog(id) {
    },
    openDetail(id) {
      this.$refs.detailDrawer.open(id)
    },
    terminatePipeline(id) {
      this.$api.terminateAppPipelineTask({
        id
      }).then(() => {
        this.$message.success('已停止')
      })
    },
    remove(idList) {
      this.$api.deleteAppPipelineTask({
        idList
      }).then(() => {
        this.$message.success('已删除')
        this.getList({})
      })
    },
    openDetailLog(e, id, stageType) {
      if (!e.ctrlKey) {
        e.preventDefault()
        // 打开模态框
        if (stageType === STAGE_TYPE.BUILD.value) {
          this.$refs.buildAppender.open(id)
        } else {
          this.$refs.releaseAppender.open(id)
        }
        return false
      } else {
        // 跳转页面
        return true
      }
    },
    terminatePipelineDetail(id, detailId) {
      this.$api.terminateAppPipelineTaskDetail({
        id,
        detailId
      }).then(() => {
        this.$message.success('已停止')
      })
    },
    skipPipelineDetail(id, detailId) {
      this.$api.skipAppPipelineTaskDetail({
        id,
        detailId
      }).then(() => {
        this.$message.success('已跳过')
      })
    },
    openClear() {
      this.$refs.clear.open(this.query.profileId)
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.query.pipelineId = undefined
      this.query.pipelineName = undefined
      this.query.status = undefined
      this.$refs.pipelineSelector.reset()
      this.getList({})
    },
    forceUpdateRows() {
      this.$set(this.rows, 0, this.rows[0])
    },
    pollStatus() {
      if (!this.rows || !this.rows.length) {
        return
      }
      const pollItems = this.rows.filter(r => r.status === PIPELINE_STATUS.WAIT_AUDIT.value ||
        r.status === PIPELINE_STATUS.AUDIT_REJECT.value ||
        r.status === PIPELINE_STATUS.WAIT_RUNNABLE.value ||
        r.status === PIPELINE_STATUS.WAIT_SCHEDULE.value ||
        r.status === PIPELINE_STATUS.RUNNABLE.value)
      if (!pollItems.length) {
        return
      }
      const idList = pollItems.map(s => s.id)
      if (!idList.length) {
        return
      }
      const detailIdList = pollItems.map(s => s.details)
        .filter(s => s && s.length)
        .flat()
        .map(s => s.id)
      this.$api.getAppPipelineTaskListStatus({
        idList,
        detailIdList
      }).then(({ data }) => {
        if (!data || !data.length) {
          return
        }
        for (const status of data) {
          // 发布状态
          this.rows.filter(s => s.id === status.id).forEach(row => {
            row.status = status.status
            row.keepTime = status.keepTime
            row.used = status.used
            if (!status.details || !status.details.length || !row.details || !row.details.length) {
              return
            }
            // 机器状态
            for (const detail of status.details) {
              row.details.filter(m => m.id === detail.id).forEach(m => {
                m.relId = detail.relId
                m.status = detail.status
                m.keepTime = detail.keepTime
                m.used = detail.used
              })
            }
          })
        }
        // 强制刷新状态
        this.forceUpdateRows()
      })
    }
  },
  filters: {
    formatDate,
    formatStageType(type, f) {
      return enumValueOf(STAGE_TYPE, type)[f]
    },
    formatPipelineStatus(status, f) {
      return enumValueOf(PIPELINE_STATUS, status)[f]
    },
    formatPipelineDetailStatus(status, f) {
      return enumValueOf(PIPELINE_DETAIL_STATUS, status)[f]
    }
  },
  async mounted() {
    // 读取当前环境
    const activeProfile = this.$storage.get(this.$storage.keys.ACTIVE_PROFILE)
    if (!activeProfile) {
      this.$message.warning('请先维护应用环境')
      return
    }
    this.query.profileId = JSON.parse(activeProfile).id
    // 设置轮询
    this.pollId = setInterval(this.pollStatus, 5000)
    // 查询列表
    this.getList({})
  },
  beforeDestroy() {
    this.pollId !== null && clearInterval(this.pollId)
    this.pollId = null
  }
}
</script>

<style lang="less" scoped>
.timed-wrapper {
  display: contents;

  .timed-icon {
    background: #03A9F4;
    color: #FAFAFA;
    font-size: 16px;
    padding: 4px;
    border-radius: 3px;
    margin-right: 2px;
    display: inline-block;
  }
}

::v-deep .ant-table-expand-icon-th, .ant-table-row-expand-icon-cell {
  width: 45px;
  min-width: 45px;
}

.stage-config-release-version {
  display: inline-block;
  max-width: 120px;
  margin-right: 8px;
}

.stage-config-release-machine {
  display: inline-block;
}
</style>
