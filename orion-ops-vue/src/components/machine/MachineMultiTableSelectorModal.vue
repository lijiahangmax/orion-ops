<template>
  <a-modal v-model="visible"
           title="机器列表"
           width="75%"
           :dialogStyle="{top: '48px'}"
           :bodyStyle="{padding: '8px'}"
           :okButtonProps="{props: {disabled: selectedRowKeys.length === 0}}"
           @ok="choose"
           @cancel="close">
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
  </a-modal>
</template>

<script>

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
  name: 'MachineMultiTableSelectorModal',
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
        this.getList({})
      }
    },
    close() {
      this.visible = false
    },
    getList() {
      this.loading = true
      this.$api.getMachineList({
        ...this.query,
        page: 1,
        limit: 10000
      }).then(({ data }) => {
        this.rows = data.rows || []
        this.visibleRows = this.rows
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    changePage(page) {
      const pagination = { ...this.pagination }
      pagination.current = page.current
      this.pagination = pagination
    },
    doFilterMachine(e) {
      const value = e.target.value
      this.visibleRows = this.rows.filter(machine => {
        if (value) {
          return machine.name.includes(value) || machine.tag.includes(value) || machine.host.includes(value)
        } else {
          return true
        }
      })
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

::v-deep .ant-table-row-cell-ellipsis {
  padding: 8px !important;
}
</style>
