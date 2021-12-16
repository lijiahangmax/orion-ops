<template>
  <div class="machine-list-container">
    <!-- 条件列 -->
    <div class="table-search-columns">
      <a-form-model class="machine-info-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="名称" prop="name">
              <a-input v-model="query.name"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="tag" prop="tag">
              <a-input v-model="query.tag"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="主机" prop="host">
              <a-input v-model="query.host"/>
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
                <a-select-option :value="status.value" v-for="status in $enum.ENABLE_STATUS" :key="status.value">
                  {{ status.label }}
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
        <span class="table-title">机器列表</span>
        <a-divider v-show="selectedRowKeys.length" type="vertical"/>
        <div v-show="selectedRowKeys.length">
          <a-button class="ml8" type="primary" icon="build" @click="batchStatus(1)">启用</a-button>
          <a-button class="ml8" type="primary" icon="fork" @click="batchStatus(2)">停用</a-button>
          <a-button class="ml8" type="danger" icon="delete" @click="batchRemove()">删除</a-button>
        </div>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a target="_blank" href="#/machine/terminal">
          <a-button class="mr8" type="primary" icon="desktop">Terminal</a-button>
        </a>
        <a-button class="mr8" type="primary" icon="plus" @click="openAdd">新建</a-button>
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
               :rowSelection="rowSelection"
               rowKey="id"
               @change="getList"
               :scroll="{x: '100%'}"
               :loading="loading"
               size="middle">
        <!-- 名称 -->
        <span slot="name" slot-scope="record">
          {{ record.name }}
          <a-tag v-if="record.id === 1" color="#5C7CFA">
            宿主机
          </a-tag>
        </span>
        <!-- tag -->
        <span slot="tag" slot-scope="record">
          <a-tag v-if="record.tag" color="#20C997">
            {{ record.tag }}
          </a-tag>
        </span>
        <!-- 主机 -->
        <a slot="host" slot-scope="record" title="复制主机" @click="$copy(record.host, true)">
          {{ record.host }}
        </a>
        <!-- 端口 -->
        <a slot="sshPort" slot-scope="record"
           title="复制ssh"
           @click="$copy($utils.getSshCommand(record.username, record.host, record.sshPort), true)">
          {{ record.sshPort }}
        </a>
        <!-- 状态 -->
        <a v-if="record.id !== 1" slot="status" slot-scope="record" :title="record.status === 1 ? '停用机器' : '启用机器'">
          <a-popconfirm :title="`确认${record.status === 1 ? '停用' : '启用'}当前机器?`"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="changeStatus(record)">
            <a-badge
              v-if="record.status"
              :status='$enum.valueOf($enum.ENABLE_STATUS, record.status)["badge-status"]'
              :text="$enum.valueOf($enum.ENABLE_STATUS, record.status).label"/>
          </a-popconfirm>
        </a>
        <span v-else slot="status" slot-scope="record">
            <a-badge v-if="record.status"
                     :status='$enum.valueOf($enum.ENABLE_STATUS, record.status)["badge-status"]'
                     :text="$enum.valueOf($enum.ENABLE_STATUS, record.status).label"/>
        </span>
        <!-- 更多 -->
        <span slot="action" slot-scope="record">
          <a @click="openDetail(record.id)">详情</a>
          <a-divider type="vertical"/>
          <a v-if="record.status === 1" target="_blank" :href="`#/machine/terminal/${record.id}`">Terminal</a>
          <a-divider v-if="record.status === 1" type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay" @click="menuHandler($event, record)">
              <a-menu-item key="update">
                编辑
              </a-menu-item>
              <a-menu-item v-if="record.id !== 1" key="delete">
                删除
              </a-menu-item>
              <a-menu-item key="copy">
                复制
              </a-menu-item>
              <a-menu-item key="sftp" v-if="record.status === 1">
                sftp
              </a-menu-item>
              <a-menu-item key="ping" v-if="record.status === 1">
                ping
              </a-menu-item>
              <a-menu-item key="connect" v-if="record.status === 1">
                测试连接
              </a-menu-item>
              <a-menu-item key="openEnv">
                环境变量
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>
      </a-table>
    </div>
    <!-- 事件 -->
    <div class="machine-event">
      <!-- 添加模态框 -->
      <AddMachineModal ref="addModal" @added="getList({})" @updated="getList({})"/>
      <!-- 详情模态框 -->
      <MachineDetailModal ref="detailModal"/>
    </div>
  </div>
</template>

<script>
import MachineDetailModal from '@/components/machine/MachineDetailModal'
import AddMachineModal from '@/components/machine/AddMachineModal'

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
    key: 'name',
    width: 200,
    ellipsis: true,
    sorter: (a, b) => a.name.localeCompare(b.name),
    scopedSlots: { customRender: 'name' }
  },
  {
    title: 'tag',
    key: 'tag',
    width: 150,
    sorter: (a, b) => a.tag.localeCompare(b.tag),
    scopedSlots: { customRender: 'tag' }
  },
  {
    title: '主机',
    key: 'host',
    width: 180,
    sorter: (a, b) => a.host.localeCompare(b.host),
    scopedSlots: { customRender: 'host' }
  },
  {
    title: '端口',
    key: 'sshPort',
    width: 80,
    sorter: (a, b) => a.sshPort - b.sshPort,
    scopedSlots: { customRender: 'sshPort' }
  },
  {
    title: '状态',
    key: 'status',
    width: 80,
    scopedSlots: { customRender: 'status' }
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true,
    width: 200
  },
  {
    title: '操作',
    key: 'operation',
    fixed: 'right',
    width: 190,
    scopedSlots: { customRender: 'action' }
  }
]

const moreMenuHandler = {
  update(record) {
    this.$refs.addModal.update(record.id)
  },
  delete(record) {
    this.$confirm({
      title: '确认删除',
      content: '是否删除当前机器? 将会删除机器相关联的所有数据!',
      okText: '确认',
      okType: 'danger',
      cancelText: '取消',
      onOk: () => {
        this.$api.deleteMachine({
          idList: [record.id]
        }).then(() => {
          this.$message.success('删除成功')
          this.getList({})
        })
      }
    })
  },
  copy(record) {
    this.$confirm({
      title: '确认复制',
      content: `是否复制机器 ${record.name}?`,
      okText: '复制',
      cancelText: '取消',
      onOk: () => {
        const copyLoading = this.$message.loading('正在提交复制请求', 3)
        this.$api.copyMachine({
          id: record.id
        }).then(() => {
          copyLoading()
          this.$message.success('复制成功')
          this.getList()
        }).catch(() => {
          copyLoading()
        })
      }
    })
  },
  sftp(record) {
    this.$router.push({ path: `/machine/sftp/${record.id}` })
  },
  ping(record) {
    const ping = this.$message.loading(`ping ${record.host}`, 10)
    this.$api.machineTestPing({ id: record.id })
      .then(e => {
        ping()
        if (e.data === 1) {
          this.$message.success('ok')
        } else {
          this.$message.error(`无法访问 ${record.host}`)
        }
      }).catch(() => {
      ping()
      this.$message.error(`无法访问 ${record.host}`)
    })
  },
  connect(record) {
    const ssh = `${record.username}@${record.host}:${record.sshPort}`
    const connecting = this.$message.loading(`connecting ${ssh}`, 10)
    this.$api.machineTestConnect({ id: record.id })
      .then(e => {
        connecting()
        if (e.data === 1) {
          this.$message.success('ok')
        } else {
          this.$message.error(`无法连接 ${ssh}`)
        }
      }).catch(() => {
      connecting()
      this.$message.error(`无法连接 ${ssh}`)
    })
  },
  openEnv(record) {
    this.$router.push({
      path: `/machine/env/${record.id}`
    })
  }
}

export default {
  name: 'MachineList',
  components: {
    MachineDetailModal,
    AddMachineModal
  },
  data() {
    return {
      rows: [],
      query: {
        name: null,
        tag: null,
        host: null,
        status: undefined,
        description: null
      },
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showTotal: function(total) {
          return `共 ${total} 条`
        }
      },
      columns,
      loading: false,
      selectedRowKeys: []
    }
  },
  computed: {
    rowSelection() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        onChange: e => {
          this.selectedRowKeys = e
        },
        getCheckboxProps: record => ({
          props: {
            disabled: record.id === 1
          }
        })
      }
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getMachineList({
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
        this.selectedRowKeys = []
      }).catch(() => {
        this.loading = false
      })
    },
    openAdd() {
      this.$refs.addModal.add()
    },
    openDetail(id) {
      this.$refs.detailModal.open(id)
    },
    menuHandler({ key }, record) {
      moreMenuHandler[key].call(this, record)
    },
    changeStatus(record) {
      if (record.id === 1) {
        this.$message.error('宿主机不支持该操作')
      } else {
        this.$api.updateMachineStatus({
          idList: [record.id],
          status: record.status === 1 ? 2 : 1
        }).then(() => {
          this.$message.success('修改成功')
          this.getList()
        })
      }
    },
    batchStatus(status) {
      this.$confirm({
        title: `确认${status === 1 ? '启用' : '停用'}`,
        content: `是否${status === 1 ? '启用' : '停用'}选中机器?`,
        okText: '确认',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          this.$api.updateMachineStatus({
            idList: this.selectedRowKeys,
            status
          }).then(() => {
            this.$message.success('修改成功')
            this.getList()
          })
          this.selectedRowKeys = []
        }
      })
    },
    batchRemove() {
      this.$confirm({
        title: '确认删除',
        content: '是否删除选择中机器? 将会删除机器相关联的所有数据!',
        okText: '确认',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          this.$api.deleteMachine({
            idList: this.selectedRowKeys
          }).then(() => {
            this.$message.success('删除成功')
            this.getList({})
          })
          this.selectedRowKeys = []
        }
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
