<template>
  <div class="webhook-container">
    <!-- 搜索列 -->
    <div class="table-search-columns">
      <a-form-model class="webhook-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="名称" prop="name">
              <a-input v-model="query.name" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="url" prop="url">
              <a-input v-model="query.url" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="类型" prop="type">
              <a-select v-model="query.type" placeholder="请选择" @change="getList({})" allowClear>
                <a-select-option v-for="type of WEBHOOK_TYPE" :key="type.value" :value="type.value">
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
        <span class="table-title">webhook 列表</span>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a-button class="ml16 mr8" type="primary" icon="plus" @click="add">添加</a-button>
        <a-divider type="vertical"/>
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
        <!-- 类型 -->
        <template #type="record">
          {{ record.type | formatType('label') }}
        </template>
        <!-- url -->
        <template #url="record">
          <a @click="$copy(record.url)" title="复制">
            <a-icon type="copy"/>
          </a>
          <span :title="record.url">
            {{ record.url }}
          </span>
        </template>
        <!-- 操作 -->
        <template #action="record">
          <!-- 修改 -->
          <a @click="update(record.id)">修改</a>
          <a-divider type="vertical"/>
          <!-- 删除 -->
          <a-popconfirm title="确认删除当前行?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="remove(record.id)">
            <span class="span-blue pointer">删除</span>
          </a-popconfirm>
        </template>
      </a-table>
    </div>
    <!-- 新建模态框 -->
    <AddWebhookModal ref="addModal" :mask="true" @added="getList({})" @updated="getList({})"/>
  </div>
</template>

<script>
import { enumValueOf, WEBHOOK_TYPE } from '@/lib/enum'
import AddWebhookModal from '@/components/content/AddWebhookModal'

/**
 * 列
 */
const columns = [
  {
    title: '序号',
    key: 'seq',
    width: 60,
    align: 'center',
    customRender: (text, record, index) => `${index + 1}`
  },
  {
    title: '名称',
    dataIndex: 'name',
    key: 'name',
    width: 200,
    ellipsis: true
  },
  {
    title: '类型',
    key: 'type',
    width: 180,
    scopedSlots: { customRender: 'type' }
  },
  {
    title: 'webhook url',
    key: 'url',
    ellipsis: true,
    scopedSlots: { customRender: 'url' }
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 180,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'WebhookList',
  components: {
    AddWebhookModal
  },
  data() {
    return {
      WEBHOOK_TYPE,
      query: {
        name: undefined,
        url: undefined,
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
      columns
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getWebhookConfigList({
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
    add() {
      this.$refs.addModal.add()
    },
    update(id) {
      this.$refs.addModal.update(id)
    },
    remove(id) {
      this.$api.deleteWebhookConfig({
        id
      }).then(() => {
        this.$message.success('删除成功')
        this.getList({})
      })
    }
  },
  filters: {
    formatType(status, f) {
      return enumValueOf(WEBHOOK_TYPE, status)[f]
    }
  },
  mounted() {
    this.getList({})
  }
}
</script>

<style scoped>

</style>
