<template>
  <div class="machine-key-container">
    <!-- 搜索框 -->
    <div class="table-search-columns">
      <a-form-model class="machine-key-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="6">
            <a-form-model-item label="秘钥名称" prop="name">
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
          <span class="table-title">机器秘钥</span>
          <a-divider v-show="selectedRowKeys.length" type="vertical"/>
          <div v-show="selectedRowKeys.length">
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
          <template #path="record">
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
          <!-- 创建时间 -->
          <template #createTime="record">
            {{ record.createTime | formatDate }}
          </template>
          <!-- 操作 -->
          <template #action="record">
            <!-- 修改 -->
            <MachineChecker :ref="`machineChecker${record.id}`" placement="bottomRight">
              <template #trigger>
                <a>绑定机器</a>
              </template>
              <template #footer>
                <a-button type="primary" size="small" @click="chooseRelMachines(record.id)">确定</a-button>
              </template>
            </MachineChecker>
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
    </div>
    <!-- 事件 -->
    <div class="machine-key-event">
      <!-- 新建模态框 -->
      <AddMachineKeyModal ref="addModal" @added="getList({})" @updated="getList({})"/>
    </div>
  </div>
</template>

<script>
import { defineArrayKey } from '@/lib/utils'
import { FILE_DOWNLOAD_TYPE } from '@/lib/enum'
import { formatDate } from '@/lib/filters'
import AddMachineKeyModal from '@/components/machine/AddMachineKeyModal'
import MachineChecker from '@/components/machine/MachineChecker'

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
    width: 190,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'MachineKey',
  components: {
    MachineChecker,
    AddMachineKeyModal
  },
  data: function() {
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
        defineArrayKey(data.rows, 'downloadUrl')
        this.rows = data.rows || []
        this.pagination = pagination
        this.loading = false
        this.selectedRowKeys = []
      }).catch(() => {
        this.loading = false
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
    chooseRelMachines(id) {
      const ref = this.$refs[`machineChecker${id}`]
      const checkedList = ref.checkedList
      if (!checkedList || !checkedList.length) {
        this.$message.warning('请选择绑定秘钥的机器')
        return
      }
      ref.hide()
      ref.clear()
      this.$api.bindMachineKey({
        id,
        machineIdList: checkedList
      }).then(() => {
        this.$message.success('绑定成功')
      })
    },
    add() {
      this.$refs.addModal.add()
    },
    update(id) {
      this.$refs.addModal.update(id)
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.getList({})
    },
    async loadDownloadUrl(record) {
      try {
        const downloadUrl = await this.$api.getFileDownloadToken({
          type: FILE_DOWNLOAD_TYPE.SECRET_KEY.value,
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
    }
  },
  filters: {
    formatDate
  },
  mounted() {
    this.getList({})
  }
}
</script>

<style scoped>

</style>
