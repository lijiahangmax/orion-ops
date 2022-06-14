<template>
  <div class="log-list-container">
    <!-- 筛选列 -->
    <div class="table-search-columns">
      <a-form-model class="log-list-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="机器" prop="machine">
              <MachineSelector ref="machineSelector" @change="machineId => query.machineId = machineId"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="名称" prop="name">
              <a-input v-model="query.name" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="路径" prop="path">
              <a-input v-model="query.path" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="命令" prop="command">
              <a-input v-model="query.command" allowClear/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 工具 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <span class="table-title">日志列表</span>
        <a-divider v-show="selectedRowKeys.length" type="vertical"/>
        <div v-show="selectedRowKeys.length">
          <a-popconfirm title="确认删除所选中的执行记录吗?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="remove(selectedRowKeys)">
            <a-button class="ml8" type="danger" icon="delete">删除</a-button>
          </a-popconfirm>
        </div>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a-button class="mr8" icon="filter" @click="cleanAnsi">清除 ANSI</a-button>
        <a target="_blank" href="#/log/view">
          <a-button class="mr8" type="primary" icon="file-text">日志面板</a-button>
        </a>
        <a-button class="mr8" type="primary" icon="upload" @click="upload">上传</a-button>
        <a-button class="mr8" type="primary" icon="plus" @click="add">添加</a-button>
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
        <template v-slot:machine="record">
          <a-tooltip placement="top">
            <template #title>
              <span>{{ `${record.machineName} (${record.machineHost})` }}</span>
            </template>
            <span>{{ record.machineName }}</span>
          </a-tooltip>
        </template>
        <!-- 路径 -->
        <template v-slot:path="record">
           <span class="pointer" title="预览" @click="previewText(record.path)">
             {{ record.path }}
           </span>
        </template>
        <!-- 命令 -->
        <template v-slot:command="record">
          <span class="pointer" title="预览" @click="previewText(record.command)">
            {{ record.command }}
          </span>
        </template>
        <!-- 修改时间 -->
        <template v-slot:updateTime="record">
          {{ record.updateTime | formatDate }}
        </template>
        <!-- 操作 -->
        <template v-slot:action="record">
          <!-- 打开 -->
          <a-tooltip title="ctrl 点击打开新页面" v-if="record.machineStatus === ENABLE_STATUS.ENABLE.value">
            <a target="_blank"
               :href="`#/log/view/${record.id}`"
               @click="openLogView($event, record.id)">
              <a-button class="open-log-trigger" type="link">
                打开
              </a-button>
            </a>
          </a-tooltip>
          <a-tooltip title="机器未启用" v-else>
            <a-button class="open-log-trigger"
                      type="link"
                      :disabled="true">
              打开
            </a-button>
          </a-tooltip>
          <a-divider type="vertical"/>
          <!-- 修改 -->
          <a @click="update(record.id)">修改</a>
          <a-divider type="vertical"/>
          <!-- 删除 -->
          <a-popconfirm title="是否要删除当前日志记录?"
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
    <div class="log-list-event">
      <!-- 文本预览 -->
      <TextPreview ref="previewText"/>
      <!-- 添加模态框 -->
      <AddLogFileModal ref="addModal" @added="getList({})" @updated="getList({})"/>
      <!-- 上传模态框 -->
      <UploadLogFileModal ref="uploadModal" @uploaded="getList({})"/>
      <!-- 清除ANSI模态框 -->
      <FileAnsiCleanModal ref="cleanModal"/>
      <!-- 日志模态框 -->
      <LoggerViewModal ref="logView"/>
      <!-- 导出模态框 -->
      <MachineTailFileExportModal ref="export"/>
      <!-- 导入模态框 -->
      <DataImportModal ref="import" :importType="importType"/>
    </div>
  </div>
</template>

<script>
import { formatDate } from '@/lib/filters'
import { ENABLE_STATUS, IMPORT_TYPE } from '@/lib/enum'
import MachineSelector from '@/components/machine/MachineSelector'
import AddLogFileModal from '@/components/log/AddLogFileModal'
import TextPreview from '@/components/preview/TextPreview'
import LoggerViewModal from '@/components/log/LoggerViewModal'
import MachineTailFileExportModal from '@/components/export/MachineTailFileExportModal'
import DataImportModal from '@/components/import/DataImportModal'
import UploadLogFileModal from '@/components/log/UploadLogFileModal'
import FileAnsiCleanModal from '@/components/log/FileAnsiCleanModal'

/**
 * 列
 */
const columns = [
  {
    title: '机器',
    key: 'machine',
    ellipsis: true,
    sorter: (a, b) => a.machineName.localeCompare(b.machineName),
    scopedSlots: { customRender: 'machine' }
  },
  {
    title: '名称',
    key: 'name',
    dataIndex: 'name',
    ellipsis: true,
    sorter: (a, b) => a.name.localeCompare(b.name)
  },
  {
    title: '路径',
    key: 'path',
    ellipsis: true,
    sorter: (a, b) => a.path.localeCompare(b.path),
    scopedSlots: { customRender: 'path' }
  },
  {
    title: '命令',
    key: 'command',
    ellipsis: true,
    scopedSlots: { customRender: 'command' }
  },
  {
    title: '偏移量(行)',
    key: 'offset',
    dataIndex: 'offset',
    width: 110,
    sorter: (a, b) => a.offset - b.offset
  },
  {
    title: '编码',
    key: 'charset',
    dataIndex: 'charset',
    width: 100,
    align: 'center'
  },
  {
    title: '修改时间',
    key: 'updateTime',
    width: 150,
    ellipsis: true,
    align: 'center',
    sorter: (a, b) => a.updateTime - b.updateTime,
    scopedSlots: { customRender: 'updateTime' }
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 170,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'LoggerList',
  components: {
    FileAnsiCleanModal,
    UploadLogFileModal,
    DataImportModal,
    MachineTailFileExportModal,
    LoggerViewModal,
    MachineSelector,
    AddLogFileModal,
    TextPreview
  },
  data() {
    return {
      ENABLE_STATUS,
      query: {
        machineId: undefined,
        name: null,
        path: null,
        command: null
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
      selectedRowKeys: [],
      loading: false,
      columns,
      importType: IMPORT_TYPE.TAIL_FILE
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getTailList({
        ...this.query,
        page: page.current,
        limit: page.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
        this.rows = data.rows || []
        this.pagination = pagination
        this.selectedRowKeys = []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    upload() {
      this.$refs.uploadModal.open()
    },
    cleanAnsi() {
      this.$refs.cleanModal.open()
    },
    add() {
      this.$refs.addModal.add()
    },
    update(id) {
      this.$refs.addModal.update(id)
    },
    remove(idList) {
      this.$api.deleteTailFile({
        idList
      }).then(() => {
        this.$message.success('已删除')
        this.getList({})
      })
    },
    previewText(value) {
      this.$refs.previewText.preview(value)
    },
    openExport() {
      this.$refs.export.open()
    },
    openImport() {
      this.$refs.import.open()
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.machineSelector.reset()
      this.query.machineId = undefined
      this.getList({})
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

<style lang="less" scoped>

.open-log-trigger {
  height: 22px;
  padding: 0;
  margin: 0;
}

</style>
