<template>
  <div class="command-template-container">
    <!-- 搜索列 -->
    <div class="table-search-columns">
      <a-form-model class="command-template-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="模板名称" prop="name">
              <a-input v-model="query.name" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="模板内容" prop="value">
              <a-input v-model="query.value" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="模板描述" prop="description">
              <a-input v-model="query.description" allowClear/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 工具栏 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <span class="table-title">模板列表</span>
        <a-divider v-show="selectedRowKeys.length" type="vertical"/>
        <a-popconfirm v-show="selectedRowKeys.length"
                      placement="topRight"
                      title="是否删除选中模板?"
                      ok-text="确定"
                      cancel-text="取消"
                      @confirm="remove(selectedRowKeys)">
          <a-button class="ml8" type="danger" icon="delete">删除</a-button>
        </a-popconfirm>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a-button class="ml16 mr8" type="primary" icon="plus" @click="add">添加</a-button>
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
        <!-- 模板内容 -->
        <template v-slot:value="record">
          <span class="pointer" title="预览" @click="preview(record.value)">
            {{ record.value }}
          </span>
        </template>
        <!-- 修改时间 -->
        <template v-slot:updateTime="record">
          {{ record.updateTime | formatDate }}
        </template>
        <!-- 操作 -->
        <template v-slot:action="record">
          <!-- 修改 -->
          <a @click="update(record.id)">修改</a>
          <a-divider type="vertical"/>
          <!-- 删除 -->
          <a-popconfirm title="确认删除当前模板?"
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
    <div class="command-template-event">
      <!-- 新建模态框 -->
      <AddTemplateModal ref="addModal" @added="getList({})" @updated="getList({})"/>
      <!-- 预览 -->
      <EditorPreview ref="preview"/>
      <!-- 导出模态框 -->
      <CommandTemplateExportModal ref="export"/>
      <!-- 导入模态框 -->
      <DataImportModal ref="import" :importType="$enum.IMPORT_TYPE.COMMAND_TEMPLATE"/>
    </div>
  </div>
</template>

<script>
import { formatDate } from '@/lib/filters'
import AddTemplateModal from '@/components/template/AddTemplateModal'
import EditorPreview from '@/components/preview/EditorPreview'
import CommandTemplateExportModal from '@/components/export/CommandTemplateExportModal'
import DataImportModal from '@/components/import/DataImportModal'

/**
 * 列
 */
const columns = [
  {
    title: '模板名称',
    dataIndex: 'name',
    key: 'name',
    width: 120,
    ellipsis: true,
    sorter: (a, b) => a.name.localeCompare(b.name)
  },
  {
    title: '模板内容',
    key: 'value',
    width: 450,
    ellipsis: true,
    scopedSlots: { customRender: 'value' },
    sorter: (a, b) => a.value.localeCompare(b.value)
  },
  {
    title: '模板描述',
    dataIndex: 'description',
    key: 'description',
    width: 140,
    ellipsis: true
  },
  {
    title: '创建人',
    dataIndex: 'createUserName',
    key: 'createUserName',
    width: 100,
    ellipsis: true,
    sorter: (a, b) => a.createUserName.localeCompare(b.createUserName)
  },
  {
    title: '修改人',
    dataIndex: 'updateUserName',
    key: 'updateUserName',
    width: 100,
    ellipsis: true,
    sorter: (a, b) => a.updateUserName.localeCompare(b.updateUserName)
  },
  {
    title: '修改时间',
    key: 'updateTime',
    width: 150,
    align: 'center',
    sorter: (a, b) => a.updateTime - b.updateTime,
    scopedSlots: { customRender: 'updateTime' }
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
  name: 'TemplateList',
  components: {
    DataImportModal,
    CommandTemplateExportModal,
    AddTemplateModal,
    EditorPreview
  },
  data() {
    return {
      query: {
        name: null,
        value: null,
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
      selectedRowKeys: [],
      columns
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getTemplateList({
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
    openExport() {
      this.$refs.export.open()
    },
    openImport() {
      this.$refs.import.open()
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.getList({})
    },
    add() {
      this.$refs.addModal.add()
    },
    update(id) {
      this.$refs.addModal.update(id)
    },
    remove(idList) {
      this.$api.deleteTemplate({
        idList
      }).then(() => {
        this.$message.success('删除成功')
        this.getList({})
      })
    },
    preview(value) {
      this.$refs.preview.preview(value)
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
