<template>
  <div class="alarm-history-container">
    <!-- 筛选列 -->
    <div class="table-search-columns">
      <a-form-model class="user-list-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="类型" prop="type">
              <a-select v-model="query.type" placeholder="全部" @change="getList({})" allowClear>
                <a-select-option v-for="type in MACHINE_ALARM_TYPE"
                                 :value="type.value"
                                 :key="type.value">
                  {{ type.label }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="报警值" prop="value">
              <a-input-group compact>
                <a-input v-limit-integer
                         class="alarm-value-input"
                         placeholder="最小值"
                         v-model="query.alarmValueStart"/>
                <a-input class="between-input-center" placeholder="~" disabled/>
                <a-input v-limit-integer
                         class="alarm-value-input alarm-value-input-right"
                         placeholder="最大值"
                         v-model="query.alarmValueEnd"/>
              </a-input-group>
            </a-form-model-item>
          </a-col>
          <a-col :span="10">
            <a-form-model-item label="报警时间" prop="time">
              <a-range-picker :showTime="true" v-model="alarmTimeRange" @change="selectedAlarmTime"/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 工具 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <span class="table-title">报警历史 ({{ machineName }})</span>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
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
        <!-- 报警类型 -->
        <template #alarmType="record">
          {{ record.type | formatType('label') }}
        </template>
        <!-- 报警值 -->
        <template #alarmValue="record">
          <span style="color: red">{{ record.alarmValue }}%</span>
        </template>
        <!-- 报警类型 -->
        <template #alarmTime="record">
          {{ record.alarmTime | formatDate }}
          ({{ record.alarmTimeAgo }})
        </template>
        <!-- 操作 -->
        <template #action="record">
          <a-popconfirm title="确定要重新执行通知操作吗?"
                        placement="bottomLeft"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="triggerAlarmNotify(record.id)">
            <span class="span-blue pointer">重新通知</span>
          </a-popconfirm>
        </template>
      </a-table>
    </div>
    <!-- 事件 -->
  </div>
</template>

<script>
import { enumValueOf, MACHINE_ALARM_TYPE } from '@/lib/enum'
import { formatDate } from '@/lib/filters'

/**
 * 列
 */
const columns = [
  {
    title: '序号',
    key: 'seq',
    width: 65,
    align: 'center',
    customRender: (text, record, index) => `${index + 1}`
  },
  {
    title: '报警类型',
    key: 'alarmType',
    width: 160,
    scopedSlots: { customRender: 'alarmType' }
  },
  {
    title: '报警值',
    key: 'alarmValue',
    align: 'center',
    width: 240,
    scopedSlots: { customRender: 'alarmValue' }
  },
  {
    title: '报警时间',
    key: 'alarmTime',
    ellipsis: true,
    scopedSlots: { customRender: 'alarmTime' }
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 140,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'MachineMonitorAlarmHistory',
  props: {
    machineId: Number,
    machineName: String
  },
  data() {
    return {
      MACHINE_ALARM_TYPE,
      query: {
        type: undefined,
        alarmValueStart: undefined,
        alarmValueEnd: undefined,
        alarmTimeStart: undefined,
        alarmTimeEnd: undefined
      },
      rows: [],
      pagination: {
        current: 1,
        pageSize: 15,
        total: 0,
        showTotal: function(total) {
          return `共 ${total} 条`
        }
      },
      loading: false,
      columns: columns,
      alarmTimeRange: undefined
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getMachineAlarmHistory({
        ...this.query,
        machineId: this.machineId,
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
    selectedAlarmTime(moments, dates) {
      this.query.alarmTimeStart = dates[0]
      this.query.alarmTimeEnd = dates[1]
    },
    triggerAlarmNotify(id) {
      this.$api.triggerMachineAlarmNotify({ id }).then(() => {
        this.$message.success('通知完成')
      })
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.alarmTimeRange = undefined
      this.query.alarmValueStart = undefined
      this.query.alarmValueEnd = undefined
      this.query.alarmTimeStart = undefined
      this.query.alarmTimeEnd = undefined
      this.getList({})
    }
  },
  filters: {
    formatDate,
    formatType(type, f) {
      return enumValueOf(MACHINE_ALARM_TYPE, type)[f]
    }
  },
  mounted() {
    this.getList({})
  }
}
</script>

<style lang="less" scoped>
.alarm-history-container {
  padding: 18px;
  overflow: auto;
}

.alarm-value-input {
  width: calc(50% - 15px) !important;
  text-align: center !important;
}

.alarm-value-input-right {
  border-left: 0;
}

</style>
