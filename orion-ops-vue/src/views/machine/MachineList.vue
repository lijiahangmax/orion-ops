<template>
  <div class="machine-list-container">
    <!-- 条件 -->
    <div class="machine-columns-search">
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
          <a-col :span="3">
            <a-form-model-item label="状态" prop="status">
              <a-select v-model="query.status" placeholder="全部" allowClear>
                <a-select-option :value="status.value" v-for="status in $enum.MACHINE_STATUS" :key="status.value">
                  {{ status.label }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <a-form-model-item>
              <a-button-group>
                <a-button type="primary" @click="getList({})" icon="search">查询</a-button>
                <a-button @click="resetForm" icon="reload">重置</a-button>
              </a-button-group>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 工具栏 -->
    <div class="machine-tools-bar">
      <a-button type="primary" icon="plus" @click="openAdd">新建</a-button>
      <a-button type="primary" icon="build" v-show="selectedRowKeys.length" @click="batchStatus(1)">启用</a-button>
      <a-button type="primary" icon="fork" v-show="selectedRowKeys.length" @click="batchStatus(2)">停用</a-button>
      <a-button type="danger" icon="delete" v-show="selectedRowKeys.length" @click="batchRemove()">删除</a-button>
    </div>
    <!-- 表格 -->
    <div class="machine-list-table">
      <a-table :columns="columns"
               :dataSource="rows"
               :pagination="pagination"
               :rowSelection="rowSelection"
               rowKey="id"
               @change="getList"
               :loading="loading"
               size="middle">
        <a slot="tag" slot-scope="record">
          <a-tag v-if="record.tag" color="#20C997">
            {{ record.tag }}
          </a-tag>
          <a-tag v-if="record.id === 1" color="#5C7CFA">
            宿主机
          </a-tag>
        </a>
        <a slot="host" slot-scope="record" @click="copyHost(record.host)">
          {{ record.host }}
        </a>
        <a slot="sshPort" slot-scope="record" @click="copySshCommand(record)">
          {{ record.sshPort }}
        </a>
        <a slot="status" slot-scope="record">
          <a-popconfirm :title="`确认${record.status === 1 ? '停用' : '启用'}当前机器?`"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="changeStatus(record)">
            <a-badge
              v-if="record.status"
              :status='$enum.valueOf($enum.MACHINE_STATUS, record.status)["badge-status"]'
              :text="$enum.valueOf($enum.MACHINE_STATUS, record.status).label"/>
          </a-popconfirm>
        </a>
        <span slot="action" slot-scope="record">
          <a @click="openDetail(record)">详情</a>
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
      <AddMachineModal ref="addModal" @added="getList()" @updated="getList()"/>
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
    customRender: (text, record, index) => `${index + 1}`,
    width: 60,
    align: 'center'
  },
  {
    title: '名称',
    dataIndex: 'name',
    key: 'name',
    width: 150,
    sorter: (a, b) => a.name.localeCompare(b.name)
  },
  {
    title: 'tag',
    key: 'tag',
    width: 150,
    scopedSlots: { customRender: 'tag' },
    sorter: (a, b) => a.tag.localeCompare(b.tag)
  },
  {
    title: '主机',
    key: 'host',
    width: 180,
    scopedSlots: { customRender: 'host' },
    sorter: (a, b) => a.host.localeCompare(b.host)
  },
  {
    title: '端口',
    key: 'sshPort',
    width: 80,
    scopedSlots: { customRender: 'sshPort' },
    sorter: (a, b) => a.sshPort - b.sshPort
  },
  {
    title: '状态',
    key: 'status',
    scopedSlots: { customRender: 'status' },
    width: 80
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    width: 200
  },
  {
    title: '操作',
    key: 'operation',
    fixed: 'right',
    width: 230,
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
          this.getList()
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
        this.$api.copyMachine({
          id: record.id
        }).then(() => {
          this.$message.success('复制成功')
          this.getList()
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
      })
      .catch(() => {
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
      })
      .catch(() => {
        connecting()
        this.$message.error(`无法连接 ${ssh}`)
      })
  },
  openEnv(record) {
    this.$router.push({
      path: '/machine/env',
      query: { machineId: record.id }
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
        status: null
      },
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showTotal: function(total) {
          return `共 ${total}条`
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
    copyHost(host) {
      this.$copy(host, `${host} 已复制`)
    },
    copySshCommand(record) {
      const command = this.$utils.getSshCommand(record.username, record.host, record.sshPort)
      this.$copy(command, `${command} 已复制`)
    },
    openAdd() {
      this.$refs.addModal.add()
    },
    openDetail(record) {
      this.$refs.detailModal.open(record.id)
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
            this.getList()
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

.machine-columns-search {
  margin: 12px 12px 0 12px;
}

.machine-tools-bar {
  margin-bottom: 12px;
}

.machine-tools-bar > button {
  margin-right: 8px;
}

.machine-info-search-form /deep/ .ant-form-item {
  display: flex;
}

.machine-info-search-form /deep/ .ant-form-item-control-wrapper {
  flex: 0.8;
}

</style>
