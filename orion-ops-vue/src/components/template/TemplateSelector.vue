<template>
  <a-modal v-model="visible"
           v-drag-modal
           :width="1000"
           :dialogStyle="{top: '16px'}"
           :bodyStyle="{padding: '8px'}"
           :destroyOnClose="true"
           title="模板列表">
    <!-- 搜索列 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <a-form-model layout="inline" class="command-template-search-form" ref="query" :model="query">
          <a-form-model-item label="模板名称" prop="name">
            <a-input v-model="query.name" allowClear/>
          </a-form-model-item>
          <a-form-model-item label="模板内容" prop="value">
            <a-input v-model="query.value" allowClear/>
          </a-form-model-item>
        </a-form-model>
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
               rowKey="id"
               @change="getList"
               :scroll="{x: '100%'}"
               :loading="loading"
               size="middle">
        <!-- 模板内容 -->
        <template v-slot:value="record">
          <span :title="record.value">
            <a-icon class="span-blue pointer" type="copy" title="复制" @click="$copy(record.value)"/>
            {{ record.value }}
          </span>
        </template>
        <!-- 操作 -->
        <template v-slot:action="record">
          <!-- 选择 -->
          <a @click="selected(record.value)">选择</a>
        </template>
      </a-table>
    </div>
    <!-- 页脚 -->
    <template #footer>
      <a-button @click="() => visible = false">关闭</a-button>
    </template>
  </a-modal>
</template>

<script>

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
    width: 240,
    ellipsis: true,
    sorter: (a, b) => a.value.localeCompare(b.value),
    scopedSlots: { customRender: 'value' }
  },
  {
    title: '模板描述',
    key: 'description',
    width: 140,
    ellipsis: true,
    dataIndex: 'description'
  },
  {
    title: '操作',
    key: 'action',
    width: 60,
    fixed: 'right',
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'TemplateSelector',
  data() {
    return {
      visible: false,
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
      columns
    }
  },
  methods: {
    open() {
      this.visible = true
      if (!this.rows.length) {
        this.getList({})
      }
    },
    close() {
      this.visible = false
    },
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
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.getList({})
    },
    selected(value) {
      this.$emit('selected', value)
    }
  }
}
</script>

<style scoped>

</style>
