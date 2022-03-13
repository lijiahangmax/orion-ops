<template>
  <div class="scheduler-record-container">
    <!-- 调度任务菜单 -->
    <div class="scheduler-task-menu">
      <!-- 任务列表头 -->
      <div class="scheduler-task-header">
        <a-page-header @back="() => {}">
          <template #title>
            <span class="ant-page-header-heading-title pointer" title="刷新" @click="getSchedulerTask">任务列表</span>
          </template>
          <template #backIcon>
            <a-icon type="stop" title="清空" @click="chooseTask(0)"/>
          </template>
        </a-page-header>
      </div>
      <!-- 机器菜单 -->
      <a-spin :spinning="taskLoading">
        <div class="task-list-wrapper">
          <a-menu mode="inline" v-model="selectedTaskIds">
            <a-menu-item v-for="task in taskList" :key="task.id" :title="task.name" @click="chooseTask(task.id)">
              <a-icon type="carry-out"/>
              {{ task.name }}
            </a-menu-item>
          </a-menu>
        </div>
      </a-spin>
    </div>
    <!-- 调度明细表格 -->
    <div class="scheduler-task-record-container">
      <!-- 搜索列 -->
      <div class="search-columns-wrapper">
        <div class="table-search-columns search-columns">
          <a-form-model class="scheduler-search-form" ref="query" :model="query">
            <a-row>
              <a-col :span="6">
                <a-form-model-item label="任务名称" prop="taskName">
                  <a-input v-model="query.taskName" allowClear/>
                </a-form-model-item>
              </a-col>
              <a-col :span="6">
                <a-form-model-item label="执行状态" prop="status">
                  <a-select v-model="query.status" placeholder="全部" allowClear>
                    <a-select-option :value="type.value" v-for="type in $enum.SCHEDULER_TASK_STATUS" :key="type.value">
                      {{ type.label }}
                    </a-select-option>
                  </a-select>
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-form-model>
        </div>
      </div>
      <!-- 表格 -->
      <div class="record-table-wrapper">
        <!-- 工具栏 -->
        <div class="table-tools-bar">
          <!-- 左侧 -->
          <div class="tools-fixed-left">
            <span class="table-title">任务明细</span>
            <a-divider v-show="selectedRowKeys.length" type="vertical"/>
            <div v-show="selectedRowKeys.length">
              <a-popconfirm title="是否要删除选中调度记录?"
                            placement="topRight"
                            ok-text="确定"
                            cancel-text="取消"
                            @confirm="deleteRecord(selectedRowKeys)">
                <a-button class="ml8" type="danger" icon="delete">删除</a-button>
              </a-popconfirm>
            </div>
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
                   :expandedRowKeys.sync="expandedRowKeys"
                   rowKey="id"
                   @change="getList"
                   @expand="expandMachine"
                   :loading="loading"
                   size="middle">
            <!-- 展开明细 -->
            <template v-slot:expandedRowRender="record">
              <a-table
                v-if="record.machines"
                :rowKey="(record, index) => index"
                :columns="innerColumns"
                :dataSource="record.machines"
                :loading="record.loading"
                :pagination="false"
                size="middle">
                <!-- 状态 -->
                <template v-slot:status="machine">
                  <a-tag :color="$enum.valueOf($enum.SCHEDULER_TASK_MACHINE_STATUS, machine.status).color">
                    {{ $enum.valueOf($enum.SCHEDULER_TASK_MACHINE_STATUS, machine.status).label }}
                  </a-tag>
                </template>
                <!-- 操作 -->
                <template v-slot:action="machine">
                  <!-- 日志 -->
                  <a-button class="p0"
                            type="link"
                            style="height: 22px"
                            :disabled="!visibleHolder.visibleMachineLog(machine.status)">
                    <a target="_blank"
                       title="ctrl 打开新页面"
                       :href="`#/task/machine/log/view/${machine.id}`"
                       @click="openMachineLog($event, machine.id)">日志</a>
                  </a-button>
                  <a-divider type="vertical"/>
                  <!-- 命令 -->
                  <span class="span-blue pointer" @click="previewCommand(machine.command)">命令</span>
                  <a-divider type="vertical" v-if="visibleHolder.visibleMachineTerminated(machine.status)"/>
                  <!-- 停止 -->
                  <a-popconfirm v-if="visibleHolder.visibleMachineTerminated(machine.status)"
                                title="是否要停止执行?"
                                placement="topRight"
                                ok-text="确定"
                                cancel-text="取消"
                                @confirm="terminatedMachine(record.id, machine.id)">
                    <span class="span-blue pointer">停止</span>
                  </a-popconfirm>
                  <a-divider type="vertical" v-if="visibleHolder.visibleMachineSkip(machine.status)"/>
                  <!-- 跳过 -->
                  <a-popconfirm v-if="visibleHolder.visibleMachineSkip(machine.status)"
                                title="是否要跳过执行?"
                                placement="topRight"
                                ok-text="确定"
                                cancel-text="取消"
                                @confirm="skipMachine(record.id, machine.id)">
                    <span class="span-blue pointer">跳过</span>
                  </a-popconfirm>
                </template>
              </a-table>
            </template>
            <!-- 状态 -->
            <template v-slot:status="record">
              <a-tag :color="$enum.valueOf($enum.SCHEDULER_TASK_STATUS, record.status).color">
                {{ $enum.valueOf($enum.SCHEDULER_TASK_STATUS, record.status).label }}
              </a-tag>
            </template>
            <!-- 开始时间 -->
            <template v-slot:startTime="record">
              {{ record.startTime | formatDate }}
            </template>
            <!-- 结束时间 -->
            <template v-slot:endTime="record">
              <template v-if="record.endTime">
                {{ record.endTime | formatDate }}
              </template>
            </template>
            <!-- 操作 -->
            <template v-slot:action="record">
              <!-- 日志 -->
              <a-button class="p0"
                        type="link"
                        style="height: 22px"
                        :disabled="!visibleHolder.visibleRecordLog(record.status)">
                <a target="_blank"
                   title="ctrl 打开新页面"
                   :href="`#/task/log/view/${record.id}`"
                   @click="openTaskLog($event, record.id)">日志</a>
              </a-button>
              <a-divider type="vertical" v-if="visibleHolder.visibleRecordTerminated(record.status)"/>
              <!-- 停止 -->
              <a-popconfirm v-if="visibleHolder.visibleRecordTerminated(record.status)"
                            title="是否要停止执行?"
                            placement="topRight"
                            ok-text="确定"
                            cancel-text="取消"
                            @confirm="terminated(record.id)">
                <span class="span-blue pointer">停止</span>
              </a-popconfirm>
              <a-divider type="vertical" v-if="visibleHolder.visibleRecordDelete(record.status)"/>
              <!-- 停止 -->
              <a-popconfirm v-if="visibleHolder.visibleRecordDelete(record.status)"
                            title="是否要删除当前调度记录?"
                            placement="topRight"
                            ok-text="确定"
                            cancel-text="取消"
                            @confirm="deleteRecord([record.id])">
                <span class="span-blue pointer">删除</span>
              </a-popconfirm>
            </template>
          </a-table>
        </div>
      </div>
    </div>
    <!-- 事件 -->
    <div class="scheduler-event-container">
      <!-- 预览 -->
      <EditorPreview ref="preview"/>
      <!-- 任务日志 -->
      <SchedulerTaskLogAppenderModal ref="taskLoggerView"/>
      <!-- 机器日志 -->
      <SchedulerTaskMachineLogAppenderModal ref="machineLoggerView"/>
    </div>
  </div>
</template>

<script>

import _filters from '@/lib/filters'
import _enum from '@/lib/enum'
import EditorPreview from '@/components/preview/EditorPreview'
import SchedulerTaskMachineLogAppenderModal from '@/components/log/SchedulerTaskMachineLogAppenderModal'
import SchedulerTaskLogAppenderModal from '@/components/log/SchedulerTaskLogAppenderModal'

/**
 * 列
 */
const columns = [
  {
    title: '任务名称',
    dataIndex: 'taskName',
    key: 'taskName',
    ellipsis: true
  },
  {
    title: '状态',
    key: 'status',
    width: 140,
    align: 'center',
    sorter: (a, b) => a.status - b.status,
    scopedSlots: { customRender: 'status' }
  },
  {
    title: '开始时间',
    key: 'startTime',
    align: 'center',
    ellipsis: true,
    sorter: (a, b) => (a.startTime || 0) - (b.startTime || 0),
    scopedSlots: { customRender: 'startTime' }
  },
  {
    title: '结束时间',
    key: 'endTime',
    align: 'center',
    ellipsis: true,
    sorter: (a, b) => (a.endTime || 0) - (b.endTime || 0),
    scopedSlots: { customRender: 'endTime' }
  },
  {
    title: '持续时间',
    key: 'keepTime',
    dataIndex: 'keepTime',
    ellipsis: true,
    sorter: (a, b) => (a.used || 0) - (b.used || 0)
  },
  {
    title: '操作',
    key: 'action',
    width: 180,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

/**
 * 展开列
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
    width: 200,
    ellipsis: true,
    sorter: (a, b) => a.machineHost.localeCompare(b.machineHost)
  },
  {
    title: '状态',
    key: 'status',
    width: 140,
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
    width: 140,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

/**
 * 显示判断
 */
const visibleHolder = {
  visibleRecordLog(status) {
    return status !== _enum.SCHEDULER_TASK_STATUS.WAIT.value
  },
  visibleRecordTerminated(status) {
    return status === _enum.SCHEDULER_TASK_STATUS.RUNNABLE.value
  },
  visibleRecordDelete(status) {
    return status !== _enum.SCHEDULER_TASK_STATUS.WAIT.value &&
      status !== _enum.SCHEDULER_TASK_STATUS.RUNNABLE.value
  },
  visibleMachineLog(status) {
    return status !== _enum.SCHEDULER_TASK_MACHINE_STATUS.WAIT.value &&
      status !== _enum.SCHEDULER_TASK_MACHINE_STATUS.SKIPPED.value
  },
  visibleMachineTerminated(status) {
    return status === _enum.SCHEDULER_TASK_MACHINE_STATUS.RUNNABLE.value
  },
  visibleMachineSkip(status) {
    return status === _enum.SCHEDULER_TASK_MACHINE_STATUS.WAIT.value
  }
}

export default {
  name: 'ScheduleRecord',
  components: {
    SchedulerTaskLogAppenderModal,
    SchedulerTaskMachineLogAppenderModal,
    EditorPreview
  },
  data() {
    return {
      taskLoading: false,
      taskList: [],
      selectedTaskIds: [0],
      query: {
        taskName: undefined,
        status: undefined
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
      columns,
      innerColumns,
      visibleHolder,
      selectedRowKeys: [],
      expandedRowKeys: []
    }
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
            disabled: record.status === this.$enum.SCHEDULER_TASK_STATUS.WAIT.value ||
              record.status === this.$enum.SCHEDULER_TASK_STATUS.RUNNABLE.value
          }
        })
      }
    }
  },
  methods: {
    async getSchedulerTask() {
      this.taskLoading = true
      await this.$api.getSchedulerTaskList({
        limit: 10000
      }).then(({ data }) => {
        this.taskLoading = false
        this.taskList = data.rows || []
      }).catch(() => {
        this.taskLoading = false
      })
    },
    chooseTask(id) {
      this.selectedTaskIds = [id]
      this.getList({})
    },
    getList(page = this.pagination) {
      this.loading = true
      this.expandedRowKeys = []
      this.$api.getSchedulerTaskRecordList({
        ...this.query,
        taskId: this.selectedTaskIds[0] || undefined,
        page: page.current,
        limit: page.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
        this.$utils.defineArrayKey(data.rows, 'loading', false)
        this.$utils.defineArrayKey(data.rows, 'machines', [])
        this.rows = data.rows || []
        this.pagination = pagination
        this.selectedRowKeys = []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    expandMachine(expand, record) {
      if (!expand || record.machines.length) {
        return
      }
      // 加载机器
      record.loading = true
      this.$api.getSchedulerTaskMachinesRecordList({
        recordId: record.id
      }).then(({ data }) => {
        record.loading = false
        record.machines = data
      }).catch(() => {
        record.loading = false
      })
    },
    openTaskLog(e, id) {
      if (!e.ctrlKey) {
        e.preventDefault()
        // 打开模态框
        this.$refs.taskLoggerView.open(id)
        return false
      } else {
        // 跳转页面
        return true
      }
    },
    openMachineLog(e, id) {
      if (!e.ctrlKey) {
        e.preventDefault()
        // 打开模态框
        this.$refs.machineLoggerView.open(id)
        return false
      } else {
        // 跳转页面
        return true
      }
    },
    deleteRecord(idList) {
      this.$api.deleteSchedulerTaskRecord({
        idList
      }).then(() => {
        this.$message.success('已删除')
        this.getList({})
      })
    },
    terminated(id) {
      this.$api.terminatedAllSchedulerTaskRecord({
        id
      }).then(() => {
        this.$message.success('已停止')
      })
    },
    terminatedMachine(id, machineRecordId) {
      this.$api.terminatedMachineSchedulerTaskRecord({
        id,
        machineRecordId
      }).then(() => {
        this.$message.success('已停止')
      })
    },
    skipMachine(id, machineRecordId) {
      this.$api.skipMachineSchedulerTaskRecord({
        id,
        machineRecordId
      }).then(() => {
        this.$message.success('已跳过')
      })
    },
    previewCommand(command) {
      this.$refs.preview.preview(command)
    },
    pollStatus() {
      if (!this.rows || !this.rows.length) {
        return
      }
      const pollItems = this.rows.filter(r => r.status === this.$enum.SCHEDULER_TASK_STATUS.WAIT.value ||
        r.status === this.$enum.SCHEDULER_TASK_STATUS.RUNNABLE.value)
      if (!pollItems.length) {
        return
      }
      const idList = pollItems.map(s => s.id)
      if (!idList.length) {
        return
      }
      const machineRecordIdList = pollItems.map(s => s.machines)
        .filter(s => s && s.length)
        .flat()
        .map(s => s.id)
      this.$api.getSchedulerTaskRecordStatus({
        idList,
        machineRecordIdList
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
            row.startTime = status.startTime
            row.endTime = status.endTime
            if (!status.machines || !status.machines.length || !row.machines || !row.machines.length) {
              return
            }
            // 机器状态
            for (const machine of status.machines) {
              row.machines.filter(m => m.id === machine.id).forEach(m => {
                m.status = machine.status
                m.keepTime = machine.keepTime
                m.used = machine.used
              })
            }
          })
        }
        // 强制刷新状态
        this.$set(this.rows, 0, this.rows[0])
      })
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.query.status = undefined
      this.getList({})
    }
  },
  filters: {
    ..._filters
  },
  async mounted() {
    await this.getSchedulerTask()
    if (this.$route.params.id) {
      this.chooseTask(parseInt(this.$route.params.id))
    } else if (this.taskList.length) {
      this.chooseTask(this.taskList[0].id)
    }
    // 设置轮询
    this.pollId = setInterval(this.pollStatus, 5000)
  },
  beforeDestroy() {
    this.pollId !== null && clearInterval(this.pollId)
    this.pollId = null
  }
}
</script>

<style lang="less" scoped>

.scheduler-record-container {
  display: flex;

  .scheduler-task-menu {
    width: 216px;
    padding: 0 8px 8px 8px;
    margin-right: 16px;
    background-color: #FFF;
    border-radius: 4px;
  }

  .scheduler-task-record-container {
    width: calc(100% - 232px);
    background-color: #FFF;
    border-radius: 4px;
    min-height: calc(100vh - 84px);
  }

  .search-columns-wrapper {
    .search-columns {
      margin-bottom: 0;
      border-radius: 4px;
    }

    padding-bottom: 18px;
    background: #F0F2F5;
  }

  .record-table-wrapper {
    background: #F0F2F5;

    .table-main-container {
      border-radius: 0;
    }
  }
}

/deep/ .ant-table-expand-icon-th, /deep/ .ant-table-row-expand-icon-cell {
  width: 45px;
  min-width: 45px;
}

</style>
