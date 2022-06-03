<template>
  <a-modal v-model="visible"
           :width="1024"
           :dialogStyle="{top: '64px'}"
           :bodyStyle="{padding: '8px'}"
           :maskClosable="false"
           :destroyOnClose="true"
           @cancel="close">
    <!-- 历史值表格 -->
    <div class="table-main-container table-scroll-x-auto">
      <a-table :columns="columns"
               :dataSource="rows"
               :pagination="pagination"
               rowKey="id"
               @change="getList"
               :scroll="{x: '100%'}"
               :loading="loading"
               size="middle">
        <!-- beforeValue -->
        <template v-slot:beforeValue="record">
          <div class="auto-ellipsis">
            <a class="copy-icon-left" v-if="record.beforeValue" @click="$copy(record.beforeValue)">
              <a-icon type="copy"/>
            </a>
            <span class="pointer auto-ellipsis-item" title="预览" @click="preview(record.beforeValue)">
            {{ record.beforeValue }}
          </span>
          </div>
        </template>
        <!-- afterValue -->
        <template v-slot:afterValue="record">
          <div class="auto-ellipsis">
            <a class="copy-icon-left" @click="$copy(record.afterValue)">
              <a-icon type="copy"/>
            </a>
            <span class="pointer auto-ellipsis-item" title="预览" @click="preview(record.afterValue)">
            {{ record.afterValue }}
          </span>
          </div>
        </template>
        <!-- 类型 -->
        <template v-slot:type="record">
          <a-tag class="m0" :color="$enum.valueOf($enum.HISTORY_VALUE_OPTION_TYPE, record.type).color">
            {{ $enum.valueOf($enum.HISTORY_VALUE_OPTION_TYPE, record.type).label }}
          </a-tag>
        </template>
        <!-- 修改时间 -->
        <template v-slot:createTime="record">
          {{ record.createTime | formatDate }} ({{ record.createTimeAgo }})
        </template>
        <!-- 操作 -->
        <template v-slot:action="record">
          <a @click="rollback(record)">回滚</a>
        </template>
      </a-table>
    </div>
    <!-- 历史值表格 -->
    <div class="history-event">
      <!-- 预览框 -->
      <TextPreview ref="preview"/>
    </div>
    <!-- 头部 -->
    <template #title>
      <span>历史记录</span>
      <span class="span-blue" style="margin-left: 8px">{{ env.key }}</span>
      <a @click="$copy(env.key)">
        <a-icon class="copy-icon-right" type="copy"/>
      </a>
    </template>
    <!-- 底部 -->
    <template #footer>
      <a-button @click="close">关闭</a-button>
    </template>
  </a-modal>
</template>

<script>

import TextPreview from '@/components/preview/TextPreview'
import _filters from '@/lib/filters'

const columns = [
  {
    title: '序号',
    key: 'seq',
    width: 60,
    align: 'center',
    customRender: (text, record, index) => `${index + 1}`
  },
  {
    title: 'beforeValue',
    key: 'beforeValue',
    width: 200,
    ellipsis: true,
    sorter: (a, b) => (a.beforeValue || '').localeCompare(b.beforeValue || ''),
    scopedSlots: { customRender: 'beforeValue' }
  },
  {
    title: 'afterValue',
    key: 'afterValue',
    width: 200,
    ellipsis: true,
    sorter: (a, b) => (a.afterValue || '').localeCompare(b.afterValue || ''),
    scopedSlots: { customRender: 'afterValue' }
  },
  {
    title: '类型',
    key: 'type',
    width: 80,
    align: 'center',
    scopedSlots: { customRender: 'type' }
  },
  {
    title: '修改人',
    key: 'updateUserName',
    dataIndex: 'updateUserName',
    width: 100,
    align: 'center'
  },
  {
    title: '修改时间',
    key: 'createTime',
    width: 220,
    align: 'center',
    sorter: (a, b) => a.createTime - b.createTime,
    scopedSlots: { customRender: 'createTime' }
  },
  {
    title: '操作',
    key: 'action',
    width: 90,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'EnvHistoryModal',
  components: {
    TextPreview
  },
  data: function() {
    return {
      loading: false,
      visible: false,
      env: {},
      rows: [],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showTotal: function(total) {
          return `共 ${total} 条`
        }
      },
      columns
    }
  },
  methods: {
    open(env) {
      this.env = env
      this.visible = true
      this.getList({})
    },
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getHistoryValueList({
        valueId: this.env.valueId,
        valueType: this.env.valueType,
        page: page.current,
        limit: page.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
        this.rows = data.rows || []
        this.pagination = pagination
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    rollback(record) {
      var updateValue
      switch (record.type) {
        case this.$enum.HISTORY_VALUE_OPTION_TYPE.INSERT.value:
          updateValue = record.afterValue
          break
        default:
          updateValue = record.beforeValue
      }
      this.$confirm({
        title: '确认回滚',
        content: `是否回滚值为 ${updateValue}`,
        okText: '确认',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          this.loading = true
          this.$api.rollbackHistoryValue({
            id: record.id
          }).then(() => {
            this.loading = false
            this.$message.success('已回滚')
            this.getList({})
            this.$emit('rollback', record.id, updateValue)
          }).catch(() => {
            this.loading = false
          })
        }
      })
    },
    preview(value) {
      this.$refs.preview.preview(value)
    },
    close() {
      this.loading = false
      this.visible = false
      this.env = {}
    }
  },
  filters: {
    ..._filters
  }
}
</script>

<style scoped>

</style>
