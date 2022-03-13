<template>
  <div class="machine-key-container">
    <!-- 搜索框 -->
    <div class="table-search-columns">
      <a-form-model class="machine-key-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="秘钥名称" prop="name">
              <a-input v-model="query.name" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="描述" prop="description">
              <a-input v-model="query.description" allowClear/>
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
        <span class="table-title">机器秘钥</span>
        <a-divider v-show="selectedRowKeys.length" type="vertical"/>
        <div v-show="selectedRowKeys.length">
          <!-- 挂载 -->
          <a-popconfirm title="是否挂载选中秘钥?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="batchMount">
            <a-button class="ml8" type="primary" icon="pull-request">挂载</a-button>
          </a-popconfirm>
          <!-- 卸载 -->
          <a-popconfirm title="是否卸载选中秘钥? 可能会导致机器无法连接!"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="batchDump">
            <a-button class="ml8" type="danger" icon="poweroff">卸载</a-button>
          </a-popconfirm>
          <!-- 删除 -->
          <a-popconfirm title="确认删除选中秘钥?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="remove(selectedRowKeys)">
            <a-button class="ml8" type="danger" icon="delete">删除</a-button>
          </a-popconfirm>
        </div>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <!-- 临时挂载 -->
        <a-button class="mr8" type="primary" @click="tempMount">临时挂载</a-button>
        <!-- 挂载全部 -->
        <a-popconfirm title="是否挂载全部秘钥?"
                      placement="topRight"
                      ok-text="确定"
                      cancel-text="取消"
                      @confirm="mountAll">
          <a-button class="mr8" type="primary">挂载全部</a-button>
        </a-popconfirm>
        <!-- 卸载全部 -->
        <a-popconfirm title="是否卸载全部秘钥? 可能会导致机器无法连接!"
                      placement="topRight"
                      ok-text="确定"
                      cancel-text="取消"
                      @confirm="dumpAll">
          <a-button class="mr8" type="primary">卸载全部</a-button>
        </a-popconfirm>
        <a-divider type="vertical"/>
        <a-button class="mx8" type="primary" icon="plus" @click="add">新建</a-button>
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
               :rowSelection="{selectedRowKeys, onChange: e => selectedRowKeys = e}"
               rowKey="id"
               @change="getList"
               :scroll="{x: '100%'}"
               :loading="loading"
               size="middle">
        <!-- 秘钥路径 -->
        <template v-slot:path="record">
          <a @click="loadDownloadUrl(record)" title="获取下载链接">{{ record.path }}</a>
          <a v-if="record.downloadUrl"
             target="_blank"
             title="下载"
             style="margin-left: 10px"
             :href="record.downloadUrl"
             @click="clearDownloadUrl(record)">
            <a-icon type="download"/>
          </a>
        </template>
        <!-- 挂载状态 -->
        <template v-slot:mountStatus="record">
          <a-tag :color="$enum.valueOf($enum.MACHINE_KEY_MOUNT_STATUS, record.mountStatus).color">
            {{ $enum.valueOf($enum.MACHINE_KEY_MOUNT_STATUS, record.mountStatus).label }}
          </a-tag>
        </template>
        <!-- 创建时间 -->
        <template v-slot:createTime="record">
          {{ record.createTime | formatDate }}
        </template>
        <!-- 操作 -->
        <template v-slot:action="record">
          <!-- 挂载 -->
          <a-button v-if="record.mountStatus === 1" type="link" disabled style="height: 22px; padding: 0">
            挂载
          </a-button>
          <a-popconfirm v-else
                        :title="record.mountStatus === 2 ? '是否卸载当前秘钥? 可能会导致机器无法连接!' : '是否挂载当前秘钥?'"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="changeMountStatus(record.id, record.mountStatus)">
            <span class="span-blue pointer" v-text="record.mountStatus === 2 ? '卸载' : '挂载'"/>
          </a-popconfirm>
          <a-divider type="vertical"/>
          <!-- 修改 -->
          <a @click="update(record.id)">修改</a>
          <a-divider type="vertical"/>
          <!-- 删除 -->
          <a-popconfirm title="确认删除当前秘钥?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="remove([record.id])">
            <span class="span-blue pointer">删除</span>
          </a-popconfirm>
        </template>
      </a-table>
    </div>
    <!-- 事件 -->
    <div class="machine-key-event">
      <!-- 新建模态框 -->
      <AddMachineKeyModal ref="addModal" @added="getList({})" @updated="getList({})"/>
      <!-- 临时挂载模态框 -->
      <TempMountMachineKeyModal ref="tempMountModal"/>
    </div>
  </div>
</template>

<script>
import AddMachineKeyModal from '@/components/machine/AddMachineKeyModal'
import TempMountMachineKeyModal from '@/components/machine/TempMountMachineKeyModal'
import _filters from '@/lib/filters'

/**
 * 列
 */
const columns = [
  {
    title: '秘钥名称',
    dataIndex: 'name',
    key: 'name',
    width: 200,
    ellipsis: true,
    sorter: (a, b) => a.name.localeCompare(b.name)
  },
  {
    title: '秘钥路径',
    key: 'path',
    width: 265,
    scopedSlots: { customRender: 'path' }
  },
  {
    title: '挂载状态',
    key: 'mountStatus',
    width: 100,
    scopedSlots: { customRender: 'mountStatus' }
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true,
    width: 200
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 150,
    align: 'center',
    sorter: (a, b) => a.createTime - b.createTime,
    scopedSlots: { customRender: 'createTime' }
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 160,
    align: 'center',
    scopedSlots: { customRender: 'action' }
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
          return `共 ${total} 条`
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
        this.rows = data.rows || []
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
          type: this.$enum.FILE_DOWNLOAD_TYPE.SECRET_KEY.value,
          id: record.id
        })
        record.downloadUrl = this.$api.fileDownloadExec({ token: downloadUrl.data })
      } catch (e) {
        // ignore
      }
    },
    clearDownloadUrl(record) {
      setTimeout(() => {
        record.downloadUrl = null
      })
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
      this.$api.mountMachineKey({
        idList: this.selectedRowKeys
      }).then(() => {
        this.$message.success('挂载成功')
        this.getList()
      }).catch(() => {
        this.$message.error('挂载失败')
      })
    },
    mountAll() {
      this.$api.mountAllMachineKey()
        .then(() => {
          this.$message.success('挂载成功')
          this.getList()
        }).catch(() => {
        this.$message.error('挂载失败')
      })
    },
    batchDump() {
      this.$api.dumpMachineKey({
        idList: this.selectedRowKeys
      }).then(() => {
        this.$message.success('卸载成功')
        this.getList()
      }).catch(() => {
        this.$message.error('卸载失败')
      })
    },
    dumpAll() {
      this.$api.dumpAllMachineKey()
        .then(() => {
          this.$message.success('卸载成功')
          this.getList()
        }).catch(() => {
        this.$message.error('卸载失败')
      })
    },
    remove(idList) {
      this.$api.removeMachineKey({
        idList
      }).then(() => {
        this.$message.success('删除成功')
        this.getList({})
      })
    },
    tempMount() {
      this.$refs.tempMountModal.add()
    },
    add() {
      this.$refs.addModal.add()
    },
    update(id) {
      this.$refs.addModal.update(id)
    }
  },
  filters: {
    ..._filters
  },
  mounted() {
    this.getList({})
  }
}
</script>

<style scoped>

</style>
