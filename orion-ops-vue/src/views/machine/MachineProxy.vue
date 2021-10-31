<template>
  <div class="machine-proxy-container">
    <!-- 搜索列 -->
    <div class="machine-proxy-column">
      <a-form-model class="machine-proxy-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="代理主机" prop="host">
              <a-input v-model="query.host"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="代理端口" prop="port">
              <a-input v-limit-integer v-model="query.port"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="代理用户" prop="username">
              <a-input v-model="query.username"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="代理类型" prop="type">
              <a-select v-model="query.type" placeholder="全部" allowClear>
                <a-select-option :value="type.value" v-for="type in $enum.MACHINE_PROXY_TYPE" :key="type.value">
                  {{type.label}}
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
    <div class="machine-proxy-tools-bar">
      <a-button type="primary" icon="plus" @click="add">新建</a-button>
      <a-button type="danger" icon="delete" v-show="selectedRowKeys.length" @click="batchRemove()">批量删除</a-button>
    </div>
    <!-- 表格 -->
    <div class="machine-proxy-table">
      <a-table :columns="columns"
               :dataSource="rows"
               :pagination="pagination"
               :rowSelection="{selectedRowKeys, onChange: e => selectedRowKeys = e}"
               rowKey="id"
               @change="getList"
               :loading="loading"
               size="middle">
        <!-- 主机 -->
        <div slot="host" slot-scope="record">
          <span>{{record.host}}</span>
          <a class="copy-icon-right" @click="$copy(record.host)">
            <a-icon type="copy"/>
          </a>
        </div>
        <!-- 类型 -->
        <a-tag slot="type" slot-scope="record" :color="$enum.valueOf($enum.MACHINE_PROXY_TYPE, record.type).color">
          {{$enum.valueOf($enum.MACHINE_PROXY_TYPE, record.type).label}}
        </a-tag>
        <!-- 操作 -->
        <div slot="action" slot-scope="record">
          <!-- 修改 -->
          <a @click="update(record)">修改</a>
          <a-divider type="vertical"/>
          <!-- 删除 -->
          <a @click="remove(record.id)">删除</a>
        </div>
      </a-table>
    </div>
    <!-- 事件 -->
    <div class="machine-proxy-event">
      <AddMachineProxyModal ref="addModal" @added="getList({})" @updated="getList()"/>
    </div>
  </div>
</template>

<script>
  import AddMachineProxyModal from '../../components/machine/AddMachineProxyModal'

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
      title: '代理主机',
      key: 'host',
      width: 200,
      scopedSlots: { customRender: 'host' },
      sorter: (a, b) => a.host.localeCompare(b.host)
    },
    {
      title: '代理端口',
      dataIndex: 'port',
      key: 'port',
      width: 150,
      sorter: (a, b) => a.port > b.port
    },
    {
      title: '代理用户',
      dataIndex: 'username',
      key: 'username',
      width: 200,
      sorter: (a, b) => a.host.localeCompare(b.host)
    },
    {
      title: '代理类型',
      key: 'type',
      width: 150,
      scopedSlots: { customRender: 'type' }
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 160,
      scopedSlots: { customRender: 'action' },
      align: 'center'
    }
  ]

  export default {
    name: 'MachineProxy',
    components: {
      AddMachineProxyModal
    },
    data: function() {
      return {
        query: {
          host: null,
          port: null,
          username: null,
          type: null
        },
        rows: [],
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
        columns
      }
    },
    methods: {
      getList(page = this.pagination) {
        this.loading = true
        this.$api.getMachineProxyList({
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
      remove(id) {
        this.$confirm({
          title: '确认删除',
          content: '是否删除当前代理? 将会清除是所有关联机器的代理!',
          okText: '确认',
          okType: 'danger',
          cancelText: '取消',
          onOk: () => {
            this.$api.deleteMachineProxy({
              idList: [id]
            }).then(() => {
              this.$message.success('删除成功')
              this.getList()
            })
          }
        })
      },
      batchRemove() {
        this.$confirm({
          title: '确认删除',
          content: '是否删除选中代理? 将会清除是所有关联机器的代理!',
          okText: '确认',
          okType: 'danger',
          cancelText: '取消',
          onOk: () => {
            this.$api.deleteMachineProxy({
              idList: this.selectedRowKeys
            }).then(() => {
              this.$message.success('删除成功')
              this.getList()
            })
            this.selectedRowKeys = []
          }
        })
      },
      add() {
        this.$refs.addModal.add()
      },
      update(record) {
        this.$refs.addModal.update(record)
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

  .machine-proxy-column {
    margin: 12px 12px 0 12px;
  }

  .machine-proxy-tools-bar {
    margin-bottom: 12px;
  }

  .machine-proxy-tools-bar > button {
    margin-right: 8px;
  }

  .machine-proxy-search-form /deep/ .ant-form-item {
    display: flex;
  }

  .machine-proxy-search-form /deep/ .ant-form-item-control-wrapper {
    flex: 0.8;
  }

</style>
