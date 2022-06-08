<template>
  <div class="scheduler-list-container">
    <!-- 表格 -->
    <div class="scheduler-table-container">
      <!-- 搜索列 -->
      <div class="table-search-columns">
        <a-form-model class="scheduler-search-form" ref="query" :model="query">
          <a-row>
            <a-col :span="5">
              <a-form-model-item label="名称" prop="username">
                <a-input v-model="query.name" allowClear/>
              </a-form-model-item>
            </a-col>
            <a-col :span="5">
              <a-form-model-item label="描述" prop="description">
                <a-input v-model="query.description" allowClear/>
              </a-form-model-item>
            </a-col>
            <a-col :span="5">
              <a-form-model-item label="状态" prop="type">
                <a-select v-model="query.latelyStatus" placeholder="全部" allowClear>
                  <a-select-option :value="type.value" v-for="type in $enum.SCHEDULER_TASK_STATUS" :key="type.value">
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
          <span class="table-title">任务列表</span>
        </div>
        <!-- 右侧 -->
        <div class="tools-fixed-right">
          <!-- 状态单选 -->
          <div class="mr8 nowrap">
            <a-radio-group v-model="query.enableStatus" @change="getList({})">
              <a-radio-button :value="undefined">
                全部
              </a-radio-button>
              <a-radio-button :value="type.value" v-for="type in $enum.ENABLE_STATUS" :key="type.value">
                {{ type.label }}
              </a-radio-button>
            </a-radio-group>
          </div>
          <a-divider type="vertical"/>
          <a-button class="mx8" type="primary" icon="plus" @click="add">新增</a-button>
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
          <!-- 最近状态 -->
          <template v-slot:latelyStatus="record">
            <template v-if="record.enableStatus === 1">
              <a-tag :color="$enum.valueOf($enum.SCHEDULER_TASK_STATUS, record.latelyStatus).color">
                {{ $enum.valueOf($enum.SCHEDULER_TASK_STATUS, record.latelyStatus).label }}
              </a-tag>
            </template>
            <template v-else>
              <a-tag>停用</a-tag>
            </template>
          </template>
          <!-- 上次调度时间 -->
          <template v-slot:latelyScheduleTime="record">
            <template v-if="record.latelyScheduleTime">
              {{ record.latelyScheduleTime | formatDate }}
            </template>
          </template>
          <!-- 下次调度时间 -->
          <template v-slot:nextTime="record">
            <template v-if="record.nextTime && record.nextTime.length">
              {{ record.nextTime[0] | formatDate }}
            </template>
          </template>
          <!-- 更新时间 -->
          <template v-slot:updateTime="record">
            {{ record.updateTime | formatDate }}
          </template>
          <!-- 操作 -->
          <template v-slot:action="record">
            <!-- 修改 -->
            <a @click="update(record.id)">修改</a>
            <a-divider type="vertical"/>
            <!-- 启用/停用 -->
            <a-popconfirm :title="`是否要${record.enableStatus === 1 ? '停用' : '启用'}此任务?`"
                          placement="topRight"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="updateStatus(record)">
              <span class="span-blue pointer" v-if="record.enableStatus === 1">停用</span>
              <span class="span-blue pointer" v-else>启用</span>
            </a-popconfirm>
            <a-divider type="vertical"/>
            <a-dropdown>
              <a class="ant-dropdown-link">
                更多
                <a-icon type="down"/>
              </a>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="remove(record.id)">
                    删除
                  </a-menu-item>
                  <a-menu-item :disabled="record.enableStatus !== 1" @click="manualTrigger(record.id)">
                    手动触发
                  </a-menu-item>
                  <a-menu-item @click="historyRecord(record.id)">
                    历史记录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>
        </a-table>
      </div>
    </div>
    <!-- 事件 -->
    <div class="scheduler-event-container">
      <AddSchedulerTask ref="addModal" @added="getList()" @updated="getList()"/>
    </div>
  </div>
</template>

<script>
import { formatDate } from '@/lib/filters'
import AddSchedulerTask from '@/components/scheduler/AddSchedulerTask'

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
    title: '任务名称',
    dataIndex: 'name',
    key: 'name',
    ellipsis: true
  },
  {
    title: 'cron表达式',
    dataIndex: 'expression',
    key: 'expression',
    ellipsis: true
  },
  {
    title: '最近状态',
    key: 'latelyStatus',
    width: 120,
    align: 'center',
    sorter: (a, b) => a.latelyStatus - b.latelyStatus,
    scopedSlots: { customRender: 'latelyStatus' }
  },
  {
    title: '上次调度时间',
    key: 'latelyScheduleTime',
    align: 'center',
    ellipsis: true,
    sorter: (a, b) => (a.latelyScheduleTime || 0) - (b.latelyScheduleTime || 0),
    scopedSlots: { customRender: 'latelyScheduleTime' }
  },
  {
    title: '下次调度时间',
    key: 'nextTime',
    align: 'center',
    ellipsis: true,
    sorter: (a, b) => ((a.nextTime && a.nextTime.length && a.nextTime[0]) || 0) -
      ((b.nextTime && b.nextTime.length && b.nextTime[0]) || 0),
    scopedSlots: { customRender: 'nextTime' }
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
    width: 200,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'ScheduleList',
  components: { AddSchedulerTask },
  data: function() {
    return {
      query: {
        name: null,
        description: null,
        enableStatus: undefined,
        latelyStatus: undefined
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
      this.$api.getSchedulerTaskList({
        ...this.query,
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
    remove(id) {
      this.$confirm({
        title: '确认删除',
        content: '是否删除当前调度任务?',
        okText: '确认',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          this.$api.deleteSchedulerTask({
            id
          }).then(() => {
            this.$message.success('已删除')
            this.getList({})
          })
        }
      })
    },
    historyRecord(id) {
      this.$router.push({
        path: `/scheduler/record/${id}`
      })
    },
    manualTrigger(id) {
      this.$confirm({
        content: '确定要手动触发此任务吗?',
        okText: '确认',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          this.$api.manualTriggerSchedulerTask({
            id
          }).then(() => {
            this.$message.success('已触发')
          })
        }
      })
    },
    updateStatus(record) {
      this.$api.updateSchedulerTaskStatus({
        id: record.id,
        enableStatus: record.enableStatus === 1 ? 2 : 1
      }).then(() => {
        this.getList()
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
      this.query.enableStatus = undefined
      this.query.latelyStatus = undefined
      this.getList({})
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
