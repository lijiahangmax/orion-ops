<template>
  <div class="machine-env-container">
    <!-- 机器容器 -->
    <div class="machine-env-machine-container">
      <!-- 机器头 -->
      <div class="machine-env-machine-header">
        <a-page-header @back="() => {}">
          <span slot="title"
                class="ant-page-header-heading-title pointer"
                title="刷新"
                @click="getMachines">机器列表</span>
          <a-icon v-if="!redirectMachineId"
                  slot="backIcon"
                  type="desktop"
                  title="刷新"
                  @click="getMachines"/>
          <a-icon v-else
                  slot="backIcon"
                  type="arrow-left"
                  title="返回"
                  @click="backHistory"/>
        </a-page-header>
      </div>
      <!-- 机器菜单 -->
      <a-spin :spinning="machineLoading">
        <div class="machine-env-machine-list">
          <a-menu mode="inline" :defaultSelectedKeys="defaultSelectedMachineIds">
            <a-menu-item v-for="machine in machineList" :key="machine.id" :title="machine.host" @click="chooseMachine(machine.id)">
              <a-icon type="desktop"/>
              {{ machine.name }}
            </a-menu-item>
          </a-menu>
        </div>
      </a-spin>
    </div>
    <!-- 环境变量容器 -->
    <div class="machine-env-machine-env-container">
      <!-- 环境变量筛选 -->
      <div class="table-search-columns">
        <a-form-model class="machine-env-machine-env-search-form" ref="query" :model="query">
          <a-row>
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
            <a-col :span="6">
              <a-form-model-item label="描述" prop="description">
                <a-input v-model="query.description"/>
              </a-form-model-item>
            </a-col>
          </a-row>
        </a-form-model>
      </div>
      <!-- 工具栏 -->
      <div class="table-tools-bar">
        <!-- 左侧 -->
        <div class="tools-fixed-left">
          <span class="table-title">环境变量</span>
          <a-divider v-show="selectedRowKeys.length" type="vertical"/>
          <a-button-group class="ml8" v-show="selectedRowKeys.length">
            <a-button type="danger" icon="delete" @click="batchRemove()">删除</a-button>
          </a-button-group>
        </div>
        <!-- 右侧 -->
        <div class="tools-fixed-right">
          <a-button class="ml16 mr8" type="primary" icon="plus" @click="add">添加</a-button>
          <a-divider type="vertical"/>
          <a-icon type="search" class="tools-icon" title="查询" @click="getMachineEnv({})"/>
          <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
        </div>
      </div>
      <!-- 环境变量表格 -->
      <div class="table-main-container">
        <a-table :columns="columns"
                 :dataSource="rows"
                 :pagination="pagination"
                 :rowSelection="rowSelection"
                 :loading="loading"
                 rowKey="id"
                 @change="getMachineEnv"
                 :scroll="{x: '100%'}"
                 size="middle">
          <!-- key -->
          <div slot="key" slot-scope="record">
            <a class="copy-icon-left" @click="$copy(record.key)">
              <a-icon type="copy"/>
            </a>
            <span class="pointer" title="预览" @click="preview(record.key)">{{ record.key }}</span>
          </div>
          <!-- value -->
          <div slot="value" slot-scope="record">
            <a class="copy-icon-left" @click="$copy(record.value)">
              <a-icon type="copy"/>
            </a>
            <span class="pointer" title="预览" @click="preview(record.value)">{{ record.value }}</span>
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
            <a @click="update(record.id)">修改</a>
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
      <EnvHistoryModal ref="historyModal" @rollback="getMachineEnv()"/>
      <!-- 预览框 -->
      <TextPreview ref="preview"/>
    </div>
  </div>
</template>

<script>
import _utils from '@/lib/utils'
import AddMachineEnvModal from '@/components/machine/AddMachineEnvModal'
import EnvHistoryModal from '@/components/history/EnvHistoryModal'
import TextPreview from '@/components/preview/TextPreview'

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
    width: 180,
    ellipsis: true,
    sorter: (a, b) => a.key.localeCompare(b.key)
  },
  {
    title: 'value',
    key: 'value',
    scopedSlots: { customRender: 'value' },
    width: 220,
    ellipsis: true,
    sorter: (a, b) => a.value.localeCompare(b.value)
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true,
    width: 180
  },
  {
    title: '修改时间',
    key: 'updateTime',
    scopedSlots: { customRender: 'updateTime' },
    sorter: (a, b) => a.updateTime - b.updateTime,
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
    EnvHistoryModal,
    TextPreview
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
        value: null,
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
      loading: false,
      selectedRowKeys: [],
      columns,
      currentRecord: {}
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
      this.machineList = machines.data.rows.map(i => {
        return {
          id: i.id,
          name: i.name,
          host: i.host
        }
      })
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
    update(id) {
      this.$refs.addModal.update(id)
    },
    remove(id) {
      this.$api.deleteMachineEnv({
        idList: [id]
      }).then(() => {
        this.$message.success('删除成功')
        this.getMachineEnv({})
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
            this.getMachineEnv({})
          })
          this.selectedRowKeys = []
        }
      })
    },
    preview(value) {
      this.$refs.preview.preview(value)
    },
    history(record) {
      this.$refs.historyModal.open({
        key: record.key,
        valueId: record.id,
        valueType: this.$enum.HISTORY_VALUE_TYPE.MACHINE_ENV.value
      })
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
  width: 100%;

  .machine-env-machine-container {
    width: 216px;
    padding: 0 8px;
    margin-right: 16px;
    background-color: #FFF;
    border-radius: 4px;
  }

  .machine-env-machine-env-container {
    width: calc(100% - 222px)
  }

}

.machine-env-machine-list {
  width: 200px;
  min-height: 25vh;
  max-height: calc(100vh - 164px - 12px);
  border-radius: 5px;
  overflow-y: auto;
  margin-right: 20px;
}

.machine-env-machine-list, .machine-env-machine-list ul {
  background-color: #F8F9FA;
}

</style>
