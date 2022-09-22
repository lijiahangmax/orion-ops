<template>
  <div class="user-event-log-container">
    <!-- 搜索列 -->
    <div class="table-search-columns">
      <a-form-model ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="操作用户" prop="user">
              <UserAutoComplete ref="userSelector" @change="selectedUser"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="关键字" prop="log">
              <a-input v-model="query.log" placeholder="日志关键字" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="7">
            <a-form-model-item label="操作分类" prop="classify">
              <a-input-group compact>
                <a-select v-model="query.classify"
                          placeholder="操作分类"
                          style="width: 50%;"
                          allowClear>
                  <a-select-option :value="classify.value" v-for="classify in EVENT_CLASSIFY" :key="classify.value">
                    {{ classify.label }}
                  </a-select-option>
                </a-select>
                <a-select v-model="query.type"
                          :disabled="!query.classify"
                          placeholder="操作类型"
                          style="width: 50%;"
                          @change="getList({})"
                          allowClear>
                  <a-select-option :value="typeInfo.value" v-for="typeInfo in typeArray" :key="typeInfo.value">
                    {{ typeInfo.label }}
                  </a-select-option>
                </a-select>
              </a-input-group>
            </a-form-model-item>
          </a-col>
          <a-col :span="7">
            <a-form-model-item label="时间区间" prop="date">
              <a-range-picker v-model="dateRange" @change="selectedDate"/>
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
          <span class="table-title">操作日志</span>
        </div>
        <!-- 右侧 -->
        <div class="tools-fixed-right">
          <div class="table-tools-bar p0 log-search-bar">
            <a-icon type="delete" class="tools-icon" title="清理" @click="openClear"/>
            <a-icon type="export" class="tools-icon" title="导出数据" @click="openExport"/>
            <a-icon type="search" class="tools-icon" title="查询" @click="getList({})"/>
            <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
          </div>
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
          <!-- 操作描述 -->
          <template #log="record">
            <span v-html="record.log"/>
          </template>
          <!-- 操作分类 -->
          <template #classify="record">
          <span class="span-blue pointer" @click="chooseClassify(record.classify)">
            {{ record.classify | filterClassify }}
          </span>
          </template>
          <!-- 操作类型 -->
          <template #type="record">
          <span class="span-blue pointer" @click="chooseType(record.classify, record.type)">
            {{ record.type | filterType }}
          </span>
          </template>
          <!-- 操作用户 -->
          <template #username="record">
          <span class="span-blue pointer" @click="chooseUser(record.userId)">
            {{ record.username }}
          </span>
          </template>
          <!-- 操作IP -->
          <template #ip="record">
            <span>{{ record.ip }}</span>
            <br>
            <span>{{ record.ipLocation }}</span>
          </template>
          <!-- 操作时间 -->
          <template #time="record">
            <span>{{ record.createTime | formatDate }}</span>
            <br>
            <span>({{ record.createTimeAgo }})</span>
          </template>
          <!-- 操作 -->
          <template #action="record">
            <span class="span-blue pointer" @click="preview(record.params)">参数</span>
          </template>
        </a-table>
      </div>
    </div>
    <!-- 事件 -->
    <div class="command-template-event">
      <!-- 预览 -->
      <EditorPreview ref="preview" title="参数预览" :editorConfig="{lang: 'json'}"/>
      <!-- 清理模态框 -->
      <EventLogClearModal ref="clear" @clear="getList({})"/>
      <!-- 导出模态框 -->
      <EventLogExportExportModal ref="export" :manager="true"/>
    </div>
  </div>
</template>

<script>
import { formatDate } from '@/lib/filters'
import { enumValueOf, EVENT_CLASSIFY, EVENT_TYPE } from '@/lib/enum'
import EditorPreview from '@/components/preview/EditorPreview'
import UserAutoComplete from '@/components/user/UserAutoComplete'
import { replaceStainKeywords } from '@/lib/utils'
import EventLogClearModal from '@/components/clear/EventLogClearModal'
import EventLogExportExportModal from '@/components/export/EventLogExportExportModal'

/**
 * 列
 */
const columns = [
  {
    title: '操作描述',
    key: 'log',
    scopedSlots: { customRender: 'log' }
  },
  {
    title: '操作分类',
    key: 'classify',
    width: 130,
    scopedSlots: { customRender: 'classify' }
  },
  {
    title: '操作类型',
    key: 'type',
    width: 130,
    scopedSlots: { customRender: 'type' }
  },
  {
    title: '操作用户',
    key: 'username',
    width: 130,
    scopedSlots: { customRender: 'username' }
  },
  {
    title: '操作IP',
    key: 'ip',
    width: 130,
    scopedSlots: { customRender: 'ip' }
  },
  {
    title: '操作时间',
    key: 'time',
    width: 150,
    scopedSlots: { customRender: 'time' }
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 80,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'UserEventLogList',
  components: {
    EventLogExportExportModal,
    EventLogClearModal,
    UserAutoComplete,
    EditorPreview
  },
  data() {
    return {
      columns,
      EVENT_CLASSIFY,
      loading: false,
      rows: [],
      query: {
        result: 1,
        parserIp: 1,
        userId: undefined,
        username: undefined,
        classify: undefined,
        type: undefined,
        log: undefined,
        rangeStart: undefined,
        rangeEnd: undefined
      },
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showTotal: total => {
          return `共 ${total} 条`
        }
      },
      typeArray: {},
      dateRange: undefined
    }
  },
  watch: {
    'query.classify'(e) {
      this.query.type = undefined
      this.typeArray = {}
      if (e) {
        for (const key in EVENT_TYPE) {
          if (EVENT_TYPE[key].classify === e) {
            this.typeArray[key] = EVENT_TYPE[key]
          }
        }
      }
      this.getList({})
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getEventLogList({
        ...this.query,
        page: page.current,
        limit: page.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
        this.rows = data.rows || []
        this.rows.forEach((row) => {
          row.log = replaceStainKeywords(row.log)
        })
        this.pagination = pagination
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    preview(json) {
      try {
        this.$refs.preview.preview(JSON.stringify(JSON.parse(json), null, 2))
      } catch (e) {
        this.$refs.preview.preview(json)
      }
    },
    selectedDate(moments, dates) {
      this.query.rangeStart = dates[0] + ' 00:00:00'
      this.query.rangeEnd = dates[1] + ' 23:59:59'
    },
    chooseUser(userId) {
      this.query.userId = userId
      this.$refs.userSelector.set(userId)
      this.getList({})
    },
    chooseClassify(classify) {
      this.query.classify = classify
      this.query.type = undefined
      this.getList({})
    },
    chooseType(classify, type) {
      this.query.classify = classify
      this.$nextTick(() => {
        this.query.type = type
        this.getList({})
      })
    },
    selectedUser(id, name) {
      if (id) {
        this.query.userId = id
        this.query.username = undefined
      } else {
        this.query.userId = undefined
        this.query.username = name
      }
    },
    openClear() {
      this.$refs.clear.open()
    },
    openExport() {
      this.$refs.export.open()
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.userSelector.reset()
      this.query.userId = undefined
      this.query.username = undefined
      this.query.classify = undefined
      this.query.type = undefined
      this.query.rangeStart = undefined
      this.query.rangeEnd = undefined
      this.dateRange = undefined
      this.getList({})
    }
  },
  filters: {
    formatDate,
    filterClassify(origin) {
      return enumValueOf(EVENT_CLASSIFY, origin).label
    },
    filterType(origin) {
      return enumValueOf(EVENT_TYPE, origin).label
    }
  },
  mounted() {
    const id = this.$route.params.id
    if (id) {
      this.query.userId = id
    }
    this.getList({})
  }
}
</script>

<style lang="less" scoped>

.log-item-container {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .log-item-container-left {

    .log-info {
      word-break: break-all;
      white-space: pre-line;
    }
  }

  .log-item-container-right {
    white-space: nowrap;

    .log-item-user {
      margin: 0 12px 0 24px;
      width: 120px;
      display: inline-block;
    }

    .log-item-type {
      margin: 0 12px 0 12px;
      width: 186px;
      display: inline-block;
    }

    .log-item-date {
      color: #000;
      width: 200px;
      display: inline-block;
    }
  }
}

</style>
