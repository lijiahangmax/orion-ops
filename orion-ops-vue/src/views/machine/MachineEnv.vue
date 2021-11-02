<template>
  <div class="machine-env-container">
    <!-- 机器容器 -->
    <div class="machine-env-machine-container">
      <!-- 机器头 -->
      <div class="machine-env-machine-header">
        <a-page-header
          title="机器列表"
          @back="() => {}">
          <a-icon v-if="!redirectMachineId" slot="backIcon" type="desktop" @click="getMachines"/>
          <a-icon v-else slot="backIcon" type="arrow-left" @click="backHistory"/>
        </a-page-header>
      </div>
      <!-- 机器菜单 -->
      <a-spin :spinning="machineLoading">
        <div class="machine-env-machine-list">
          <a-menu mode="inline" :defaultSelectedKeys="defaultSelectedMachineIds">
            <a-menu-item v-for="machine in machineList" :key="machine.id" @click="chooseMachine(machine.id)">
              {{ machine.name }}
            </a-menu-item>
          </a-menu>
        </div>
      </a-spin>
    </div>
    <!-- 环境变量容器 -->
    <div class="machine-env-machine-env-container">
      <!-- 环境变量筛选 -->
      <div class="machine-env-machine-env-search">
        <a-form-model class="machine-env-machine-env-search-form" ref="query" :model="query">
          <a-row>
            <a-col :span="5">
              <a-form-model-item class="env-tools-bar">
                <a-button type="primary" @click="add" icon="plus">添加</a-button>
                <a-button type="danger" icon="delete" v-show="selectedRowKeys.length" @click="batchRemove()">删除</a-button>
              </a-form-model-item>
            </a-col>
            <a-col :span="6">
              <a-form-model-item label="key" prop="key">
                <a-input v-model="query.key"/>
              </a-form-model-item>
            </a-col>
            <a-col :span="6">
              <a-form-model-item label="value" prop="value">
                <a-input v-model="query.value"/>
              </a-form-model-item>
            </a-col>
            <a-col :span="4">
              <a-form-model-item>
                <a-button-group>
                  <a-button type="primary" icon="search" @click="getMachineEnv({})">查询</a-button>
                  <a-button icon="reload" @click="resetForm">重置</a-button>
                </a-button-group>
              </a-form-model-item>
            </a-col>
          </a-row>
        </a-form-model>
      </div>
      <!-- 环境变量表格 -->
      <div class="machine-env-machine-env-table">
        <a-table :columns="columns"
                 :dataSource="rows"
                 :pagination="pagination"
                 :rowSelection="rowSelection"
                 :loading="loading"
                 rowKey="id"
                 @change="getMachineEnv"
                 size="middle">
          <!-- key -->
          <div slot="key" slot-scope="record">
            <a class="copy-icon-left" @click="$copy(record.key)">
              <a-icon type="copy"/>
            </a>
            <span :title="record.key">{{ record.key }}</span>
          </div>
          <!-- value -->
          <div slot="value" :title="record.value" slot-scope="record">
            <a class="copy-icon-left" @click="$copy(record.value)">
              <a-icon type="copy"/>
            </a>
            <span :title="record.value">{{ record.value }}</span>
          </div>
          <!-- 修改时间 -->
          <span slot="updateTime" slot-scope="record">
              {{
              record.updateTime | formatDate({
                date: record.updateTime,
                pattern: 'yyyy-MM-dd HH:mm:ss'
              })
            }}
          </span>
          <!-- 操作 -->
          <div slot="action" slot-scope="record">
            <a @click="update(record)">修改</a>
            <a-divider type="vertical"/>
            <a @click="history(record)">历史</a>
            <a-divider v-if="record.forbidDelete === 1" type="vertical"/>
            <a-popconfirm v-if="record.forbidDelete === 1"
                          placement="topRight"
                          title="是否删除当前变量?"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="remove(record.id)">
              <span class="span-blue pointer">删除</span>
            </a-popconfirm>
          </div>
        </a-table>
      </div>
    </div>
    <!-- 事件 -->
    <div class="machine-env-event-container">
      <!-- 添加模态框 -->
      <AddMachineEnvModal ref="addModal" @added="getMachineEnv({})" @updated="getMachineEnv({})"/>
      <!-- 历史模态框 -->
      <EnvHistoryModal ref="historyModal" :env="historyEnv" @rollback="getMachineEnv()"/>
    </div>
  </div>
</template>

<script>
import _utils from '@/lib/utils'
import AddMachineEnvModal from '@/components/machine/AddMachineEnvModal'
import EnvHistoryModal from '@/components/history/EnvHistoryModal'

const columns = [
  {
    title: '序号',
    key: 'seq',
    customRender: (text, record, index) => `${index + 1}`,
    width: 60,
    align: 'center'
  },
  {
    title: 'key',
    key: 'key',
    scopedSlots: { customRender: 'key' },
    width: 200,
    sorter: (a, b) => a.key.localeCompare(b.key)
  },
  {
    title: 'value',
    key: 'value',
    scopedSlots: { customRender: 'value' },
    width: 250,
    sorter: (a, b) => a.value.localeCompare(b.value)
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true,
    width: 250
  },
  {
    title: '修改时间',
    key: 'updateTime',
    scopedSlots: { customRender: 'updateTime' },
    sorter: (a, b) => a.updateTime.localeCompare(b.updateTime),
    width: 150
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 140,
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'MachineEnv',
  components: {
    AddMachineEnvModal,
    EnvHistoryModal
  },
  data: function() {
    return {
      defaultSelectedMachineIds: [],
      redirectMachineId: null,
      machineLoading: false,
      machineList: [],
      rows: [],
      query: {
        machineId: null,
        key: null,
        value: null
      },
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showTotal: function(total) {
          return `共 ${total}条`
        }
      },
      loading: false,
      selectedRowKeys: [],
      columns,
      currentRecord: {},
      historyEnv: {
        key: null,
        valueId: null,
        valueType: 10
      }
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
            disabled: record.forbidDelete !== 1
          }
        })
      }
    }
  },
  methods: {
    async getMachines() {
      this.machineLoading = true
      const machines = await this.$api.getMachineList({ limit: 10000 })
      this.machineList = machines.data.rows
      this.machineLoading = false
    },
    chooseMachine(id) {
      this.query.machineId = id
      this.pagination.current = 1
      this.getMachineEnv()
    },
    getMachineEnv(page = this.pagination) {
      this.loading = true
      this.$api.getMachineEnvList({
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
    add() {
      this.$refs.addModal.add(this.query.machineId)
    },
    update(record) {
      this.$refs.addModal.update(record)
    },
    remove(id) {
      this.$api.deleteMachineEnv({
        idList: [id]
      }).then(() => {
        this.$message.success('删除成功')
        this.getMachineEnv()
      })
    },
    batchRemove() {
      this.$confirm({
        title: '确认删除',
        content: '是否删除所选环境变量?',
        okType: 'danger',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$api.deleteMachineEnv({
            idList: this.selectedRowKeys
          }).then(() => {
            this.$message.success('删除成功')
            this.getMachineEnv()
          })
          this.selectedRowKeys = []
        }
      })
    },
    history(record) {
      this.$refs.historyModal.visible = true
      this.historyEnv.key = record.key
      this.historyEnv.valueId = record.id
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.getMachineEnv({})
    },
    backHistory() {
      this.$router.back(-1)
    }
  },
  created() {
    if (this.$route.query.machineId) {
      this.redirectMachineId = parseInt(this.$route.query.machineId)
    }
  },
  async mounted() {
    await this.getMachines()
    let chooseId
    if (this.redirectMachineId) {
      chooseId = this.redirectMachineId
    } else if (this.machineList.length) {
      chooseId = this.machineList[0].id
    }
    this.chooseMachine(chooseId)
    this.defaultSelectedMachineIds.push(chooseId)
  },
  filters: {
    formatDate(origin, {
      date,
      pattern
    }) {
      return _utils.dateFormat(new Date(date), pattern)
    }
  }
}
</script>

<style lang="less" scoped>

.machine-env-container {
  display: flex;
  justify-content: flex-start;
}

.machine-env-machine-list {
  width: 200px;
  min-height: 25vh;
  max-height: 75vh;
  border-radius: 5px;
  overflow-y: auto;
  margin-right: 20px;
}

.machine-env-machine-env-search {
  margin: 20px 0 0 5px;
}

.machine-env-machine-list, .machine-env-machine-list ul {
  background-color: #f8f9fa;
}

.machine-env-machine-env-container {
  width: 100%;
}

.machine-env-machine-env-search-form /deep/ .ant-form-item {
  display: flex;
}

.machine-env-machine-env-search-form /deep/ .ant-form-item-control-wrapper {
  flex: 0.8;
}

.env-tools-bar /deep/ button {
  margin-left: 8px;
}

</style>