<template>
  <div class="user-list-container">
    <!-- 筛选列 -->
    <div class="table-search-columns">
      <a-form-model class="user-list-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="用户名" prop="username">
              <a-input v-model="query.username"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="昵称" prop="nickname">
              <a-input v-model="query.nickname"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="手机号" prop="phone">
              <a-input v-model="query.phone"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="邮箱" prop="email">
              <a-input v-model="query.email"/>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row style="margin-top: 8px">
          <a-col :span="5">
            <a-form-model-item label="角色" prop="role">
              <a-select v-model="query.role" placeholder="全部" allowClear>
                <a-select-option v-for="role in $enum.ROLE_TYPE"
                                 :value="role.value"
                                 :key="role.value">
                  {{ role.label }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="状态" prop="status">
              <a-select v-model="query.status" placeholder="全部" allowClear>
                <a-select-option :value="1">
                  启用
                </a-select-option>
                <a-select-option :value="2">
                  停用
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 工具 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <span class="table-title">用户列表</span>
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
               @change="getList"
               :scroll="{x: '100%'}"
               :loading="loading"
               size="middle">
        <!-- 用户名 -->
        <div class="pointer" slot="username" slot-scope="record" @click="$copy(record.username)" title="点击复制">
          <span>{{ record.username }}</span>
        </div>
        <!-- 昵称 -->
        <span class="pointer" slot="nickname" slot-scope="record" @click="$copy(record.nickname)" title="点击复制">
            {{ record.nickname }}
        </span>
        <!-- 手机号 -->
        <span class="pointer" slot="phone" slot-scope="record" @click="$copy(record.phone)" title="点击复制">
            {{ record.phone }}
        </span>
        <!-- 邮箱 -->
        <span class="pointer" slot="email" slot-scope="record" @click="$copy(record.email)" title="点击复制">
            {{ record.email }}
        </span>
        <!-- 角色 -->
        <span slot="role" slot-scope="record">
            {{ $enum.valueOf($enum.ROLE_TYPE, record.role).label }}
        </span>
        <!-- 状态 -->
        <span slot="status" slot-scope="record">
            <a-badge
              :status='$enum.valueOf($enum.ENABLE_STATUS, record.status)["badge-status"]'
              :text="$enum.valueOf($enum.ENABLE_STATUS, record.status).label"/>
        </span>
        <!-- 登陆时间 -->
        <span slot="lastLoginTime" slot-scope="record" v-if="record.lastLoginTime">
          {{
            record.lastLoginTime | formatDate({
              date: record.lastLoginTime,
              pattern: 'yyyy-MM-dd HH:mm:ss'
            })
          }} ({{ record.lastLoginAgo }})
        </span>
        <!-- 操作 -->
        <div v-if="record.id !== $getUserId()" slot="action" slot-scope="record">
          <!-- 状态 -->
          <a-popconfirm :title="`确认${record.status === 1 ? '停用' : '启用'}当前用户?`"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="updateStatus(record)">
            <span class="span-blue pointer">{{ record.status === 1 ? '停用' : '启用' }}</span>
          </a-popconfirm>
          <a-divider type="vertical"/>
          <!-- 修改 -->
          <span class="span-blue pointer" @click="update(record.id)">修改</span>
          <a-divider type="vertical"/>
          <!-- 重置密码 -->
          <a @click="resetPassword(record.id)">重置密码</a>
          <a-divider type="vertical"/>
          <!-- 删除 -->
          <a-popconfirm title="确认要删除该行记录?"
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
    <div class="log-list-event">
      <!-- 添加模态框 -->
      <AddUserModal ref="addModal" @added="getList({})" @updated="getList({})"/>
      <!-- 重置密码模态框 -->
      <ResetPassword ref="reset" :visibleBeforePassword="false"/>
    </div>
  </div>
</template>

<script>

import _utils from '@/lib/utils'
import ResetPassword from '@/components/user/ResetPassword'
import AddUserModal from '@/components/user/AddUserModal'

/**
 * 列
 */
function getColumns() {
  const columns = [
    {
      title: '序号',
      key: 'seq',
      width: 65,
      align: 'center',
      customRender: (text, record, index) => `${index + 1}`
    },
    {
      title: '用户名',
      key: 'username',
      width: 150,
      ellipsis: true,
      sorter: (a, b) => a.username.localeCompare(b.username),
      scopedSlots: { customRender: 'username' }
    },
    {
      title: '昵称',
      key: 'nickname',
      ellipsis: true,
      width: 150,
      sorter: (a, b) => a.nickname.localeCompare(b.nickname),
      scopedSlots: { customRender: 'nickname' }
    },
    {
      title: '联系电话',
      key: 'phone',
      ellipsis: true,
      width: 150,
      align: 'center',
      scopedSlots: { customRender: 'phone' }
    },
    {
      title: '联系邮箱',
      key: 'email',
      ellipsis: true,
      width: 200,
      scopedSlots: { customRender: 'email' }
    },
    {
      title: '角色',
      key: 'role',
      ellipsis: true,
      width: 100,
      sorter: (a, b) => a.role > b.role,
      scopedSlots: { customRender: 'role' }
    },
    {
      title: '状态',
      key: 'status',
      ellipsis: true,
      width: 100,
      sorter: (a, b) => a.status > b.status,
      scopedSlots: { customRender: 'status' }
    },
    {
      title: '最后登录时间',
      key: 'lastLoginTime',
      width: 200,
      ellipsis: true,
      align: 'center',
      sorter: (a, b) => a.lastLoginTime - b.lastLoginTime,
      scopedSlots: { customRender: 'lastLoginTime' }
    }
  ]
  if (this.$isAdmin()) {
    columns.push({
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 220,
      align: 'center',
      scopedSlots: { customRender: 'action' }
    })
  }
  return columns
}

export default {
  name: 'UserList',
  components: {
    ResetPassword,
    AddUserModal
  },
  data() {
    return {
      query: {
        username: null,
        nickname: null,
        phone: null,
        email: null,
        role: undefined,
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
      columns: getColumns.call(this)
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getUserList({
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
    update(id) {
      this.$refs.addModal.update(id)
    },
    add() {
      this.$refs.addModal.add()
    },
    resetPassword(id) {
      this.$refs.reset.open(id)
    },
    remove(id) {
      this.$api.deleteUser({ idList: [id] })
        .then(() => {
          this.$message.success('删除成功')
          this.getList({})
        })
    },
    updateStatus(record) {
      this.$api.updateUserStatus({
        idList: [record.id],
        status: record.status === 1 ? 2 : 1
      }).then(() => {
        this.$message.success('修改成功')
        this.getList()
      })
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.query.role = undefined
      this.query.status = undefined
      this.getList({})
    }
  },
  filters: {
    formatDate(origin, {
      date,
      pattern
    }) {
      return _utils.dateFormat(new Date(date), pattern)
    }
  },
  mounted() {
    this.getList({})
  }
}
</script>

<style scoped>

</style>
