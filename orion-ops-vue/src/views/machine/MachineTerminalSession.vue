<template>
  <div id="machine-terminal-session-container">
    <!-- 条件列 -->
    <div class="table-search-columns">
      <a-form-model class="machine-session-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="6">
            <a-form-model-item label="主机" prop="host">
              <MachineAutoComplete ref="machineSelector" @change="selectedMachine" @choose="getList({})"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="6">
            <a-form-model-item label="用户" prop="user">
              <UserAutoComplete ref="userSelector" @change="selectedUser" @choose="getList({})"/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 工具栏 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <span class="table-title">会话列表</span>
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
               rowKey="token"
               @change="getList"
               :scroll="{x: '100%'}"
               :loading="loading"
               size="middle">
        <!-- 连接时间 -->
        <template v-slot:connectedTime="record">
          {{ record.connectedTime | formatDate }} ({{ record.connectedTimeAgo }})
        </template>
        <!-- 操作 -->
        <template v-slot:action="record">
          <a-tooltip title="只读监视, ctrl 点击为读写监视">
            <span class="span-blue pointer" @click="openTerminalWatcher($event, record)">监视</span>
          </a-tooltip>
          <a-divider type="vertical"/>
          <a @click="forceOffline(record.token)">下线</a>
        </template>
      </a-table>
    </div>
    <!-- 事件 -->
    <TerminalWatcherModal ref="watcher"/>
  </div>
</template>

<script>
import { defineArrayKey } from '@/lib/utils'
import { formatDate } from '@/lib/filters'
import MachineAutoComplete from '@/components/machine/MachineAutoComplete'
import UserAutoComplete from '@/components/user/UserAutoComplete'
import TerminalWatcherModal from '@/components/terminal/TerminalWatcherModal'

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
    title: '机器名称',
    dataIndex: 'machineName',
    key: 'machineName',
    width: 180,
    ellipsis: true,
    sorter: (a, b) => a.machineName.localeCompare(b.machineName)
  },
  {
    title: '连接主机',
    dataIndex: 'machineHost',
    key: 'machineHost',
    width: 180,
    ellipsis: true,
    sorter: (a, b) => a.machineHost.localeCompare(b.machineHost)
  },
  {
    title: '连接用户',
    dataIndex: 'userName',
    key: 'userName',
    width: 120,
    ellipsis: true,
    sorter: (a, b) => a.userName.localeCompare(b.userName)
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
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 130,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'MachineTerminalSession',
  components: {
    TerminalWatcherModal,
    MachineAutoComplete,
    UserAutoComplete
  },
  data: function() {
    return {
      query: {
        userId: undefined,
        username: undefined,
        machineId: undefined,
        machineName: undefined
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
      columns
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.terminalSessionList({
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
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.machineSelector.reset()
      this.$refs.userSelector.reset()
      this.query.machineId = undefined
      this.query.machineName = undefined
      this.query.userId = undefined
      this.query.username = undefined
      this.getList({})
    },
    forceOffline(token) {
      this.$api.terminalOffline({ token })
      .then(() => {
        this.getList()
        this.$message.success('操作成功')
      })
    },
    openTerminalWatcher(e, record) {
      const readonly = e.ctrlKey ? 2 : 1
      this.$api.getTerminalWatcherToken({
        token: record.token,
        readonly
      }).then(({ data }) => {
        this.$refs.watcher.open({
          ...data,
          machineName: record.machineName,
          machineHost: record.machineHost,
          username: record.userName
        })
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
