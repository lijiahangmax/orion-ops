<template>
  <div class="app-vcs-container">
    <!-- 搜索列 -->
    <div class="table-search-columns">
      <a-form-model class="app-vcs-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="名称" prop="name">
              <a-input v-model="query.name"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="url" prop="url">
              <a-input v-model="query.url"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="资源用户" prop="username">
              <a-input v-model="query.username"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="描述" prop="description">
              <a-input v-model="query.description"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <a-form-model-item label="状态" prop="status">
              <a-select v-model="query.status" placeholder="全部" allowClear>
                <a-select-option :value="type.value" v-for="type in $enum.VCS_STATUS" :key="type.value">
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
        <span class="table-title">仓库列表</span>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a-button class="ml16 mr8" type="primary" icon="plus" @click="add">新建</a-button>
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
        <!-- url -->
        <span slot="url" slot-scope="record" class="span-blue pointer" @click="$copy(record.url)">
          {{ record.url }}
        </span>
        <!-- 状态 -->
        <a-tag slot="status" slot-scope="record" :color="$enum.valueOf($enum.VCS_STATUS, record.status).color">
          {{ $enum.valueOf($enum.VCS_STATUS, record.status).label }}
        </a-tag>
        <!-- 操作 -->
        <div slot="action" slot-scope="record">
          <!-- 初始化 -->
          <a v-if="$enum.VCS_STATUS.OK.value !== record.status" @click="init(record)">初始化</a>
          <a-divider v-if="$enum.VCS_STATUS.OK.value !== record.status" type="vertical"/>
          <!-- 修改 -->
          <a @click="update(record.id)">修改</a>
          <a-divider type="vertical"/>
          <!-- 删除 -->
          <a-popconfirm title="确认删除当前仓库?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="remove(record.id)">
            <span class="span-blue pointer">删除</span>
          </a-popconfirm>
        </div>
      </a-table>
    </div>
    <!-- 事件 -->
    <div class="app-vcs-event">
      <AddAppVcsModal ref="addModal" @added="getList({})" @updated="getList({})"/>
    </div>
  </div>
</template>

<script>
import AddAppVcsModal from '@/components/app/AddAppVcsModal'

/**
 * 列
 */
const columns = [
  {
    title: '序号',
    key: 'seq',
    customRender: (text, record, index) => `${index + 1}`,
    width: 60,
    align: 'center'
  },
  {
    title: '名称',
    key: 'name',
    dataIndex: 'name',
    ellipsis: true,
    width: 160,
    sorter: (a, b) => a.name.localeCompare(b.name)
  },
  {
    title: 'url',
    key: 'url',
    width: 300,
    ellipsis: true,
    scopedSlots: { customRender: 'url' }
  },
  {
    title: '资源用户',
    dataIndex: 'username',
    key: 'username',
    ellipsis: true,
    width: 180
  },
  {
    title: '状态',
    key: 'status',
    width: 110,
    scopedSlots: { customRender: 'status' }
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    width: 150,
    ellipsis: true
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 170,
    scopedSlots: { customRender: 'action' },
    align: 'center'
  }
]

export default {
  name: 'AppVcs',
  components: {
    AddAppVcsModal
  },
  data: function() {
    return {
      query: {
        name: null,
        url: null,
        username: null,
        description: null,
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
      this.$api.getVcsList({
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
    remove(id) {
      this.$api.deleteVcs({
        id
      }).then(() => {
        this.$message.success('删除成功')
        this.getList({})
      })
    },
    init(record) {
      this.$message.success('已提交初始化请求')
      record.status = this.$enum.VCS_STATUS.INITIALIZING.value
      this.$api.initVcs({
        id: record.id
      }).then(() => {
        this.$message.success('初始化成功')
        this.getList({})
      }).catch(() => {
        this.getList({})
      })
    },
    add() {
      this.$refs.addModal.add()
    },
    update(id) {
      this.$refs.addModal.update(id)
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.getList({})
    }
  },
  mounted() {
    this.getList({})
  }
}
</script>

<style scoped>

</style>
