<template>
  <div class="machine-key-container">
    <!-- 搜索框 -->
    <div class="table-search-columns">
      <a-form-model class="machine-key-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="秘钥名称" prop="name">
              <a-input v-model="query.name"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="描述" prop="description">
              <a-input v-model="query.description"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="挂载状态" prop="mountStatus">
              <a-select v-model="query.mountStatus" placeholder="全部" allowClear>
                <a-select-option :value="type.value" v-for="type in $enum.MACHINE_KEY_MOUNT_STATUS" :key="type.value">
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
        <span class="table-title">代理列表</span>
        <a-divider v-show="selectedRowKeys.length" type="vertical"/>
        <a-button-group class="ml8" v-show="selectedRowKeys.length">
          <a-button type="primary" icon="pull-request" @click="batchMount()">挂载</a-button>
          <a-button type="danger" icon="poweroff" @click="batchDump()">卸载</a-button>
          <a-button type="danger" icon="delete" @click="batchRemove()">删除</a-button>
        </a-button-group>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a-button-group class="mr8">
          <a-button type="primary" @click="tempMount">临时挂载</a-button>
          <a-button type="primary" @click="mountAll">挂载全部</a-button>
          <a-button type="primary" @click="dumpAll">卸载全部</a-button>
        </a-button-group>
        <a-divider type="vertical"/>
        <a-button class="mx8" type="primary" icon="plus" @click="add">新建</a-button>
        <a-divider type="vertical"/>
        <a-icon type="search" class="tools-icon" title="查询" @click="getList({})"/>
        <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
      </div>
    </div>
    <!-- 表格 -->
    <div class="table-main-container">
      <a-table :columns="columns"
               :dataSource="rows"
               :pagination="pagination"
               :rowSelection="{selectedRowKeys, onChange: e => selectedRowKeys = e}"
               :loading="loading"
               rowKey="id"
               @change="getList"
               size="middle">
        <!-- 秘钥路径 -->
        <div slot="path" slot-scope="record">
          <a @click="loadDownloadUrl(record)">{{ record.path }}</a>
          <a v-if="record.downloadUrl" :href="record.downloadUrl" @click="clearDownloadUrl(record)" style="margin-left: 10px">
            <a-icon type="download"/>
          </a>
        </div>
        <!-- 创建时间 -->
        <span slot="createTime" slot-scope="record">
          {{
            record.createTime | formatDate({
              date: record.createTime,
              pattern: 'yyyy-MM-dd HH:mm:ss'
            })
          }}
        </span>
        <!-- 挂载状态 -->
        <div slot="mountStatus" slot-scope="record">
          <!-- 未挂载 -->
          <a-tag v-if="record.mountStatus === 1"
                 :color="$enum.valueOf($enum.MACHINE_KEY_MOUNT_STATUS, record.mountStatus).color">
            {{ $enum.valueOf($enum.MACHINE_KEY_MOUNT_STATUS, record.mountStatus).label }}
          </a-tag>
          <!-- 可操作 -->
          <a-popconfirm v-else
                        :title="record.mountStatus === 2 ?
                        '是否卸载当前秘钥? 可能会导致机器无法连接!' : '是否挂载当前秘钥?'"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="changeMountStatus(record.id, record.mountStatus)">
            <a-tag style="margin: 0;cursor: pointer"
                   :color="$enum.valueOf($enum.MACHINE_KEY_MOUNT_STATUS, record.mountStatus).color">
              {{ $enum.valueOf($enum.MACHINE_KEY_MOUNT_STATUS, record.mountStatus).label }}
            </a-tag>
          </a-popconfirm>
        </div>

        <!-- 操作 -->
        <div slot="action" slot-scope="record">
          <!-- 修改 -->
          <a @click="update(record)">修改</a>
          <a-divider type="vertical"/>
          <!-- 删除 -->
          <a-popconfirm title="确认删除当前秘钥?"
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
    <div class="machine-key-event">
      <!-- 新建模态框 -->
      <AddMachineKeyModal ref="addModal" @added="getList({})" @updated="getList()"/>
      <!-- 临时挂载模态框 -->
      <TempMountMachineKeyModal ref="tempMountModal"/>
    </div>
  </div>
</template>

<script>
import _utils from '@/lib/utils'
import AddMachineKeyModal from '@/components/machine/AddMachineKeyModal'
import TempMountMachineKeyModal from '@/components/machine/TempMountMachineKeyModal'

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
    title: '秘钥名称',
    dataIndex: 'name',
    key: 'name',
    width: 200,
    sorter: (a, b) => a.name.localeCompare(b.name)
  },
  {
    title: '秘钥路径',
    key: 'path',
    scopedSlots: { customRender: 'path' },
    width: 300
  },
  {
    title: '创建时间',
    key: 'createTime',
    sorter: (a, b) => a.createTime - b.createTime,
    scopedSlots: { customRender: 'createTime' },
    width: 180
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    width: 200
  },
  {
    title: '挂载状态',
    key: 'mountStatus',
    width: 100,
    scopedSlots: { customRender: 'mountStatus' }
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
  name: 'MachineKey',
  components: {
    AddMachineKeyModal,
    TempMountMachineKeyModal
  },
  data: function() {
    return {
      query: {
        name: null,
        description: null,
        mountStatus: undefined
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
      this.$api.getMachineKeyList({
        ...this.query,
        page: page.current,
        limit: page.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
        // 定义下载路径
        this.$utils.defineArrayKey(data.rows, 'downloadUrl')
        this.rows = data.rows
        this.pagination = pagination
        this.loading = false
        this.selectedRowKeys = []
      }).catch(() => {
        this.loading = false
      })
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.getList({})
    },
    async loadDownloadUrl(record) {
      try {
        const downloadUrl = await this.$api.getFileDownloadToken({
          type: 10,
          id: record.id
        })
        record.downloadUrl = this.$api.fileDownloadExec({ token: downloadUrl.data })
      } catch (e) {
        // ignore
      }
    },
    clearDownloadUrl(record) {
      record.downloadUrl = null
    },
    changeMountStatus(id, status) {
      if (status === 2) {
        this.$api.dumpMachineKey({
          idList: [id]
        }).then(() => {
          this.$message.success('卸载成功')
          this.getList()
        }).catch(() => {
          this.$message.error('卸载失败')
        })
      } else if (status === 3) {
        // 未挂载
        this.$api.mountMachineKey({
          idList: [id]
        }).then(() => {
          this.$message.success('挂载成功')
          this.getList()
        }).catch(() => {
          this.$message.error('挂载失败')
        })
      }
    },
    batchMount() {
      this.$confirm({
        title: '确认挂载',
        content: '是否挂载选中秘钥?',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$api.mountMachineKey({
            idList: this.selectedRowKeys
          }).then(() => {
            this.$message.success('挂载成功')
            this.getList()
          }).catch(() => {
            this.$message.error('挂载失败')
          })
          this.selectedRowKeys = []
        }
      })
    },
    mountAll() {
      this.$confirm({
        title: '确认挂载全部',
        content: '是否挂载全部秘钥?',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$api.mountAllMachineKey()
            .then(() => {
              this.$message.success('挂载成功')
              this.getList()
            })
            .catch(() => {
              this.$message.error('挂载失败')
            })
        }
      })
    },
    batchDump() {
      this.$confirm({
        title: '确认卸载全部',
        content: '是否卸载选中秘钥? 可能会导致机器无法连接!',
        okType: 'danger',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$api.dumpMachineKey({
            idList: this.selectedRowKeys
          }).then(() => {
            this.$message.success('卸载成功')
            this.getList()
          }).catch(() => {
            this.$message.error('卸载失败')
          })
          this.selectedRowKeys = []
        }
      })
    },
    dumpAll() {
      this.$confirm({
        title: '确认卸载全部',
        content: '是否卸载全部秘钥? 可能会导致机器无法连接!',
        okType: 'danger',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$api.dumpAllMachineKey()
            .then(() => {
              this.$message.success('卸载成功')
              this.getList()
            })
            .catch(() => {
              this.$message.error('卸载失败')
            })
        }
      })
    },
    remove(id) {
      this.$api.removeMachineKey({
        idList: [id]
      }).then(() => {
        this.$message.success('删除成功')
        this.getList()
      })
    },
    batchRemove() {
      this.$confirm({
        title: '确认删除',
        content: '是否删除选中秘钥?',
        okType: 'danger',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$api.removeMachineKey({
            idList: this.selectedRowKeys
          }).then(() => {
            this.$message.success('删除成功')
            this.getList()
          })
          this.selectedRowKeys = []
        }
      })
    },
    tempMount() {
      this.$refs.tempMountModal.add()
    },
    add() {
      this.$refs.addModal.add()
    },
    update(record) {
      this.$refs.addModal.update(record)
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
