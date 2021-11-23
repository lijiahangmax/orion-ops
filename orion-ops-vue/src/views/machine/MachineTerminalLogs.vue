<template>
  <div id="machine-terminal-logs-container">
    <!-- 条件列 -->
    <div class="table-search-columns">
      <a-form-model class="machine-log-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="主机" prop="host">
              <MachineSelector ref="machineSelector" @change="machineId => query.machineId = machineId"/>
            </a-form-model-item>
          </a-col>
          <a-col v-if="$isAdmin()" :span="5">
            <a-form-model-item label="用户" prop="user">
              <UserSelector ref="userSelector" @change="userId => query.userId = userId"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <a-form-model-item label="结束码" prop="closeCode">
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
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
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
               :scroll="{x: '100%'}"
               :loading="loading"
               size="middle">
        <!-- 主机 -->
        <div slot="machine" slot-scope="record">
          <a-tooltip placement="top">
            <template slot="title">
              <span>{{ `${record.machineName} (${record.machineHost})` }}</span>
            </template>
            <span>{{ record.machineName }}</span>
          </a-tooltip>
        </div>
        <!-- 连接时间 -->
        <span slot="connectedTime" slot-scope="record">
          {{
            record.connectedTime | formatDate({
              date: record.connectedTime,
              pattern: 'yyyy-MM-dd HH:mm:ss'
            })
          }}
          ({{ record.connectedTimeAgo }})
        </span>
        <!-- 断连时间 -->
        <span slot="disconnectedTime" slot-scope="record" v-if="record.disconnectedTime">
          {{
            record.disconnectedTime | formatDate({
              date: record.disconnectedTime,
              pattern: 'yyyy-MM-dd HH:mm:ss'
            })
          }}
          ({{ record.disconnectedTimeAgo }})
        </span>
        <!-- 操作 -->
        <div slot="action" slot-scope="record">
          <a v-if="record.downloadUrl" @click="clearDownloadUrl(record)" target="_blank" :href="record.downloadUrl">下载</a>
          <a v-else @click="loadDownloadUrl(record)">获取操作日志</a>
        </div>
      </a-table>
    </div>
  </div>
</template>

<script>

import _utils from '@/lib/utils'
import MachineSelector from '@/components/machine/MachineSelector'
import UserSelector from '@/components/user/UserSelector'

/**
 * 列
 */
const columns = [
  {
    title: '序号',
    key: 'seq',
    customRender: (text, record, index) => `${index + 1}`,
    width: 65,
    align: 'center'
  },
  {
    title: '连接机器',
    key: 'machine',
    width: 180,
    ellipsis: true,
    scopedSlots: { customRender: 'machine' },
    sorter: (a, b) => a.machineName.localeCompare(b.machineName)
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
    sorter: (a, b) => a.connectedTime - b.connectedTime,
    scopedSlots: { customRender: 'connectedTime' }
  },
  {
    title: '断连时间',
    key: 'disconnectedTime',
    width: 180,
    ellipsis: true,
    sorter: (a, b) => a.disconnectedTime - b.disconnectedTime,
    scopedSlots: { customRender: 'disconnectedTime' }
  },
  {
    title: '操作',
    key: 'action',
    width: 80,
    scopedSlots: { customRender: 'action' },
    align: 'center'
  }
]

export default {
  name: 'MachineTerminalLogs',
  components: {
    MachineSelector,
    UserSelector
  },
  data: function() {
    return {
      query: {
        userId: undefined,
        machineId: undefined,
        closeCode: null
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
      this.$api.terminalLogList({
        ...this.query,
        page: page.current,
        limit: page.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
        this.$utils.defineArrayKey(data.rows, 'downloadUrl')
        this.rows = data.rows
        this.pagination = pagination
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.machineSelector.reset()
      this.$refs.userSelector.reset()
      this.query.machineId = undefined
      this.query.userId = undefined
      this.getList({})
    },
    async loadDownloadUrl(record) {
      try {
        const downloadUrl = await this.$api.getFileDownloadToken({
          type: this.$enum.FILE_DOWNLOAD_TYPE.TERMINAL_LOG.value,
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
    formatDate(origin, {
      date,
      pattern
    }) {
      return _utils.dateFormat(new Date(date), pattern)
    }
  },
  mounted() {
    this.getList({})
  }
}
</script>

<style scoped>

</style>
