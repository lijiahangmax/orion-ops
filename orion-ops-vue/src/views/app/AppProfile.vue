<template>
  <div class="app-profile-container">
    <!-- 筛选列 -->
    <div class="table-search-columns">
      <a-form-model class="app-profile-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="环境名称" prop="name">
              <a-input v-model="query.name" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="标签" prop="tag">
              <a-input v-model="query.tag" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="描述" prop="description">
              <a-input v-model="query.description" allowClear/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 工具 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <span class="table-title">环境列表</span>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a-button class="mr8" type="primary" icon="plus" @click="add">添加</a-button>
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
               @change="changePage"
               :scroll="{x: '100%'}"
               :loading="loading"
               size="middle">
        <!-- tag -->
        <a slot="tag" slot-scope="record">
          <a-tag color="#20C997">
            {{ record.tag }}
          </a-tag>
        </a>
        <!-- 审核 -->
        <a slot="releaseAudit" slot-scope="record">
          <a-tag v-if="record.releaseAudit" :color=" $enum.valueOf($enum.PROFILE_AUDIT_STATUS,record.releaseAudit).color">
            {{ $enum.valueOf($enum.PROFILE_AUDIT_STATUS, record.releaseAudit).label }}
          </a-tag>
        </a>
        <!-- 操作 -->
        <div slot="action" slot-scope="record">
          <!-- 修改 -->
          <a @click="update(record.id)">修改</a>
          <a-divider type="vertical"/>
          <!-- 删除 -->
          <a-popconfirm title="确认删除当前环境?"
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
    <div class="profile-list-event">
      <!-- 新建模态框 -->
      <AddAppProfileModal ref="addModal" @added="getList({})" @updated="getList({})"/>
    </div>
  </div>
</template>

<script>

import AddAppProfileModal from '@/components/app/AddAppProfileModal'

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
    title: '环境名称',
    key: 'name',
    dataIndex: 'name',
    width: 180,
    ellipsis: true,
    sorter: (a, b) => a.name.localeCompare(b.name)
  },
  {
    title: '标签',
    key: 'tag',
    ellipsis: true,
    width: 150,
    sorter: (a, b) => a.tag.localeCompare(b.tag),
    scopedSlots: { customRender: 'tag' }
  },
  {
    title: '发布是否需要审核',
    key: 'releaseAudit',
    ellipsis: true,
    align: 'center',
    width: 140,
    sorter: (a, b) => a.releaseAudit - b.releaseAudit,
    scopedSlots: { customRender: 'releaseAudit' }
  },
  {
    title: '描述',
    key: 'description',
    dataIndex: 'description',
    ellipsis: true,
    width: 220,
    scopedSlots: { customRender: 'description' }
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 140,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'AppProfile',
  components: {
    AddAppProfileModal
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
      columns
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getProfileList({
        ...this.query,
        page: page.current,
        limit: page.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.length
        this.rows = data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    changePage(page) {
      const pagination = { ...this.pagination }
      pagination.current = page.current
      this.pagination = pagination
    },
    add() {
      this.$refs.addModal.add()
    },
    update(id) {
      this.$refs.addModal.update(id)
    },
    remove(id) {
      this.$api.deleteProfile({ id })
        .then(() => {
          this.getList({})
        })
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
