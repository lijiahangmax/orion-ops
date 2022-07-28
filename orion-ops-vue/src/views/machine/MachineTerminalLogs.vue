<template>
  <div id="machine-terminal-logs-container">
    <!-- 条件列 -->
    <div class="table-search-columns">
      <a-form-model class="machine-log-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="主机" prop="host">
              <MachineAutoComplete ref="machineSelector" @change="selectedMachine" @choose="getList({})"/>
            </a-form-model-item>
          </a-col>
          <a-col v-if="$isAdmin()" :span="5">
            <a-form-model-item label="用户" prop="user">
              <UserAutoComplete ref="userSelector" @change="selectedUser" @choose="getList({})"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="退出码" prop="closeCode">
              <a-input-number v-model="query.closeCode" :precision="0" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 工具栏 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <span class="table-title">日志列表</span>
        <a-divider v-show="selectedRowKeys.length" type="vertical"/>
        <div v-show="selectedRowKeys.length">
          <a-popconfirm title="确认删除所选中的操作日志吗?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="deleteLog(selectedRowKeys)">
            <a-button class="ml8" type="danger" icon="delete">删除</a-button>
          </a-popconfirm>
        </div>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a-upload accept=".cast" :fileList="[]" :beforeUpload="selectScreenFile">
          <a-button type="primary" icon="caret-right" class="mr8">回放</a-button>
        </a-upload>
        <a-divider type="vertical"/>
        <a-icon v-if="$isAdmin()" type="delete" class="tools-icon" title="清空" @click="openClear"/>
        <a-icon type="export" class="tools-icon" title="导出数据" @click="openExport"/>
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
        <template v-slot:machine="record">
          <a-tooltip placement="top">
            <template #title>
              <span>{{ `${record.machineName} (${record.machineHost})` }}</span>
            </template>
            <span>{{ record.machineName }}</span>
          </a-tooltip>
        </template>
        <!-- 连接时间 -->
        <template v-slot:connectedTime="record">
          {{ record.connectedTime | formatDate }} ({{ record.connectedTimeAgo }})
        </template>
        <!-- 断连时间 -->
        <template v-slot:disconnectedTime="record">
          <span v-if="record.disconnectedTime">
           {{ record.disconnectedTime | formatDate }} ({{ record.disconnectedTimeAgo }})
          </span>
        </template>
        <!-- 操作 -->
        <template v-slot:action="record">
          <!-- 回放 -->
          <span class="span-blue pointer" @click="openTerminalScreen(record)">回放</span>
          <a-divider type="vertical"/>
          <!-- 下载 -->
          <a v-if="record.downloadUrl" @click="clearDownloadUrl(record)" target="_blank" :href="record.downloadUrl">下载</a>
          <a v-else @click="loadDownloadUrl(record)">获取</a>
          <a-divider type="vertical"/>
          <!-- 删除 -->
          <a-popconfirm title="确认删除当前终端日志?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="deleteLog([record.id])">
            <span class="span-blue pointer">删除</span>
          </a-popconfirm>
        </template>
      </a-table>
    </div>
    <!-- 事件 -->
    <div class="terminal-event-container">
      <!-- 数据清理模态框 -->
      <TerminalLogClearModal ref="clear" @clear="getList({})"/>
      <!-- 数据导出模态框 -->
      <MachineTerminalLogExportModal ref="export"/>
      <!-- 终端录屏模态框 -->
      <TerminalScreen ref="screen"/>
    </div>
  </div>
</template>

<script>

import { defineArrayKey, readFileBase64 } from '@/lib/utils'
import { formatDate } from '@/lib/filters'
import MachineAutoComplete from '@/components/machine/MachineAutoComplete'
import UserAutoComplete from '@/components/user/UserAutoComplete'
import TerminalLogClearModal from '@/components/clear/TerminalLogClearModal'
import MachineTerminalLogExportModal from '@/components/export/MachineTerminalLogExportModal'
import TerminalScreen from '@/components/terminal/TerminalScreen'
import { FILE_DOWNLOAD_TYPE } from '@/lib/enum'

/**
 * 列
 */
const columns = [
  {
    title: '连接机器',
    key: 'machine',
    width: 180,
    ellipsis: true,
    sorter: (a, b) => a.machineName.localeCompare(b.machineName),
    scopedSlots: { customRender: 'machine' }
  },
  {
    title: '连接用户',
    dataIndex: 'username',
    key: 'username',
    width: 120,
    ellipsis: true,
    sorter: (a, b) => a.username.localeCompare(b.username)
  },
  {
    title: '退出码',
    dataIndex: 'closeCode',
    key: 'closeCode',
    width: 100,
    sorter: (a, b) => a.closeCode - b.closeCode
  },
  {
    title: '连接时间',
    key: 'connectedTime',
    width: 180,
    ellipsis: true,
    align: 'center',
    sorter: (a, b) => a.connectedTime - b.connectedTime,
    scopedSlots: { customRender: 'connectedTime' }
  },
  {
    title: '断连时间',
    key: 'disconnectedTime',
    width: 180,
    ellipsis: true,
    align: 'center',
    sorter: (a, b) => (a.disconnectedTime || 0) - (b.disconnectedTime || 0),
    scopedSlots: { customRender: 'disconnectedTime' }
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    align: 'center',
    fixed: 'right',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'MachineTerminalLogs',
  components: {
    TerminalScreen,
    MachineTerminalLogExportModal,
    TerminalLogClearModal,
    MachineAutoComplete,
    UserAutoComplete
  },
  data: function() {
    return {
      query: {
        userId: undefined,
        username: undefined,
        machineId: undefined,
        machineName: undefined,
        closeCode: undefined
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
      selectedRowKeys: [],
      columns
    }
  },
  computed: {
    rowSelection() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        columnWidth: '50px',
        onChange: e => {
          this.selectedRowKeys = e
        }
      }
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.terminalLogList({
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
    deleteLog(idList) {
      this.$api.deleteTerminalLog({
        idList: idList
      }).then(() => {
        this.$message.success('删除成功')
        this.getList({})
      })
    },
    openClear() {
      this.$refs.clear.open()
    },
    openExport() {
      this.$refs.export.open()
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.machineSelector.reset()
      this.$refs.userSelector && this.$refs.userSelector.reset()
      this.query.machineId = undefined
      this.query.machineName = undefined
      this.query.userId = undefined
      this.query.username = undefined
      this.getList({})
    },
    selectScreenFile(e) {
      readFileBase64(e).then(r => {
        this.$refs.screen.openFile(r)
      })
      return false
    },
    openTerminalScreen(record) {
      this.$refs.screen.open(record)
    },
    async loadDownloadUrl(record) {
      try {
        const downloadUrl = await this.$api.getFileDownloadToken({
          type: FILE_DOWNLOAD_TYPE.TERMINAL_SCREEN.value,
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
    }
  },
  filters: {
    formatDate
  },
  mounted() {
    this.getList({})
  }
}
</script>

<style scoped>

</style>
