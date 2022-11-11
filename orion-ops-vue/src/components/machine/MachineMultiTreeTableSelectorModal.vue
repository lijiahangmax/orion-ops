<template>
  <a-modal v-model="visible"
           title="机器列表"
           width="80%"
           :dialogStyle="{top: '48px'}"
           :bodyStyle="{padding: '0'}"
           :okButtonProps="{props: {disabled: selectedRowKeys.length === 0}}"
           @ok="choose"
           @cancel="close">
    <div class="machine-tree-selector-wrapper">
      <!-- 树 -->
      <div class="machine-tree-container">
        <div class="machine-tree-wrapper">
          <MachineEditableTree ref="tree"
                               :machines="rows"
                               :visibleEdit="false"
                               :treeStyle="{
                               'min-height':'216px',
                               'max-height':'calc(100vh - 270px)',
                               'overflow-y':'auto'
                             }"
                               @reloadMachine="reloadMachine"/>
        </div>
      </div>
      <!-- 表格 -->
      <div class="machine-table-wrapper">
        <!-- 搜索列 -->
        <div class="table-tools-bar">
          <!-- 左侧 -->
          <div class="tools-fixed-left">
            <a-input v-if="visible"
                     style="width: 300px"
                     placeholder="输入名称/标识/主机过滤"
                     @change="doFilterMachine"
                     allowClear/>
          </div>
          <!-- 右侧 -->
          <div class="tools-fixed-right">
            <div v-if="selectedRowKeys.length">
              <span>已选择 {{ selectedRowKeys.length }} 台机器</span>
              <span class="span-blue pointer ml8" @click="cancelChoose">取消选择</span>
            </div>
          </div>
        </div>
        <!-- 表格 -->
        <div class="table-main-container table-scroll-y-auto">
          <a-table :columns="columns"
                   :dataSource="visibleRows"
                   :pagination="pagination"
                   :rowSelection="rowSelection"
                   @change="changePage"
                   rowKey="id"
                   :scroll="{x: '100%', y: 'calc(100vh - 378px)'}"
                   :loading="loading"
                   size="middle">
            <!-- 名称 -->
            <template #info="record">
              <div class='machine-info-wrapper'>
                <!-- 机器名称 -->
                <div class="machine-info-name-wrapper">
                  <span class="machine-info-label">名称: </span>
                  <span class="machine-info-name-value">{{ record.name }}</span>
                </div>
                <!-- 唯一标识 -->
                <div class="machine-info-tag-wrapper">
                  <span class="machine-info-label">标识: </span>
                  <span class="machine-info-tag-value">{{ record.tag }}</span>
                </div>
              </div>
            </template>
            <!-- 主机 -->
            <template #host="record">
          <span class="span-blue pointer" title="复制主机" @click="$copy(record.host, true)">
            {{ record.host }}
          </span>
            </template>
          </a-table>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script>

import MachineEditableTree from '@/components/machine/MachineEditableTree'
import { defineArrayKey } from '@/lib/utils'

const columns = [
  {
    title: '机器信息',
    key: 'name',
    ellipsis: true,
    scopedSlots: { customRender: 'info' },
    sorter: (a, b) => a.name.localeCompare(b.name)
  },
  {
    title: '机器主机',
    key: 'host',
    ellipsis: true,
    scopedSlots: { customRender: 'host' },
    sorter: (a, b) => a.host.localeCompare(b.host)
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true
  }
]

export default {
  name: 'MachineMultiTreeTableSelectorModal',
  components: { MachineEditableTree },
  props: {
    query: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    return {
      visible: false,
      rows: [],
      visibleRows: [],
      selectedRowKeys: [],
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
  computed: {
    rowSelection() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        onChange: e => {
          this.selectedRowKeys = e
        }
      }
    }
  },
  methods: {
    open(selected = []) {
      this.selectedRowKeys = [...selected]
      this.visible = true
      if (!this.rows.length) {
        this.$nextTick(() => {
          this.getList({})
        })
      }
    },
    close() {
      this.visible = false
    },
    getList() {
      this.loading = true
      this.$refs.tree.loading = true
      this.$api.getMachineList({
        ...this.query,
        queryGroup: true,
        page: 1,
        limit: 10000
      }).then(({ data }) => {
        const rows = data.rows || []
        defineArrayKey(rows, 'visible', true)
        this.rows = rows
        this.loading = false
      }).then(() => {
        this.$refs.tree.initData()
      }).catch(() => {
        this.loading = false
        this.$refs.tree.loading = false
      })
    },
    reloadMachine(machines) {
      this.visibleRows = machines
    },
    changePage(page) {
      const pagination = { ...this.pagination }
      pagination.current = page.current
      this.pagination = pagination
    },
    doFilterMachine(e) {
      const value = e.target.value
      this.rows.forEach(machine => {
        if (value) {
          machine.visible = machine.name.includes(value) || machine.tag.includes(value) || machine.host.includes(value)
        } else {
          machine.visible = true
        }
      })
      // 计算数量
      this.$refs.tree.reloadMachineCount()
      // 重新加载机器
      this.$refs.tree.loadMachines(this.$refs.tree.selectedTreeNode[0])
    },
    cancelChoose() {
      this.selectedRowKeys = []
    },
    choose() {
      this.close()
      this.$emit('choose', this.selectedRowKeys)
    }
  }
}
</script>

<style lang="less" scoped>

.machine-tree-selector-wrapper {
  display: flex;
  padding: 8px 0 8px 16px;
}

.machine-tree-container {
  width: 25%;
  padding-right: 8px;
  background: #F0F0F0;
}

.machine-tree-wrapper {
  height: 100%;
  padding-right: 16px;
  background: #FFF;
}

.machine-table-wrapper {
  width: 75%;
}

::v-deep .ant-table-row-cell-ellipsis {
  padding: 8px !important;
}
</style>
