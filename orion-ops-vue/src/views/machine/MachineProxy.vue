<template>
  <div class="machine-proxy-container">
    <!-- 搜索列 -->
    <div class="table-search-columns">
      <a-form-model class="machine-proxy-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="代理主机" prop="host">
              <a-input v-model="query.host" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="代理端口" prop="port">
              <a-input v-limit-integer v-model="query.port" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="代理用户" prop="username">
              <a-input v-model="query.username" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="描述" prop="description">
              <a-input v-model="query.description" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <a-form-model-item label="代理类型" prop="type">
              <a-select v-model="query.type" placeholder="全部" @change="getList({})" allowClear>
                <a-select-option :value="type.value" v-for="type in MACHINE_PROXY_TYPE" :key="type.value">
                  {{ type.label }}
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
        <span class="table-title">代理列表</span>
        <a-divider v-show="selectedRowKeys.length" type="vertical"/>
        <a-popconfirm v-show="selectedRowKeys.length"
                      placement="topRight"
                      title="是否删除选中代理?"
                      ok-text="确定"
                      cancel-text="取消"
                      @confirm="remove(selectedRowKeys)">
          <a-button class="ml8" type="danger" icon="delete">删除</a-button>
        </a-popconfirm>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a-button class="ml16 mr8" type="primary" icon="plus" @click="add">新建</a-button>
        <a-divider type="vertical"/>
        <a-icon type="export" class="tools-icon" title="导出数据" @click="openExport"/>
        <a-icon type="import" class="tools-icon" title="导入数据" @click="openImport"/>
        <a-icon type="search" class="tools-icon" title="查询" @click="getList({})"/>
        <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
      </div>
    </div>
    <!-- 表格 -->
    <div class="table-main-container table-scroll-x-auto">
      <a-table :columns="columns"
               :dataSource="rows"
               :pagination="pagination"
               :rowSelection="{selectedRowKeys, onChange: e => selectedRowKeys = e}"
               rowKey="id"
               @change="getList"
               :scroll="{x: '100%'}"
               :loading="loading"
               size="middle">
        <!-- 主机 -->
        <template v-slot:host="record">
          <span>{{ record.host }}</span>
          <a class="copy-icon-right" title="复制" @click="$copy(record.host)">
            <a-icon type="copy"/>
          </a>
        </template>
        <!-- 类型 -->
        <template v-slot:type="record">
          <a-tag :color="record.type | formatProxyType('color')">
            {{ record.type | formatProxyType('label') }}
          </a-tag>
        </template>
        <!-- 操作 -->
        <template v-slot:action="record">
          <!-- 修改 -->
          <a @click="update(record.id)">修改</a>
          <a-divider type="vertical"/>
          <!-- 删除 -->
          <a-popconfirm title="确认删除当前代理?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="remove([record.id])">
            <span class="span-blue pointer">删除</span>
          </a-popconfirm>
        </template>
      </a-table>
    </div>
    <!-- 事件 -->
    <div class="machine-proxy-event">
      <!-- 添加模态框 -->
      <AddMachineProxyModal ref="addModal" @added="getList({})" @updated="getList({})"/>
      <!-- 导出模态框 -->
      <MachineProxyExportModal ref="export"/>
      <!-- 导入模态框 -->
      <DataImportModal ref="import" :importType="importType"/>
    </div>
  </div>
</template>

<script>
import { enumValueOf, IMPORT_TYPE, MACHINE_PROXY_TYPE } from '@/lib/enum'
import AddMachineProxyModal from '@/components/machine/AddMachineProxyModal'
import MachineProxyExportModal from '@/components/export/MachineProxyExportModal'
import DataImportModal from '@/components/import/DataImportModal'

/**
 * 列
 */
const columns = [
  {
    title: '代理主机',
    key: 'host',
    width: 200,
    sorter: (a, b) => a.host.localeCompare(b.host),
    scopedSlots: { customRender: 'host' }
  },
  {
    title: '代理端口',
    dataIndex: 'port',
    key: 'port',
    width: 150,
    sorter: (a, b) => a.port > b.port
  },
  {
    title: '代理用户',
    dataIndex: 'username',
    key: 'username',
    width: 200
  },
  {
    title: '代理类型',
    key: 'type',
    width: 150,
    scopedSlots: { customRender: 'type' }
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    width: 200,
    ellipsis: true
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 120,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'MachineProxy',
  components: {
    DataImportModal,
    MachineProxyExportModal,
    AddMachineProxyModal
  },
  data: function() {
    return {
      MACHINE_PROXY_TYPE,
      query: {
        host: undefined,
        port: undefined,
        username: undefined,
        description: undefined,
        type: undefined
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
      importType: IMPORT_TYPE.MACHINE_PROXY,
      columns
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getMachineProxyList({
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
        this.selectedRowKeys = []
      }).catch(() => {
        this.loading = false
      })
    },
    remove(idList) {
      this.$api.deleteMachineProxy({
        idList
      }).then(() => {
        this.$message.success('删除成功')
        this.getList({})
      })
    },
    add() {
      this.$refs.addModal.add()
    },
    update(id) {
      this.$refs.addModal.update(id)
    },
    openExport() {
      this.$refs.export.open()
    },
    openImport() {
      this.$refs.import.open()
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.getList({})
    }
  },
  filters: {
    formatProxyType(type, f) {
      return enumValueOf(MACHINE_PROXY_TYPE, type)[f]
    }
  },
  mounted() {
    this.getList({})
  }
}
</script>

<style scoped>

</style>
