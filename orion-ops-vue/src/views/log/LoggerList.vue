<template>
  <div class="log-list-container">
    <!-- 筛选列 -->
    <div class="table-search-columns">
      <a-form-model class="log-list-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="机器" prop="machine">
              <MachineSelector ref="machineSelector" @change="machineId => query.machineId = machineId"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="名称" prop="name">
              <a-input v-model="query.name"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="路径" prop="path">
              <a-input v-model="query.path"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="命令" prop="command">
              <a-input v-model="query.command"/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 工具 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <span class="table-title">日志列表</span>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a target="_blank" href="#/log/view">
          <a-button class="mr8" type="primary" icon="file-text">打开日志</a-button>
        </a>
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
        <!-- 主机 -->
        <div slot="machine" slot-scope="record">
          <a-tooltip placement="top">
            <template slot="title">
              <span>{{ `${record.machineName} (${record.machineHost})` }}</span>
            </template>
            <span>{{ record.machineName }}</span>
          </a-tooltip>
        </div>
        <!-- 路径 -->
        <span class="pointer" slot="path" slot-scope="record" title="预览" @click="previewText(record.path)">
            {{ record.path }}
        </span>
        <!-- 命令 -->
        <span class="pointer" slot="command" slot-scope="record" title="预览" @click="previewText(record.command)">
            {{ record.command }}
        </span>
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
          <!-- 打开 -->
          <a target="_blank" :href="`#/log/view/${record.id}`">打开</a>
          <a-divider type="vertical"/>
          <!-- 修改 -->
          <a @click="update(record.id)">修改</a>
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
      <!-- 文本预览 -->
      <TextPreview ref="previewText"/>
      <!-- 添加模态框 -->
      <AddLogFileModal ref="addModal" @added="getList({})" @updated="getList({})"/>
    </div>
  </div>
</template>

<script>

import MachineSelector from '@/components/machine/MachineSelector'
import AddLogFileModal from '@/components/log/AddLogFileModal'
import TextPreview from '@/components/preview/TextPreview'
import _utils from '@/lib/utils'

/**
 * 列
 */
const columns = [
  {
    title: '序号',
    key: 'seq',
    customRender: (text, record, index) => `${index + 1}`,
    width: 65,
    align: 'center'
  },
  {
    title: '机器',
    key: 'machine',
    width: 180,
    ellipsis: true,
    scopedSlots: { customRender: 'machine' },
    sorter: (a, b) => a.machineName.localeCompare(b.machineName)
  },
  {
    title: '名称',
    key: 'name',
    dataIndex: 'name',
    ellipsis: true,
    width: 160,
    sorter: (a, b) => a.name.localeCompare(b.name)
  },
  {
    title: '路径',
    key: 'path',
    ellipsis: true,
    scopedSlots: { customRender: 'path' },
    width: 270,
    sorter: (a, b) => a.path.localeCompare(b.path)
  },
  {
    title: '命令',
    key: 'command',
    ellipsis: true,
    scopedSlots: { customRender: 'command' },
    width: 230
  },
  {
    title: '偏移量(行)',
    key: 'offset',
    dataIndex: 'offset',
    width: 110,
    sorter: (a, b) => a.offset - b.offset
  },
  {
    title: '编码',
    key: 'charset',
    dataIndex: 'charset',
    width: 100,
    align: 'center'
  },
  {
    title: '修改时间',
    key: 'updateTime',
    width: 150,
    ellipsis: true,
    sorter: (a, b) => a.updateTime - b.updateTime,
    scopedSlots: { customRender: 'updateTime' }
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 170,
    scopedSlots: { customRender: 'action' },
    align: 'center'
  }
]

export default {
  name: 'LoggerList',
  components: {
    MachineSelector,
    AddLogFileModal,
    TextPreview
  },
  data() {
    return {
      query: {
        machineId: undefined,
        name: null,
        path: null,
        command: null
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
      columns
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getTailList({
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
    add() {
      this.$refs.addModal.add()
    },
    update(id) {
      this.$refs.addModal.update(id)
    },
    remove(id) {
      this.$api.deleteTailFile({ id })
        .then(() => {
          this.getList({})
        })
    },
    previewText(value) {
      this.$refs.previewText.preview(value)
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.machineSelector.reset()
      this.query.machineId = undefined
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