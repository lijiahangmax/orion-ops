<template>
  <div class="exec-container">
    <!-- 筛选列 -->
    <div class="table-search-columns">
      <a-form-model ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="主机" prop="host">
              <MachineSelector ref="machineSelector" @change="machineId => query.machineId = machineId"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="命令" prop="command">
              <a-input v-model="query.command"/>
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
          <a-col :span="4">
            <a-form-model-item label="退出码" prop="exitCode">
              <a-input-number v-model="query.exitCode" :precision="0" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="描述" prop="description">
              <a-input v-model="query.description"/>
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
        <a-button class="ml16 mr8" type="primary">批量执行</a-button>
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
        <!-- 主机 -->
        <div slot="machine" slot-scope="record">
          <a-tooltip placement="top">
            <template slot="title">
              <span>{{ `${record.machineName} (${record.machineHost})` }}</span>
            </template>
            <span>{{ record.machineName }}</span>
          </a-tooltip>
        </div>
        <!-- 状态 -->
        <a-tag slot="status" slot-scope="record"
               style="margin: 0"
               :color="$enum.valueOf($enum.BATCH_EXEC_STATUS, record.status).color">
          {{ $enum.valueOf($enum.BATCH_EXEC_STATUS, record.status).label }}
        </a-tag>
        <!-- 退出码 -->
        <span slot="exitCode" slot-scope="record"
              :style="{'color': record.exitCode === 0 ? '#4263EB' : '#E03131'}">
            {{ record.exitCode }}
        </span>
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
          <!-- 详情 -->
          <a @click="detail(record.id)">详情</a>
          <a-divider type="vertical"/>
          <!-- 日志 -->
          <a @click="openLogView(record.id)">日志</a>
          <a-divider v-if="record.status === 20" type="vertical"/>
          <!-- 终止 -->
          <a-popconfirm v-if="record.status === 20"
                        title="确认停止当前任务?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="terminated(record.id)">
            <span class="span-blue pointer">停止</span>
          </a-popconfirm>
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
    title: '执行机器',
    key: 'machine',
    width: 180,
    ellipsis: true,
    scopedSlots: { customRender: 'machine' },
    sorter: (a, b) => a.machineName.localeCompare(b.machineName)
  },
  {
    title: '命令',
    dataIndex: 'command',
    key: 'command',
    ellipsis: true,
    width: 400
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    align: 'center',
    scopedSlots: { customRender: 'status' }
  },
  {
    title: '退出码',
    key: 'exitCode',
    width: 100,
    align: 'center',
    scopedSlots: { customRender: 'exitCode' }
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
    sorter: (a, b) => a.createTime.localeCompare(b.createTime),
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
    fixed: 'right',
    width: 140,
    scopedSlots: { customRender: 'action' },
    align: 'center'
  }
]

export default {
  name: 'BatchExec',
  components: {
    MachineSelector,
    UserSelector
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
          return `共 ${total}条`
        }
      },
      loading: false,
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
        this.rows = data.rows
        this.pagination = pagination
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    add() {
    },
    detail(record) {
    },
    openLogView(record) {
    },
    terminated(record) {
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.machineSelector.reset()
      this.$refs.userSelector.reset()
      this.query.machineId = undefined
      this.query.userId = undefined
      this.query.status = undefined
      this.getList({})
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

<style lang="less" scoped>

.exec-tools-container {
  margin-bottom: 12px;
}

.exec-tools-container > button {
  margin-right: 8px;
}

/deep/ .ant-row {
  margin: 0;
}

</style>
