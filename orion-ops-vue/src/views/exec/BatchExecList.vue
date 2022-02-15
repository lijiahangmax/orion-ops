<template>
  <div class="exec-container">
    <!-- 筛选列 -->
    <div class="table-search-columns">
      <a-form-model ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="主机" prop="machine">
              <MachineSelector ref="machineSelector" @change="machineId => query.machineId = machineId"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="命令" prop="command">
              <a-input v-model="query.command" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="状态" prop="status">
              <a-select v-model="query.status" placeholder="全部" allowClear>
                <a-select-option :value="status.value" v-for="status in $enum.BATCH_EXEC_STATUS" :key="status.value">
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
            <a-form-model-item label="退出码" prop="exitCode">
              <a-input-number v-model="query.exitCode" :precision="0" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row v-if="$isAdmin()" style="margin-top: 8px">
          <a-col :span="5">
            <a-form-model-item label="用户" prop="user">
              <UserSelector ref="userSelector" @change="userId => query.userId = userId"/>
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
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a target="_blank" href="#/batch/exec/add">
          <a-button class="ml16 mr8"
                    type="primary"
                    icon="code"
                    title="ctrl 打开新页面"
                    @click="openExecute">
            批量执行
          </a-button>
        </a>
        <a-divider type="vertical"/>
        <a-icon type="search" class="tools-icon" title="查询" @click="getList({})"/>
        <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
      </div>
    </div>
    <!-- 表格 -->
    <div class="table-main-container table-scroll-x-auto">
      <a-table :columns="columns"
               :dataSource="rows"
               :pagination="pagination"
               rowKey="id"
               @change="getList"
               :scroll="{x: '100%'}"
               :loading="loading"
               size="middle">
        <!-- 主机 -->
        <template v-slot:machine="record">
          <a-tooltip placement="top">
            <template #title>
              <span>{{ `${record.machineName} (${record.machineHost})` }}</span>
            </template>
            <span>{{ record.machineName }}</span>
          </a-tooltip>
        </template>
        <!-- 命令 -->
        <template v-slot:command="record">
          <span class="pointer" title="预览" @click="previewEditor(record.command)">
              {{ record.command }}
          </span>
        </template>
        <!-- 状态 -->
        <template v-slot:status="record">
          <a-tag class="m0" :color="$enum.valueOf($enum.BATCH_EXEC_STATUS, record.status).color">
            {{ $enum.valueOf($enum.BATCH_EXEC_STATUS, record.status).label }}
          </a-tag>
        </template>
        <!-- 退出码 -->
        <template v-slot:exitCode="record">
          <span :style="{'color': record.exitCode === 0 ? '#4263EB' : '#E03131'}">
            {{ record.exitCode }}
          </span>
        </template>
        <!-- 描述 -->
        <template v-slot:description="record">
          <span class="pointer" @click="previewText(record.description)">
            {{ record.description }}
          </span>
        </template>
        <!-- 创建时间 -->
        <template v-slot:createTime="record">
          {{ record.createTime | formatDate }}
        </template>
        <!-- 日志 -->
        <template v-slot:log="record">
          <!-- 日志面板 -->
          <a target="_blank"
             title="ctrl 打开新页面"
             :href="`#/batch/exec/log/view/${record.id}`"
             @click="openLogView($event, record.id)">日志</a>
          <a-divider type="vertical"/>
          <!-- 下载 -->
          <a v-if="record.downloadUrl" @click="clearDownloadUrl(record)" target="_blank" :href="record.downloadUrl">下载</a>
          <a v-else @click="loadDownloadUrl(record)">获取</a>
        </template>
        <!-- 操作 -->
        <template v-slot:action="record">
          <!-- 详情 -->
          <a @click="detail(record.id)">详情</a>
          <a-divider type="vertical"/>
          <!-- 再次执行 -->
          <a-popconfirm :title="record.status === $enum.BATCH_EXEC_STATUS.RUNNABLE.value ? '当前任务未执行完毕, 确定再次执行?' : '是否再次执行当前任务?'"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="redo(record)">
            <span class="span-blue pointer">再次执行</span>
          </a-popconfirm>
          <a-divider v-if="record.status === $enum.BATCH_EXEC_STATUS.RUNNABLE.value" type="vertical"/>
          <!-- 停止 -->
          <a-popconfirm v-if="record.status === $enum.BATCH_EXEC_STATUS.RUNNABLE.value"
                        title="确认停止当前任务?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="terminated(record.id)">
            <span class="span-blue pointer">停止</span>
          </a-popconfirm>
          <a-divider v-if="record.status !== $enum.BATCH_EXEC_STATUS.RUNNABLE.value" type="vertical"/>
          <!-- 删除 -->
          <a-popconfirm v-if="record.status !== $enum.BATCH_EXEC_STATUS.RUNNABLE.value"
                        title="确认删除当前任务?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="deleteTask(record.id)">
            <span class="span-blue pointer">删除</span>
          </a-popconfirm>
        </template>
      </a-table>
    </div>
    <!-- 表格 -->
    <div class="exec-event-container">
      <!-- 编辑器预览 -->
      <EditorPreview ref="previewEditor"/>
      <!-- 文本预览 -->
      <TextPreview ref="previewText"/>
      <!-- 详情 -->
      <ExecTaskDetailModal ref="detail"/>
      <!-- 日志模态框 -->
      <ExecLoggerAppenderModal ref="logView"/>
    </div>
  </div>
</template>

<script>

import MachineSelector from '@/components/machine/MachineSelector'
import UserSelector from '@/components/user/UserSelector'
import EditorPreview from '@/components/preview/EditorPreview'
import TextPreview from '@/components/preview/TextPreview'
import ExecTaskDetailModal from '@/components/exec/ExecTaskDetailModal'
import ExecLoggerAppenderModal from '@/components/log/ExecLoggerAppenderModal'
import _filters from '@/lib/filters'

/**
 * 列
 */
const columns = [
  {
    title: '序号',
    key: 'seq',
    width: 65,
    align: 'center',
    customRender: (text, record, index) => `${index + 1}`
  },
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
    width: 260,
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
    sorter: (a, b) => a.username.localeCompare(b.username)
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
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'BatchExecList',
  components: {
    ExecLoggerAppenderModal,
    MachineSelector,
    UserSelector,
    EditorPreview,
    TextPreview,
    ExecTaskDetailModal
  },
  data: function() {
    return {
      query: {
        command: null,
        exitCode: null,
        userId: undefined,
        machineId: undefined,
        status: undefined,
        description: null
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
      columns
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
        this.$utils.defineArrayKey(data.rows, 'downloadUrl')
        this.rows = data.rows || []
        this.pagination = pagination
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
    terminated(execId) {
      this.$api.terminatedExecTask({
        id: execId
      }).then(() => {
        this.$message.success('已停止')
        this.getList({})
      })
    },
    deleteTask(execId) {
      this.$api.deleteExecTask({
        id: execId
      }).then(() => {
        this.$message.success('已删除')
        this.getList({})
      })
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.machineSelector.reset()
      this.$refs.userSelector.reset()
      this.query.machineId = undefined
      this.query.userId = undefined
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
          type: this.$enum.FILE_DOWNLOAD_TYPE.EXEC_LOG.value,
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
      const idList = this.rows.filter(r => r.status === this.$enum.BATCH_EXEC_STATUS.WAITING.value ||
        r.status === this.$enum.BATCH_EXEC_STATUS.RUNNABLE.value)
        .map(s => s.id)
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
      })
    }
  },
  filters: {
    ..._filters
  },
  mounted() {
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

/deep/ .ant-row {
  margin: 0;
}

</style>
