<template>
  <div class="user-list-container">
    <!-- 筛选列 -->
    <div class="table-search-columns">
      <a-form-model class="user-list-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="用户名" prop="username">
              <a-input v-model="query.username" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="昵称" prop="nickname">
              <a-input v-model="query.nickname" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="手机号" prop="phone">
              <a-input v-model="query.phone" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="邮箱" prop="email">
              <a-input v-model="query.email" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <a-form-model-item label="角色" prop="role">
              <a-select v-model="query.role" placeholder="全部" @change="getList({})" allowClear>
                <a-select-option v-for="role in ROLE_TYPE"
                                 :value="role.value"
                                 :key="role.value">
                  {{ role.label }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 表格 -->
    <div class="table-wrapper">
      <!-- 工具 -->
      <div class="table-tools-bar">
        <!-- 左侧 -->
        <div class="tools-fixed-left">
          <span class="table-title">用户列表</span>
        </div>
        <!-- 右侧 -->
        <div class="tools-fixed-right">
          <!-- 状态单选 -->
          <div class="mr8 nowrap">
            <a-radio-group v-model="query.status" @change="getList({})">
              <a-radio-button :value="undefined">
                全部
              </a-radio-button>
              <a-radio-button :value="1">
                启用
              </a-radio-button>
              <a-radio-button :value="2">
                禁用
              </a-radio-button>
            </a-radio-group>
          </div>
          <a-divider type="vertical"/>
          <a-button class="mx8" type="primary" icon="plus" @click="add">添加</a-button>
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
          <template #username="record">
            <span class="pointer" @click="$copy(record.username)" title="复制">{{ record.username }}</span>
          </template>
          <!-- 昵称 -->
          <template #nickname="record">
            <span class="pointer" @click="$copy(record.nickname)" title="复制">{{ record.nickname }}</span>
          </template>
          <!-- 手机号 -->
          <template #phone="record">
            <span class="pointer span-blue" @click="$copy(record.phone)" title="复制">{{ record.phone }}</span>
          </template>
          <!-- 邮箱 -->
          <template #email="record">
            <span class="pointer span-blue" @click="$copy(record.email)" title="复制">{{ record.email }}</span>
          </template>
          <!-- 角色 -->
          <template #role="record">
            <span>{{ record.role | formatRoleType('label') }}</span>
          </template>
          <!-- 状态 -->
          <template #status="record">
            <a-badge :status="record.status | formatEnableStatus('status')"
                     :text="record.status | formatEnableStatus('label')"/>
          </template>
          <!-- 登录时间 -->
          <template #lastLoginTime="record">
          <span v-if="record.lastLoginTime">
            {{ record.lastLoginTime | formatDate }} ({{ record.lastLoginAgo }})
          </span>
          </template>
          <!-- 操作 -->
          <template #action="record">
            <div v-if="record.id !== $getUserId()">
              <!-- 解锁 -->
              <a-button :disabled="record.locked === 1"
                        style="padding: 0; height: 24px"
                        type="link"
                        @click="unlock(record.id)">
                解锁
              </a-button>
              <a-divider type="vertical"/>
              <!-- 状态 -->
              <a-popconfirm :title="`确认${record.status === 1 ? '禁用' : '启用'}当前用户?`"
                            ok-text="确定"
                            cancel-text="取消"
                            @confirm="updateStatus(record)">
                <span class="span-blue pointer">{{ record.status === 1 ? '禁用' : '启用' }}</span>
              </a-popconfirm>
              <a-divider type="vertical"/>
              <!-- 修改 -->
              <span class="span-blue pointer" @click="update(record.id)">修改</span>
              <a-divider type="vertical"/>
              <!-- 操作日志 -->
              <a :href="`#/user/event/logs/${record.id}`">日志</a>
              <a-divider type="vertical"/>
              <!-- 重置密码 -->
              <a @click="resetPassword(record.id)">重置密码</a>
              <a-divider type="vertical"/>
              <!-- 删除 -->
              <a-popconfirm title="是否要删除当前用户?"
                            placement="topRight"
                            ok-text="确定"
                            cancel-text="取消"
                            @confirm="remove(record.id)">
                <span class="span-blue pointer">删除</span>
              </a-popconfirm>
            </div>
            <div v-else>
              <!-- 操作日志 -->
              <a :href="`#/user/event/logs/${record.id}`">日志</a>
            </div>
          </template>
        </a-table>
      </div>
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
import { formatDate } from '@/lib/filters'
import { ENABLE_STATUS, enumValueOf, ROLE_TYPE } from '@/lib/enum'
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
      ellipsis: true,
      sorter: (a, b) => a.username.localeCompare(b.username),
      scopedSlots: { customRender: 'username' }
    },
    {
      title: '昵称',
      key: 'nickname',
      ellipsis: true,
      sorter: (a, b) => a.nickname.localeCompare(b.nickname),
      scopedSlots: { customRender: 'nickname' }
    },
    {
      title: '联系电话',
      key: 'phone',
      ellipsis: true,
      align: 'center',
      scopedSlots: { customRender: 'phone' }
    },
    {
      title: '联系邮箱',
      key: 'email',
      ellipsis: true,
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
      width: 220,
      ellipsis: true,
      align: 'center',
      sorter: (a, b) => (a.lastLoginTime || 0) - (b.lastLoginTime || 0),
      scopedSlots: { customRender: 'lastLoginTime' }
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 310,
      align: 'center',
      scopedSlots: { customRender: 'action' },
      requireAdmin: true
    }
  ]
  if (this.$isAdmin()) {
    return columns
  } else {
    return columns.filter(s => !s.requireAdmin)
  }
}

export default {
  name: 'UserList',
  components: {
    ResetPassword,
    AddUserModal
  },
  data() {
    return {
      ROLE_TYPE,
      query: {
        username: undefined,
        nickname: undefined,
        phone: undefined,
        email: undefined,
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
        this.rows = data.rows || []
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
    unlock(id) {
      this.$api.unlockUser({
        id: id
      }).then(() => {
        this.$message.success('已解锁')
        this.getList()
      })
    },
    remove(id) {
      this.$api.deleteUser({
        id: id
      }).then(() => {
        this.$message.success('删除成功')
        this.getList({})
      })
    },
    updateStatus(record) {
      const label = record.status === 1 ? '禁用' : '启用'
      const pending = this.$message.loading(`正在${label}...`)
      this.$api.updateUserStatus({
        id: record.id,
        status: record.status === 1 ? 2 : 1
      }).then(() => {
        pending()
        this.$message.success(`${label}成功`)
        this.getList()
      }).catch(() => {
        pending()
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
    formatDate,
    formatEnableStatus(status, f) {
      return enumValueOf(ENABLE_STATUS, status)[f]
    },
    formatRoleType(type, f) {
      return enumValueOf(ROLE_TYPE, type)[f]
    }
  },
  mounted() {
    this.getList({})
  }
}
</script>

<style scoped>

</style>
