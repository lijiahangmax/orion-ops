<template>
  <div class="app-pipeline-record-container">
    <!-- 搜索列 -->
    <div class="table-search-columns">
      <a-form-model class="app-pipeline-record-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="流水线" prop="pipeline">
              <PipelineAutoComplete ref="pipelineSelector" @change="selectedPipeline"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="标题" prop="title">
              <a-input v-model="query.title" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="状态" prop="status">
              <a-select v-model="query.status" placeholder="全部" allowClear>
                <a-select-option :value="status.status" v-for="status in $enum.PIPELINE_STATUS" :key="status.value">
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
        <span class="table-title">流水线列表</span>
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
        <template v-slot:expandedRowRender="record">
          <a-table
            v-if="record.details"
            :rowKey="(record, index) => index"
            :columns="innerColumns"
            :dataSource="record.details"
            :loading="record.loading"
            :pagination="false"
            size="middle">
            <!-- 操作 -->
            <template v-slot:stage="detail">
              <span class="span-blue">
                {{ $enum.valueOf($enum.STAGE_TYPE, detail.stageType).label }}
              </span>
            </template>
            <!-- 配置 -->
            <template v-slot:config="detail">
              <!-- 构建 -->
              <div v-if="detail.stageType === $enum.STAGE_TYPE.BUILD.value && detail.config.branchName">
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
              <div v-if="detail.stageType === $enum.STAGE_TYPE.RELEASE.value">
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
                    <span v-if="detail.config.machineIdList && detail.config.machineIdList.length" class="span-blue ml4">
                      {{ detail.config.machineList.map(s => s.name).join(', ') }}
                    </span>
                    <span v-else class="span-blue ml4">全部机器</span>
                </span>
              </div>
            </template>
            <!-- 状态 -->
            <template v-slot:status="detail">
              <a-tag class="m0" :color="$enum.valueOf($enum.PIPELINE_DETAIL_STATUS, detail.status).color">
                {{ $enum.valueOf($enum.PIPELINE_DETAIL_STATUS, detail.status).label }}
              </a-tag>
            </template>
            <!-- 操作 -->
            <template v-slot:action="detail">
              {{ detail.id }}
            </template>
          </a-table>
        </template>
        <!-- 执行标题 -->
        <template v-slot:pipelineTitle="record">
          <div class="timed-wrapper">
            <!-- 定时图标 -->
            <a-tooltip v-if="record.timedExec === $enum.TIMED_TYPE.TIMED.value">
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
        <template v-slot:status="record">
          <a-tag class="m0" :color="$enum.valueOf($enum.PIPELINE_STATUS, record.status).color">
            {{ $enum.valueOf($enum.PIPELINE_STATUS, record.status).label }}
          </a-tag>
        </template>
        <!-- 创建时间 -->
        <template v-slot:createTime="record">
          {{ record.createTime | formatDate }}
        </template>
        <!-- 操作 -->
        <template v-slot:action="record">
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
          <!-- 日志 -->
          <a-divider type="vertical" v-if="statusHolder.visibleLog(record.status)"/>
          <span class="span-blue pointer" v-if="statusHolder.visibleLog(record.status)" @click="openLog(record.id)">日志</span>
          <!-- 停止 -->
          <a-divider type="vertical" v-if="statusHolder.visibleTerminated(record.status)"/>
          <a-popconfirm v-if="statusHolder.visibleTerminated(record.status)"
                        title="是否要取消停止执行?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="terminatedPipeline(record.id)">
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
      <AppPipelineExecModal ref="execModal"/>
      <!-- 审核模态框 -->
      <AppPipelineExecAuditModal ref="auditModal" @audit="auditPipeline"/>
      <!-- 定时模态框 -->
      <AppPipelineExecTimedModal ref="execTimedModal" @updated="forceUpdateRows"/>
      <!-- 详情 -->
      <AppPipelineRecordDetailDrawer ref="detailDrawer"/>
    </div>
  </div>
</template>

<script>

import _filters from '@/lib/filters'
import AppPipelineExecModal from '@/components/app/AppPipelineExecModal'
import PipelineAutoComplete from '@/components/app/PipelineAutoComplete'
import AppPipelineExecAuditModal from '@/components/app/AppPipelineExecAuditModal'
import AppPipelineExecTimedModal from '@/components/app/AppPipelineExecTimedModal'
import AppPipelineRecordDetailDrawer from '@/components/app/AppPipelineRecordDetailDrawer'

function statusHolder() {
  return {
    visibleCopy: (status) => {
      return true
    },
    visibleCancel: (status) => {
      return status === this.$enum.PIPELINE_STATUS.WAIT_SCHEDULE.value
    },
    visibleAudit: (status) => {
      return (status === this.$enum.PIPELINE_STATUS.WAIT_AUDIT.value ||
        status === this.$enum.PIPELINE_STATUS.AUDIT_REJECT.value) && this.$isAdmin()
    },
    visibleExec: (status) => {
      return status === this.$enum.PIPELINE_STATUS.WAIT_RUNNABLE.value ||
        status === this.$enum.PIPELINE_STATUS.WAIT_SCHEDULE.value
    },
    visibleTimed: (status) => {
      return status === this.$enum.PIPELINE_STATUS.WAIT_RUNNABLE.value ||
        status === this.$enum.PIPELINE_STATUS.WAIT_SCHEDULE.value
    },
    visibleDetail: (status) => {
      return true
    },
    visibleTerminated: (status) => {
      return status === this.$enum.PIPELINE_STATUS.RUNNABLE.value
    },
    visibleDelete: (status) => {
      return status !== this.$enum.PIPELINE_STATUS.RUNNABLE.value &&
        status !== this.$enum.PIPELINE_STATUS.WAIT_SCHEDULE.value
    },
    visibleLog: (status) => {
      return status === this.$enum.PIPELINE_STATUS.RUNNABLE.value ||
        status === this.$enum.PIPELINE_STATUS.FINISH.value ||
        status === this.$enum.PIPELINE_STATUS.FAILURE.value ||
        status === this.$enum.PIPELINE_STATUS.TERMINATED.value
    },
    visibleDetailLog: (status) => {
      return status === this.$enum.PIPELINE_DETAIL_STATUS.RUNNABLE.value ||
        status === this.$enum.PIPELINE_DETAIL_STATUS.FINISH.value ||
        status === this.$enum.PIPELINE_DETAIL_STATUS.FAILURE.value ||
        status === this.$enum.PIPELINE_DETAIL_STATUS.TERMINATED.value
    },
    visibleDetailTerminated: (status) => {
      return status === this.$enum.PIPELINE_DETAIL_STATUS.RUNNABLE.value
    },
    visibleDetailSkip: (status, machineStatus) => {
      return status === this.$enum.PIPELINE_STATUS.RUNNABLE.value &&
        machineStatus === this.$enum.PIPELINE_DETAIL_STATUS.WAIT.value
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
    title: '标题',
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
    width: 230
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
    width: 200,
    align: 'center',
    sorter: (a, b) => a.status - b.status,
    scopedSlots: { customRender: 'status' }
  },
  {
    title: '持续时间',
    key: 'keepTime',
    dataIndex: 'keepTime',
    width: 200,
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
  name: 'AppPipelineRecord',
  components: {
    AppPipelineRecordDetailDrawer,
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
            disabled: record.status === this.$enum.PIPELINE_STATUS.RUNNABLE.value ||
              record.status === this.$enum.PIPELINE_STATUS.WAIT_SCHEDULE.value
          }
        })
      }
    }
  },
  data: function() {
    return {
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
      columns,
      innerColumns,
      statusHolder: statusHolder.call(this)
    }
  },
  methods: {
    chooseProfile({ id }) {
      this.query.profileId = id
      this.$refs.pipelineSelector.loadData(id)
      this.getList({})
    },
    getList(page = this.pagination) {
      this.loading = true
      this.expandedRowKeys = []
      this.$api.getAppPipelineRecordList({
        ...this.query,
        page: page.current,
        limit: page.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
        this.$utils.defineArrayKey(data.rows, 'loading', false)
        this.$utils.defineArrayKey(data.rows, 'details', [])
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
    },
    expandDetails(expand, record) {
      if (!expand || record.details.length) {
        return
      }
      // 加载机器
      record.loading = true
      this.$api.getAppPipelineRecordDetails({
        id: record.id
      }).then(({ data }) => {
        record.loading = false
        record.details = data
      }).catch(() => {
        record.loading = false
      })
    },
    openAudit(id) {
      this.$refs.auditModal.open(id)
    },
    auditPipeline(id, res) {
      const match = this.rows.filter(s => s.id === id)
      if (match && match.length) {
        match[0].status = (res ? this.$enum.PIPELINE_STATUS.WAIT_RUNNABLE.value : this.$enum.PIPELINE_STATUS.AUDIT_REJECT.value)
      }
    },
    copyPipeline(id) {
      this.$api.copyAppPipelineRecord({
        id
      }).then(() => {
        this.$message.success('已复制')
        this.getList({})
      })
    },
    execPipeline(record) {
      this.$api.execAppPipelineRecord({
        id: record.id
      }).then(() => {
        this.$message.success('已提交执行请求')
        record.status = this.$enum.PIPELINE_STATUS.RUNNABLE.value
      })
    },
    openTimed(id) {
      this.$refs.execTimedModal.open(id)
    },
    cancelTimed(record) {
      this.$api.cancelAppPipelineRecordTimedExec({
        id: record.id
      }).then(() => {
        this.$message.success('已取消定时执行')
        record.status = this.$enum.PIPELINE_STATUS.WAIT_RUNNABLE.value
        record.timedExec = this.$enum.TIMED_TYPE.NORMAL.value
        record.timedExecTime = undefined
        // 强制刷新状态
        this.forceUpdateRows()
      })
    },
    openLog(id) {
      this.$message.success('日志' + id)
    },
    openDetail(id) {
      this.$refs.detailDrawer.open(id)
    },
    terminatedPipeline(id) {
      this.$api.terminatedAppPipelineRecordExec({
        id
      }).then(() => {
        this.$message.success('已提交停止请求')
      })
    },
    remove(idList) {
      this.$api.deleteAppPipelineRecord({
        idList
      }).then(() => {
        this.$message.success('已删除')
        this.getList({})
      })
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
    this.$refs.pipelineSelector.loadData(this.query.profileId)
    this.getList({})
  },
  filters: {
    ..._filters
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

/deep/ .ant-table-expand-icon-th, /deep/ .ant-table-row-expand-icon-cell {
  width: 45px;
  min-width: 45px;
}

.stage-config-release-version {
  display: inline-block;
  width: 148px;
}
</style>
