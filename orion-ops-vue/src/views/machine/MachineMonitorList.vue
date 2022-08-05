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
    <!-- 工具栏 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <span class="table-title">机器监控</span>
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
               rowKey="machineId"
               @change="getList"
               :scroll="{x: '100%'}"
               :loading="loading"
               size="middle">
        <!-- version -->
        <template v-slot:machineHost="record">
          <a-tooltip title="点击复制">
            <span class="span-blue pointer" @click="$copy(record.machineHost)">
            {{ record.machineHost }}
          </span>
          </a-tooltip>
        </template>
        <!-- 状态 -->
        <template v-slot:status="record">
          <a-tag :color="record.status | formatStatus('color')">
            {{ record.status | formatStatus('label') }}
          </a-tag>
        </template>
        <!-- version -->
        <template v-slot:version="record">
          <!-- 当前版本 -->
          <span v-if="record.currentVersion" class="span-blue">
            V{{ record.currentVersion }}
          </span>
          <span v-else>-</span>
          <!-- 升级 -->
          <a-tooltip v-if="record.currentVersion && record.latestVersion && record.currentVersion !== record.latestVersion">
            <template #title>
              最新版本: V{{ record.latestVersion }} 点击进行升级
            </template>
            <a-tag class="ml4 pointer usn" @click="upgradeVersion(record.machineId)">
              升级
            </a-tag>
          </a-tooltip>
        </template>
        <!-- 操作 -->
        <template v-slot:action="record">
          <!-- 监控 -->
          <a-button class="p0"
                    type="link"
                    style="height: 22px"
                    :disabled="MONITOR_STATUS.STARTED.value !== record.status">
            <a :href="`#/machine/monitor/metrics/${record.machineId}`">监控</a>
          </a-button>
          <a-divider type="vertical"/>
          <!-- 安装 -->
          <span v-if="MONITOR_STATUS.NOT_INSTALL.value === record.status"
                class="span-blue pointer"
                @click="installMonitor(record.machineId)">
            安装
          </span>
          <!-- 启动 -->
          <a-button v-if="MONITOR_STATUS.INSTALLING.value === record.status ||
                    MONITOR_STATUS.NOT_START.value === record.status"
                    type="link"
                    style="height: 22px; padding: 0 3px"
                    :disabled="MONITOR_STATUS.INSTALLING.value === record.status"
                    @click="installMonitor(record.machineId)">
            启动
          </a-button>
          <!-- 同步 -->
          <span v-if="MONITOR_STATUS.STARTED.value === record.status"
                class="span-blue pointer"
                @click="syncMonitor(record.machineId)">
            同步
          </span>
          <a-divider type="vertical"/>
          <a @click="openAgentSetting(record.machineId)">插件配置</a>
          <a-divider type="vertical"/>
          <a :href="`#/machine/monitor/metrics/${record.machineId}?tab=3`">报警配置</a>
          <a-divider type="vertical"/>
          <a :href="`#/machine/monitor/metrics/${record.machineId}?tab=4`">报警历史</a>
        </template>
      </a-table>
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
  {
    title: '报警',
    key: 'alarm',
    width: 150,
    scopedSlots: { customRender: 'alarm' }
  },
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
    toMonitor(machineId) {
      this.$message.success(machineId)
    },
    installMonitor(machineId) {
      this.$message.success(machineId)
    },
    upgradeVersion(machineId) {
      this.$message.success(machineId)
    },
    syncMonitor(machineId) {
      this.$message.success(machineId)
    },
    openAgentSetting(machineId) {
      this.$refs.configModal.open(machineId)
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.machineSelector.reset()
      this.$refs.userSelector && this.$refs.userSelector.reset()
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
    // 查询列表
    this.getList({})
  }
}
</script>

<style lang="less" scoped>

</style>
