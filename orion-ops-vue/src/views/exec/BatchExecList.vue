<template>
  <div class="exec-container">
    <!-- 筛选列 -->
    <div class="table-search-columns">
      <a-form-model ref="query" :model="query">
        <a-row>
          <a-col v-if="$isAdmin()" :span="5">
            <a-form-model-item label="用户" prop="user">
              <UserAutoComplete ref="userSelector" @change="selectedUser" @choose="getList({})"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="主机" prop="machine">
              <MachineAutoComplete ref="machineSelector" @change="selectedMachine" @choose="getList({})"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="命令" prop="command">
              <a-input v-model="query.command" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="描述" prop="description">
              <a-input v-model="query.description" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <a-form-model-item label="退出码" prop="exitCode">
              <a-input-number v-model="query.exitCode" :precision="0" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 工具栏 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <span class="table-title">执行列表</span>
        <a-divider v-show="selectedRowKeys.length" type="vertical"/>
        <div v-show="selectedRowKeys.length">
          <a-popconfirm title="确认删除所选中的执行记录吗?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="deleteTask(selectedRowKeys)">
            <a-button class="ml8" type="danger" icon="delete">删除</a-button>
          </a-popconfirm>
        </div>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <!-- 状态 -->
        <div class="exec-status-container">
          <a-radio-group class="nowrap"
                         v-model="query.status"
                         @change="getList({})">
            <a-radio-button :value="undefined">全部</a-radio-button>
            <a-radio-button v-for="status in BATCH_EXEC_STATUS"
                            :key="status.value"
                            :value="status.value">
              {{ status.label }}
            </a-radio-button>
          </a-radio-group>
        </div>
        <a-divider type="vertical"/>
        <!-- 执行 -->
        <a target="_blank" href="#/batch/exec/add">
          <a-button class="mx8"
                    type="primary"
                    icon="code"
                    title="ctrl 点击打开新页面"
                    @click="openExecute">
            批量执行
          </a-button>
        </a>
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
               :scroll="{x: '100%'}"
               :loading="loading"
               size="middle">
        <!-- 主机 -->
        <template #machine="record">
          <a-tooltip placement="top">
            <template #title>
              <span>{{ `${record.machineName} (${record.machineHost})` }}</span>
            </template>
            <span>{{ record.machineName }}</span>
          </a-tooltip>
        </template>
        <!-- 命令 -->
        <template #command="record">
          <span class="pointer" title="预览" @click="previewEditor(record.command)">
              {{ record.command }}
          </span>
        </template>
        <!-- 状态 -->
        <template #status="record">
          <a-tag class="m0" :color="record.status | formatExecStatus('color')">
            {{ record.status | formatExecStatus('label') }}
          </a-tag>
        </template>
        <!-- 退出码 -->
        <template #exitCode="record">
          <span :style="{'color': record.exitCode === 0 ? '#4263EB' : '#E03131'}">
            {{ record.exitCode }}
          </span>
        </template>
        <!-- 描述 -->
        <template #description="record">
          <span class="pointer" @click="previewText(record.description)">
            {{ record.description }}
          </span>
        </template>
        <!-- 创建时间 -->
        <template #createTime="record">
          {{ record.createTime | formatDate }}
        </template>
        <!-- 日志 -->
        <template #log="record">
          <!-- 日志面板 -->
          <a-tooltip title="ctrl 点击打开新页面">
            <a target="_blank"
               :href="`#/batch/exec/log/view/${record.id}`"
               @click="openLogView($event, record.id)">日志</a>
          </a-tooltip>
          <a-divider type="vertical"/>
          <!-- 下载 -->
          <a v-if="record.downloadUrl" @click="clearDownloadUrl(record)" target="_blank" :href="record.downloadUrl">下载</a>
          <a v-else @click="loadDownloadUrl(record)">获取</a>
        </template>
        <!-- 操作 -->
        <template #action="record">
          <!-- 详情 -->
          <a @click="detail(record.id)">详情</a>
          <a-divider type="vertical"/>
          <!-- 再次执行 -->
          <a-popconfirm :title="record.status === BATCH_EXEC_STATUS.RUNNABLE.value ? '当前任务未执行完毕, 确定再次执行?' : '是否再次执行当前任务?'"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="redo(record)">
            <span class="span-blue pointer">再次执行</span>
          </a-popconfirm>
          <a-divider v-if="visibleHolder.visibleTerminate(record.status)" type="vertical"/>
          <!-- 停止 -->
          <a-popconfirm v-if="visibleHolder.visibleTerminate(record.status)"
                        title="确认停止当前任务?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="terminate(record.id)">
            <span class="span-blue pointer">停止</span>
          </a-popconfirm>
          <a-divider v-if="visibleHolder.visibleDelete(record.status)" type="vertical"/>
          <!-- 删除 -->
          <a-popconfirm v-if="visibleHolder.visibleDelete(record.status)"
                        title="确认删除当前任务?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="deleteTask([record.id])">
            <span class="span-blue pointer">删除</span>
          </a-popconfirm>
        </template>
      </a-table>
    </div>
    <!-- 事件 -->
    <div class="exec-event-container">
      <!-- 编辑器预览 -->
      <EditorPreview ref="previewEditor"/>
      <!-- 文本预览 -->
      <TextPreview ref="previewText"/>
      <!-- 详情 -->
      <ExecTaskDetailModal ref="detail"/>
      <!-- 日志模态框 -->
      <ExecLoggerAppenderModal ref="logView"/>
      <!-- 清空模态框 -->
      <BatchExecClearModal ref="clear" @clear="getList({})"/>
    </div>
  </div>
</template>

<script>
import { defineArrayKey } from '@/lib/utils'
import { formatDate } from '@/lib/filters'
import { enumValueOf, BATCH_EXEC_STATUS, FILE_DOWNLOAD_TYPE } from '@/lib/enum'
import UserAutoComplete from '@/components/user/UserAutoComplete'
import EditorPreview from '@/components/preview/EditorPreview'
import TextPreview from '@/components/preview/TextPreview'
import ExecTaskDetailModal from '@/components/exec/ExecTaskDetailModal'
import ExecLoggerAppenderModal from '@/components/log/ExecLoggerAppenderModal'
import MachineAutoComplete from '@/components/machine/MachineAutoComplete'
import BatchExecClearModal from '@/components/clear/BatchExecClearModal'

/**
 * 状态判断
 */
const visibleHolder = {
  visibleTerminate(status) {
    return status === BATCH_EXEC_STATUS.RUNNABLE.value
  },
  visibleDelete(status) {
    return status !== BATCH_EXEC_STATUS.WAITING.value && status !== BATCH_EXEC_STATUS.RUNNABLE.value
  }
}

/**
 * 列
 */
const getColumns = function() {
  const columns = [
    {
      title: '执行机器',
      key: 'machine',
      width: 180,
      ellipsis: true,
      sorter: (a, b) => a.machineName.localeCompare(b.machineName),
      scopedSlots: { customRender: 'machine' }
    },
    {
      title: '执行命令',
      key: 'command',
      ellipsis: true,
      width: 220,
      scopedSlots: { customRender: 'command' }
    },
    {
      title: '状态',
      key: 'status',
      width: 100,
      align: 'center',
      sorter: (a, b) => a.status - b.status,
      scopedSlots: { customRender: 'status' }
    },
    {
      title: '退出码',
      key: 'exitCode',
      width: 100,
      align: 'center',
      sorter: (a, b) => a.exitCode - b.exitCode,
      scopedSlots: { customRender: 'exitCode' }
    },
    {
      title: '持续时间',
      key: 'keepTime',
      dataIndex: 'keepTime',
      width: 120,
      sorter: (a, b) => (a.used || 0) - (b.exitCode || 0)
    },
    {
      title: '执行用户',
      dataIndex: 'username',
      key: 'username',
      width: 120,
      ellipsis: true,
      sorter: (a, b) => a.username.localeCompare(b.username),
      requireAdmin: true
    },
    {
      title: '创建时间',
      key: 'createTime',
      width: 150,
      ellipsis: true,
      align: 'center',
      sorter: (a, b) => a.createTime - b.createTime,
      scopedSlots: { customRender: 'createTime' }
    },
    {
      title: '描述',
      key: 'description',
      ellipsis: true,
      width: 180,
      scopedSlots: { customRender: 'description' }
    },
    {
      title: '日志',
      key: 'log',
      fixed: 'right',
      width: 100,
      align: 'center',
      scopedSlots: { customRender: 'log' }
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 175,
      scopedSlots: { customRender: 'action' }
    }
  ]
  if (this.$isAdmin()) {
    return columns
  } else {
    return columns.filter(s => !s.requireAdmin)
  }
}

export default {
  name: 'BatchExecList',
  components: {
    BatchExecClearModal,
    MachineAutoComplete,
    ExecLoggerAppenderModal,
    UserAutoComplete,
    EditorPreview,
    TextPreview,
    ExecTaskDetailModal
  },
  data: function() {
    return {
      BATCH_EXEC_STATUS,
      query: {
        id: undefined,
        command: undefined,
        exitCode: undefined,
        userId: undefined,
        username: undefined,
        machineId: undefined,
        machineName: undefined,
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
      loading: false,
      pollId: null,
      columns: getColumns.call(this),
      selectedRowKeys: [],
      visibleHolder
    }
  },
  computed: {
    rowSelection() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        onChange: e => {
          this.selectedRowKeys = e
        },
        getCheckboxProps: record => ({
          props: {
            disabled: record.status === BATCH_EXEC_STATUS.WAITING.value ||
              record.status === BATCH_EXEC_STATUS.RUNNABLE.value
          }
        })
      }
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getExecList({
        ...this.query,
        page: page.current,
        limit: page.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
        defineArrayKey(data.rows, 'downloadUrl')
        this.rows = data.rows || []
        this.pagination = pagination
        this.selectedRowKeys = []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    detail(id) {
      this.$refs.detail.open(id)
    },
    previewEditor(value) {
      this.$refs.previewEditor.preview(value)
    },
    previewText(value) {
      this.$refs.previewText.preview(value)
    },
    redo(record) {
      this.$api.submitExecTask({
        machineIdList: [record.machineId],
        command: record.command,
        description: record.description
      }).then(() => {
        this.$message.success('已执行')
        this.getList({})
      })
    },
    terminate(execId) {
      this.$api.terminateExecTask({
        id: execId
      }).then(() => {
        this.$message.success('已停止')
        this.getList({})
      })
    },
    deleteTask(idList) {
      this.$api.deleteExecTask({
        idList
      }).then(() => {
        this.$message.success('已删除')
        this.getList({})
      })
    },
    selectedMachine(id, name) {
      if (id) {
        this.query.machineId = id
        this.query.machineName = undefined
      } else {
        this.query.machineId = undefined
        this.query.machineName = name
      }
      if (id === undefined && name === undefined) {
        this.getList({})
      }
    },
    selectedUser(id, name) {
      if (id) {
        this.query.userId = id
        this.query.username = undefined
      } else {
        this.query.userId = undefined
        this.query.username = name
      }
      if (id === undefined && name === undefined) {
        this.getList({})
      }
    },
    openClear() {
      this.$refs.clear.open()
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.machineSelector.reset()
      this.$refs.userSelector && this.$refs.userSelector.reset()
      this.query.id = undefined
      this.query.machineId = undefined
      this.query.machineName = undefined
      this.query.userId = undefined
      this.query.username = undefined
      this.query.status = undefined
      this.getList({})
    },
    openExecute(e) {
      if (!e.ctrlKey) {
        e.preventDefault()
        // 跳转路由
        this.$router.push({ path: '/batch/exec/add' })
        return false
      } else {
        // 跳转页面
        return true
      }
    },
    openLogView(e, id) {
      if (!e.ctrlKey) {
        e.preventDefault()
        // 打开模态框
        this.$refs.logView.open(id)
        return false
      } else {
        // 跳转页面
        return true
      }
    },
    async loadDownloadUrl(record) {
      try {
        const downloadUrl = await this.$api.getFileDownloadToken({
          type: FILE_DOWNLOAD_TYPE.EXEC_LOG.value,
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
    pollStatus() {
      if (!this.rows || !this.rows.length) {
        return
      }
      const idList = this.rows.filter(r => r.status === BATCH_EXEC_STATUS.WAITING.value ||
        r.status === BATCH_EXEC_STATUS.RUNNABLE.value).map(s => s.id)
      if (!idList.length) {
        return
      }
      this.$api.getExecTaskStatus({
        idList
      }).then(({ data }) => {
        for (const status of data) {
          this.rows.filter(s => s.id === status.id).forEach(s => {
            s.status = status.status
            s.exitCode = status.exitCode
            s.keepTime = status.keepTime
            s.used = status.used
          })
        }
        // 强制刷新状态
        this.$set(this.rows, 0, this.rows[0])
      })
    }
  },
  filters: {
    formatDate,
    formatExecStatus(status, f) {
      return enumValueOf(BATCH_EXEC_STATUS, status)[f]
    }
  },
  mounted() {
    this.query.id = this.$route.query.id
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

.exec-status-container {
  margin-right: 8px;

  ::v-deep .ant-row.ant-form-item {
    display: flex;
    margin: 0;
  }
}

</style>
