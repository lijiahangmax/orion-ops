<template>
  <div class="monitor-container">
    <!-- 筛选列 -->
    <div class="table-search-columns">
      <a-form-model ref="query" :model="query">
        <a-row>
          <a-col :span="6">
            <a-form-model-item label="主机" prop="machine">
              <MachineAutoComplete ref="machineSelector" @change="selectedMachine" @choose="getList({})"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="6">
            <a-form-model-item label="状态" prop="status">
              <a-select v-model="query.status" placeholder="全部" @change="getList({})" allowClear>
                <a-select-option :value="status.value" v-for="status in MONITOR_STATUS" :key="status.value">
                  {{ status.label }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 表格 -->
    <div class="table-wrapper">
      <!-- 工具栏 -->
      <div class="table-tools-bar">
        <!-- 左侧 -->
        <div class="tools-fixed-left">
          <span class="table-title">机器监控</span>
        </div>
        <!-- 右侧 -->
        <div class="tools-fixed-right">
          <a-popconfirm title="确定执行批量安装本页监控插件?"
                        okText="确定"
                        cancelText="取消"
                        @confirm="batchInstallMonitor">
            <a-button type="primary" icon="cloud-server">安装</a-button>
          </a-popconfirm>
          <a-popconfirm title="确定执行批量升级本页监控插件?"
                        okText="确定"
                        cancelText="取消"
                        @confirm="batchUpgradeMonitor">
            <a-button class="mx16" type="primary" icon="cloud-upload">升级</a-button>
          </a-popconfirm>
          <a-popconfirm title="确定执行批量检测本页监控插件状态?"
                        okText="确定"
                        cancelText="取消"
                        @confirm="batchCheckMonitor">
            <a-button type="primary" icon="cloud-sync">检测</a-button>
          </a-popconfirm>
          <a-divider class="ml16" type="vertical"/>
          <a-icon type="search" class="tools-icon" title="查询" @click="getList({})"/>
          <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
        </div>
      </div>
      <!-- 表格 -->
      <div class="table-main-container table-scroll-x-auto">
        <a-table :columns="columns"
                 :dataSource="rows"
                 :pagination="pagination"
                 rowKey="machineId"
                 @change="getList"
                 :scroll="{x: '100%'}"
                 :loading="loading"
                 size="middle">
          <!-- version -->
          <template #machineHost="record">
            <a-tooltip title="点击复制">
            <span class="span-blue pointer" @click="$copy(record.machineHost)">
            {{ record.machineHost }}
          </span>
            </a-tooltip>
          </template>
          <!-- 状态 -->
          <template #status="record">
            <a-badge :status="record.status | formatStatus('status')"
                     :text="record.status | formatStatus('label')"/>
          </template>
          <!-- version -->
          <template #version="record">
            <!-- 当前版本 -->
            <span v-if="record.currentVersion" class="span-blue">
            V{{ record.currentVersion }}
          </span>
            <span v-else>-</span>
            <!-- 升级 -->
            <a-tooltip v-if="MONITOR_STATUS.STARTING.value !== record.status &&
            record.currentVersion && record.latestVersion && record.currentVersion !== record.latestVersion">
              <template #title>
                最新版本: V{{ record.latestVersion }} 点击进行升级
              </template>
              <a-tag class="ml4 pointer usn" @click="upgradeMonitor(record)">
                升级
              </a-tag>
            </a-tooltip>
          </template>
          <!-- 操作 -->
          <template #action="record">
            <!-- 安装 -->
            <a-button class="p0"
                      v-if="MONITOR_STATUS.NOT_START.value === record.status"
                      type="link"
                      style="height: 22px"
                      @click="installMonitor(record)">
              安装
            </a-button>
            <!-- 监控 -->
            <a-button class="p0"
                      v-else
                      type="link"
                      style="height: 22px"
                      :disabled="MONITOR_STATUS.RUNNING.value !== record.status">
              <a :href="`#/machine/monitor/metrics/${record.machineId}`">监控</a>
            </a-button>
            <a-divider type="vertical"/>
            <!-- 检测 -->
            <a-button class="p0" type="link" style="height: 22px">
              <a @click="checkMonitor([record])">检测</a>
            </a-button>
            <a-divider type="vertical"/>
            <a @click="openAgentSetting(record)">插件配置</a>
            <a-divider type="vertical"/>
            <a :href="`#/machine/monitor/metrics/${record.machineId}?tab=3`">报警配置</a>
            <a-divider type="vertical"/>
            <a :href="`#/machine/monitor/metrics/${record.machineId}?tab=4`">报警记录</a>
          </template>
        </a-table>
      </div>
    </div>
    <!-- 配置模态框 -->
    <MachineMonitorConfigModal ref="configModal"/>
  </div>
</template>

<script>
import { formatDate } from '@/lib/filters'
import { enumValueOf, MONITOR_STATUS } from '@/lib/enum'
import MachineAutoComplete from '@/components/machine/MachineAutoComplete'
import MachineMonitorConfigModal from '@/components/machine/MachineMonitorConfigModal'

/**
 * 列
 */
const columns = [
  {
    title: '机器名称',
    key: 'machineName',
    dataIndex: 'machineName',
    ellipsis: true
  },
  {
    title: '机器主机',
    key: 'machineHost',
    ellipsis: true,
    scopedSlots: { customRender: 'machineHost' }
  },
  {
    title: '插件版本',
    key: 'version',
    width: 120,
    scopedSlots: { customRender: 'version' }
  },
  {
    title: '安装状态',
    key: 'status',
    align: 'center',
    width: 120,
    scopedSlots: { customRender: 'status' }
  },
  // {
  //   title: '报警',
  //   key: 'alarm',
  //   width: 150,
  //   scopedSlots: { customRender: 'alarm' }
  // },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    ellipsis: true,
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'MachineMonitorList',
  components: {
    MachineMonitorConfigModal,
    MachineAutoComplete
  },
  data: function() {
    return {
      MONITOR_STATUS,
      query: {
        machineId: undefined,
        machineName: undefined,
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
      columns
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getMachineMonitorList({
        ...this.query,
        page: page.current,
        limit: page.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
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
    installMonitor(record) {
      record.status = MONITOR_STATUS.STARTING.value
      const loading = this.$message.loading('正在检测...')
      this.$api.installMachineMonitorAgent({
        machineId: record.machineId
      }).then(({ data }) => {
        record.status = data.status
        record.currentVersion = data.currentVersion || record.currentVersion
        loading()
        if (MONITOR_STATUS.STARTING.value === data.status) {
          this.$message.success('插件已开始安装...')
        } else if (MONITOR_STATUS.RUNNING.value === data.status) {
          this.$message.success('机器监控插件已启动')
        }
      }).catch(() => {
        loading()
      })
    },
    upgradeMonitor(record) {
      record.status = MONITOR_STATUS.STARTING.value
      const loading = this.$message.loading('正在检测...')
      this.$api.installMachineMonitorAgent({
        machineId: record.machineId,
        upgrade: true
      }).then(({ data }) => {
        loading()
        this.$message.success('插件已开始升级...')
      }).catch(() => {
        loading()
      })
    },
    async batchInstallMonitor() {
      const rows = this.rows.filter(row => row.status === MONITOR_STATUS.NOT_START.value)
      if (!rows.length) {
        this.$message.warning('本页面没有可安装的机器')
      }
      for (const row of rows) {
        this.$emit('openLoading', `${row.machineName}(${row.machineHost}) 正在安装...`)
        const beforeStatus = row.status
        row.status = MONITOR_STATUS.STARTING.value
        try {
          const { data } = await this.$api.installMachineMonitorAgent({
            machineId: row.machineId
          })
          row.status = data.status
          row.currentVersion = data.currentVersion || row.currentVersion
        } catch {
          row.status = beforeStatus
        }
      }
      this.$emit('closeLoading')
    },
    async batchUpgradeMonitor() {
      const rows = this.rows.filter(row => row.currentVersion && row.latestVersion && row.currentVersion !== row.latestVersion)
      if (!rows.length) {
        this.$message.warning('本页面没有可升级的机器')
      }
      for (const row of rows) {
        this.$emit('openLoading', `${row.machineName}(${row.machineHost}) 正在升级...`)
        const beforeStatus = row.status
        row.status = MONITOR_STATUS.STARTING.value
        try {
          const { data } = await this.$api.installMachineMonitorAgent({
            machineId: row.machineId,
            upgrade: true
          })
        } catch {
          row.status = beforeStatus
        }
      }
      this.$emit('closeLoading')
    },
    batchCheckMonitor() {
      const rows = this.rows.filter(row => row.status !== MONITOR_STATUS.STARTING.value)
      if (!rows.length) {
        this.$message.warning('本页面没有可检查的机器')
        return
      }
      this.checkMonitor(rows)
    },
    async checkMonitor(rows) {
      for (const row of rows) {
        this.$emit('openLoading', `${row.machineName}(${row.machineHost}) 正在检测...`)
        const beforeStatus = row.status
        try {
          const { data } = await this.$api.checkMachineMonitorAgentStatus({
            machineId: row.machineId
          })
          row.status = data.status
          row.currentVersion = data.currentVersion || row.currentVersion
        } catch {
          row.status = beforeStatus
        }
      }
      this.$emit('closeLoading')
    },
    openAgentSetting(record) {
      this.$refs.configModal.open(record)
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.machineSelector.reset()
      this.query.machineId = undefined
      this.query.machineName = undefined
      this.query.status = undefined
      this.getList({})
    }
  },
  filters: {
    formatDate,
    formatStatus(status, f) {
      return enumValueOf(MONITOR_STATUS, status)[f]
    }
  },
  mounted() {
    this.query.machineId = this.$route.query.machineId
    // 查询列表
    this.getList({})
  }
}
</script>

<style lang="less" scoped>

</style>
