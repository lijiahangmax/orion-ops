<template>
  <div class="app-info-container">
    <!-- 搜索列 -->
    <div class="table-search-columns">
      <a-form-model class="app-info-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="应用" prop="appId">
              <AppSelector ref="appSelector" @change="appId => query.appId = appId"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="标题" prop="title">
              <a-input v-model="query.title" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="状态" prop="status">
              <a-select v-model="query.status" placeholder="请选择" allowClear>
                <a-select-option v-for="status of $enum.RELEASE_STATUS" :key="status.value" :value="status.value">
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
          <a-col :span="4">
            <a-form-model-item label="只看自己" prop="onlyMyself">
              <a-checkbox v-model="query.onlyMyself" @change="getList({})"/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 工具栏 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <span class="table-title">发布列表</span>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a-button class="ml16 mr8" type="primary" icon="deployment-unit" @click="openRelease">应用发布</a-button>
        <a-divider type="vertical"/>
        <a-icon type="search" class="tools-icon" title="查询" @click="getList({})"/>
        <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
      </div>
    </div>
    <!-- 表格 -->
    <div class="table-main-container">
      <a-table :columns="columns"
               :dataSource="rows"
               :pagination="pagination"
               rowKey="id"
               @change="getList"
               :loading="loading"
               size="middle">
        <!-- 展开的机器列表 -->
        <a-table
          slot="expandedRowRender"
          slot-scope="record"
          v-if="record.machines"
          :rowKey="(record, index) => index"
          :columns="innerColumns"
          :dataSource="record.machines"
          :pagination="false"
          size="middle">
          <!-- 状态 -->
          <a-tag class="m0" slot="status" slot-scope="machine" :color="$enum.valueOf($enum.ACTION_STATUS, machine.status).color">
            {{ $enum.valueOf($enum.ACTION_STATUS, machine.status).label }}
          </a-tag>
          <div slot="action" slot-scope="machine">
            <!-- 日志 -->
            <a @click="openMachineLog(machine.id)">日志</a>
            <a-divider type="vertical"/>
            <!-- 详情 -->
            <a @click="openMachineDetail(machine.id)">详情</a>
          </div>
        </a-table>
        <!-- 构建序列 -->
        <a-tag slot="seq" slot-scope="record" color="#5C7CFA">
          #{{ record.buildSeq }}
        </a-tag>
        <!-- 状态 -->
        <a-tag class="m0" slot="status" slot-scope="record" :color="$enum.valueOf($enum.RELEASE_STATUS, record.status).color">
          {{ $enum.valueOf($enum.RELEASE_STATUS, record.status).label }}
        </a-tag>
        <!-- 创建时间 -->
        <span slot="createTime" slot-scope="record">
          {{
            record.createTime | formatDate({
              date: record.createTime,
              pattern: 'yyyy-MM-dd HH:mm:ss'
            })
          }}
        </span>
        <!-- 操作 -->
        <div slot="action" slot-scope="record">
          <!-- 审核 -->
          <a v-if="statusHolder.visibleAudit(record.status)" @click="openAudit(record.id)">审核</a>
          <a-divider type="vertical" v-if="statusHolder.visibleAudit(record.status)"/>
          <!-- 发布 -->
          <a v-if="statusHolder.visibleRelease(record.status)" @click="runnableRelease(record.id)">发布</a>
          <a-divider type="vertical" v-if="statusHolder.visibleRelease(record.status)"/>
          <!-- 日志 -->
          <a v-if="statusHolder.visibleLog(record.status)" @click="openReleaseLog(record.id)">日志</a>
          <a-divider type="vertical" v-if="statusHolder.visibleLog(record.status)"/>
          <!-- 停止 -->
          <a v-if="statusHolder.visibleTerminated(record.status)" @click="terminated(record.id)">停止</a>
          <a-divider type="vertical" v-if="statusHolder.visibleTerminated(record.status)"/>
          <!-- 详情 -->
          <a @click="openReleaseDetail(record.id)">详情</a>
        </div>
      </a-table>
    </div>
    <!-- 事件 -->
    <div class="app-info-event">
      <!-- 添加模态框 -->
      <AppReleaseModal ref="addModal" @submit="getList({})"/>
      <!-- 审核模态框 -->
      <AppReleaseAuditModal ref="auditModal" @audit="releaseAudit"/>
      <!-- 发布详情抽屉 -->
      <AppReleaseDetailDrawer ref="releaseDetail"/>
      <!-- 机器详情抽屉 -->
      <AppReleaseMachineDetailDrawer ref="machineDetail"/>
    </div>
  </div>
</template>

<script>
import _utils from '@/lib/utils'
import AppSelector from '@/components/app/AppSelector'
import AppReleaseModal from '@/components/app/AppReleaseModal'
import AppReleaseAuditModal from '@/components/app/AppReleaseAuditModal'
import AppReleaseDetailDrawer from '@/components/app/AppReleaseDetailDrawer'
import AppReleaseMachineDetailDrawer from '@/components/app/AppReleaseMachineDetailDrawer'

function statusHolder() {
  return {
    visibleAudit: (status) => {
      return status === this.$enum.RELEASE_STATUS.WAIT_AUDIT.value ||
        status === this.$enum.RELEASE_STATUS.AUDIT_REJECT.value
    },
    visibleRelease: (status) => {
      return status === this.$enum.RELEASE_STATUS.WAIT_RUNNABLE.value
    },
    visibleLog: (status) => {
      return status === this.$enum.RELEASE_STATUS.RUNNABLE.value ||
        status === this.$enum.RELEASE_STATUS.FINISH.value ||
        status === this.$enum.RELEASE_STATUS.TERMINATED.value ||
        status === this.$enum.RELEASE_STATUS.INITIAL_ERROR.value ||
        status === this.$enum.RELEASE_STATUS.FAILURE.value
    },
    visibleTerminated: (status) => {
      return status === this.$enum.RELEASE_STATUS.RUNNABLE.value
    }
  }
}

/**
 * 列
 */
const columns = [
  {
    title: '构建序列',
    key: 'seq',
    width: 100,
    align: 'center',
    sorter: (a, b) => a.seq - b.seq,
    scopedSlots: { customRender: 'seq' }
  },
  {
    title: '标题',
    key: 'title',
    dataIndex: 'title',
    ellipsis: true
  },
  {
    title: '发布应用',
    key: 'appName',
    dataIndex: 'appName',
    ellipsis: true,
    sorter: (a, b) => a.appName.localeCompare(b.appName)
  },
  {
    title: '状态',
    key: 'status',
    width: 120,
    sorter: (a, b) => a.status - b.status,
    scopedSlots: { customRender: 'status' }
  },
  {
    title: '持续时间',
    key: 'keepTime',
    dataIndex: 'keepTime',
    width: 120,
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
    width: 160,
    scopedSlots: { customRender: 'action' }
  }
]

/**
 * 展开的机器列
 */
const innerColumns = [
  {
    title: '机器名称',
    key: 'name',
    dataIndex: 'machineName',
    width: 200,
    ellipsis: true,
    sorter: (a, b) => a.machineName.localeCompare(b.machineName)
  },
  {
    title: '机器主机',
    key: 'host',
    dataIndex: 'machineHost',
    width: 180,
    ellipsis: true,
    sorter: (a, b) => a.machineHost.localeCompare(b.machineHost)
  },
  {
    title: '状态',
    key: 'status',
    width: 120,
    align: 'center',
    sorter: (a, b) => a.status - b.status,
    scopedSlots: { customRender: 'status' }
  },
  {
    title: '持续时间',
    key: 'keepTime',
    dataIndex: 'keepTime',
    width: 120,
    sorter: (a, b) => (a.used || 0) - (b.used || 0)
  },
  {
    title: '操作',
    key: 'action',
    width: 120,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'AppRelease',
  components: {
    AppReleaseMachineDetailDrawer,
    AppReleaseDetailDrawer,
    AppReleaseAuditModal,
    AppReleaseModal,
    AppSelector
  },
  data: function() {
    return {
      query: {
        appId: null,
        profileId: null,
        title: null,
        status: undefined,
        description: null,
        onlyMyself: false
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
      loading: false,
      columns,
      innerColumns,
      statusHolder: statusHolder.call(this)
    }
  },
  methods: {
    chooseProfile(profile) {
      this.query.profileId = profile.id
      this.getList({})
    },
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getAppReleaseList({
        ...this.query,
        onlyMyself: this.query.onlyMyself ? 1 : 2,
        page: page.current,
        limit: page.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
        this.rows = data.rows
        this.pagination = pagination
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    openRelease() {
      this.$refs.addModal.openRelease(this.query.profileId)
    },
    openAudit(id) {
      this.$refs.auditModal.open(id)
    },
    releaseAudit(id, res) {
      const match = this.rows.filter(s => s.id === id)
      if (match && match.length) {
        match[0].status = (res ? this.$enum.RELEASE_STATUS.WAIT_RUNNABLE.value : this.$enum.RELEASE_STATUS.AUDIT_REJECT.value)
      }
    },
    runnableRelease(id) {
      this.$api.runnableAppRelease({
        id
      }).then(() => {
        this.$message.success('已提交执行请求')
      })
    },
    openReleaseLog(id) {

    },
    terminated(id) {

    },
    openReleaseDetail(id) {
      this.$refs.releaseDetail.open(id)
    },
    openMachineLog(id) {

    },
    openMachineDetail(id) {
      this.$refs.machineDetail.open(id)
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.appSelector.reset()
      this.query.appId = undefined
      this.query.status = undefined
      this.query.onlyMyself = false
      this.getList({})
    },
    pollStatus() {
    }
  },
  filters: {
    formatDate(origin, {
      date,
      pattern
    }) {
      return _utils.dateFormat(new Date(date), pattern)
    }
  },
  mounted() {
    // 读取当前环境
    const activeProfile = this.$storage.get(this.$storage.keys.ACTIVE_PROFILE)
    if (!activeProfile) {
      this.$message.warning('请先维护应用环境')
    }
    this.query.profileId = JSON.parse(activeProfile).id
    // 设置轮询
    setInterval(this.pollStatus, 5000)
    // 查询列表
    this.getList({})
  }
}
</script>

<style scoped>

</style>
