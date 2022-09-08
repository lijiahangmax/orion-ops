<template>
  <div class="alarm-group-container">
    <!-- 搜索列 -->
    <div class="table-search-columns">
      <a-form-model class="alarm-group-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="6">
            <a-form-model-item label="名称" prop="name">
              <a-input v-model="query.name" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="6">
            <a-form-model-item label="描述" prop="description">
              <a-input v-model="query.description" allowClear/>
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
          <span class="table-title">报警组列表</span>
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
          <!-- 组员 -->
          <template #groupUsers="record">
            <div class="alarm-group-user-wrapper">
              <a-tooltip v-for="(groupUser, index) of record.groupUsers"
                         placement="top"
                         :key="groupUser.id"
                         :title="groupUser.username">
              <span class="span-blue pointer" @click="$copy(groupUser.username, true)">
                {{ groupUser.nickname }}
                <template v-if="index !== record.groupUsers.length - 1">
                  ,
                </template>
              </span>
              </a-tooltip>
            </div>
          </template>
          <!-- 操作 -->
          <template #action="record">
            <!-- 修改 -->
            <a @click="update(record.id)">修改</a>
            <a-divider type="vertical"/>
            <!-- 删除 -->
            <a-popconfirm title="确认删除当前报警组?"
                          placement="topRight"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="remove(record.id)">
              <span class="span-blue pointer">删除</span>
            </a-popconfirm>
          </template>
        </a-table>
      </div>
    </div>
    <!-- 新建模态框 -->
    <AddAlarmGroup ref="addModal" :mask="true" @added="getList({})" @updated="getList({})"/>
  </div>
</template>

<script>
import AddAlarmGroup from '@/components/alarm/AddAlarmGroup'

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
    title: '报警组名称',
    dataIndex: 'name',
    key: 'name',
    width: 240,
    ellipsis: true
  },
  {
    title: '报警组组员',
    key: 'groupUsers',
    ellipsis: true,
    scopedSlots: { customRender: 'groupUsers' }
  },
  {
    title: '报警组描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true
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
  name: 'AlarmGroupList',
  components: {
    AddAlarmGroup
  },
  data() {
    return {
      query: {
        name: undefined,
        description: undefined
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
      this.$api.getAlarmGroupList({
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
      this.$api.deleteAlarmGroup({
        id
      }).then(() => {
        this.$message.success('删除成功')
        this.getList({})
      })
    }
  },
  mounted() {
    this.getList({})
  }
}
</script>

<style scoped>
.alarm-group-user-wrapper {
  display: contents;
}
</style>
