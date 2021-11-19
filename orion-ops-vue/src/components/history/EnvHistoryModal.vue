<template>
  <a-modal v-model="visible"
           :width="'80%'"
           @cancel="close">
    <div>
      <a-table :columns="columns"
               :dataSource="rows"
               :pagination="pagination"
               :loading="loading"
               rowKey="id"
               @change="getList"
               size="small">
        <!-- beforeValue -->
        <div slot="beforeValue" slot-scope="record">
          <a class="copy-icon-left" v-if="record.beforeValue" @click="$copy(record.beforeValue)">
            <a-icon type="copy"/>
          </a>
          <span>{{ record.beforeValue }}</span>
        </div>
        <!-- afterValue -->
        <div slot="afterValue" slot-scope="record">
          <a class="copy-icon-left" @click="$copy(record.afterValue)">
            <a-icon type="copy"/>
          </a>
          <span>{{ record.afterValue }}</span>
        </div>
        <!-- 类型 -->
        <a-tag slot="type" slot-scope="record"
               style="margin: 0"
               :color="$enum.valueOf($enum.HISTORY_VALUE_TYPE, record.type).color">
          {{ $enum.valueOf($enum.HISTORY_VALUE_TYPE, record.type).label }}
        </a-tag>
        <!-- 修改时间 -->
        <div slot="createTime" slot-scope="record">
          <span>{{
              record.createTime | formatDate({
                date: record.createTime,
                pattern: 'yyyy-MM-dd HH:mm:ss'
              })
            }}</span>
          |
          <span>{{ record.createTimeAgo }}</span>
        </div>
        <!-- 操作 -->
        <div slot="action" slot-scope="record">
          <a @click="rollback(record)">回滚</a>
        </div>
      </a-table>
    </div>
    <!-- 头部 -->
    <div slot="title">
      <span>历史记录</span>
      <span class="span-blue" style="margin-left: 8px">{{ env.key }}</span>
      <a @click="$copy(env.key)">
        <a-icon class="copy-icon-right" type="copy"/>
      </a>
    </div>
    <!-- 底部 -->
    <a-button slot="footer" @click="close">关闭</a-button>
  </a-modal>
</template>

<script>

import _utils from '@/lib/utils'

const columns = [
  {
    title: '序号',
    key: 'seq',
    customRender: (text, record, index) => `${index + 1}`,
    width: 60,
    align: 'center'
  },
  {
    title: 'beforeValue',
    key: 'beforeValue',
    scopedSlots: { customRender: 'beforeValue' },
    width: 200,
    ellipsis: true,
    sorter: (a, b) => a.beforeValue.localeCompare(b.beforeValue)
  },
  {
    title: 'afterValue',
    key: 'afterValue',
    scopedSlots: { customRender: 'afterValue' },
    width: 200,
    ellipsis: true,
    sorter: (a, b) => a.afterValue.localeCompare(b.afterValue)
  },
  {
    title: '类型',
    key: 'type',
    scopedSlots: { customRender: 'type' },
    width: 80,
    align: 'center'
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
    scopedSlots: { customRender: 'createTime' },
    sorter: (a, b) => a.createTime - b.createTime,
    width: 150,
    align: 'center'
  },
  {
    title: '操作',
    key: 'action',
    width: 80,
    scopedSlots: { customRender: 'action' },
    align: 'center'
  }
]

export default {
  name: 'EnvHistoryModal',
  props: {
    env: Object
  },
  data: function() {
    return {
      visible: false,
      loading: false,
      rows: [],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showTotal: function(total) {
          return `共 ${total}条`
        }
      },
      columns
    }
  },
  watch: {
    'env.valueId'(e) {
      if (e) {
        this.getList({})
      }
    }
  },
  methods: {
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
        this.rows = data.rows
        this.pagination = pagination
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    rollback(record) {
      const updateValue = record.beforeValue || record.afterValue
      this.$confirm({
        title: '确认回滚',
        content: `是否回滚值为 ${updateValue}`,
        okText: '确认',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          this.$api.rollbackHistoryValue({
            id: record.id
          }).then(() => {
            this.$message.success('已回滚')
            this.getList({})
            this.$emit('rollback', record.id, updateValue)
          })
        }
      })
    },
    close() {
      this.visible = false
      this.env.valueId = null
    }
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

<style scoped>

</style>
