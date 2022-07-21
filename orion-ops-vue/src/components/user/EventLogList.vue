<template>
  <div class="log-list-container">
    <!-- 筛选 -->
    <div v-if="!disableSearch" class="log-list-filter table-search-columns">
      <a-form-model ref="query" :model="query">
        <a-row>
          <a-col :span="6">
            <a-form-model-item label="关键字" prop="log">
              <a-input v-model="query.log" placeholder="日志关键字" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="分类" prop="classify">
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
                          @change="getEventLog(1)"
                          allowClear>
                  <a-select-option :value="typeInfo.value" v-for="typeInfo in typeArray" :key="typeInfo.value">
                    {{ typeInfo.label }}
                  </a-select-option>
                </a-select>
              </a-input-group>
            </a-form-model-item>
          </a-col>
          <a-col :span="6">
            <a-form-model-item label="时间" prop="date">
              <a-range-picker v-model="dateRange" @change="selectedDate"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <div class="table-tools-bar p0 log-search-bar">
              <a-icon type="export" class="tools-icon" title="导出数据" @click="openExport"/>
              <a-icon type="search" class="tools-icon" title="查询" @click="getEventLog()"/>
              <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
            </div>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 列表 -->
    <div class="log-list-rows-container">
      <a-list :loading="loading"
              :pagination="pagination"
              :dataSource="rows">
        <template v-slot:renderItem="item">
          <a-list-item>
            <!-- 日志主体 -->
            <div class="log-item-container">
              <div class="log-item-container-left">
                <span class="log-info" v-html="item.log"/>
              </div>
              <div class="log-item-container-right">
                <!-- 操作类型 -->
                <span class="log-item-classify">
                  <span class="span-blue pointer" @click="chooseClassify(item.classify)">
                    {{ item.classify | filterClassify }}
                  </span>
                </span>
                <!-- 操作时间 -->
                <span class="log-item-date">
                  {{ item.createTime | formatDate }} ({{ item.createTimeAgo }})</span>
              </div>
            </div>
            <!-- 操作 -->
            <template v-if="!disableAction" #actions>
              <span class="span-blue" title="查看参数" @click="preview(item.params)">参数</span>
            </template>
          </a-list-item>
        </template>
      </a-list>
    </div>
    <!-- 事件 -->
    <div class="log-list-event">
      <!-- 预览 -->
      <EditorPreview ref="preview" title="参数预览" :editorConfig="{lang: 'json'}"/>
      <!-- 导出模态框 -->
      <EventLogExportExportModal ref="export" :manager="false"/>
    </div>
  </div>
</template>

<script>
import { replaceStainKeywords } from '@/lib/utils'
import { formatDate } from '@/lib/filters'
import { enumValueOf, EVENT_CLASSIFY, EVENT_TYPE } from '@/lib/enum'
import EditorPreview from '@/components/preview/EditorPreview'
import EventLogExportExportModal from '@/components/export/EventLogExportExportModal'

export default {
  name: 'EventLogList',
  components: {
    EventLogExportExportModal,
    EditorPreview
  },
  props: {
    disableSearch: {
      type: Boolean,
      default: false
    },
    disableAction: {
      type: Boolean,
      default: false
    },
    disableClick: {
      type: Boolean,
      default: false
    },
    baseQuery: {
      type: Object,
      default: () => {
        return {
          result: 1,
          onlyMyself: 1
        }
      }
    }
  },
  data() {
    return {
      EVENT_CLASSIFY,
      loading: false,
      rows: [],
      query: {
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
        },
        onChange: page => {
          this.getEventLog(page)
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
      this.getEventLog()
    }
  },
  methods: {
    getEventLog(page = 1) {
      this.loading = true
      this.$api.getEventLogList({
        ...this.query,
        ...this.baseQuery,
        page,
        limit: this.pagination.pageSize
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
    chooseClassify(classify) {
      if (this.disableClick) {
        return
      }
      this.query.classify = classify
      this.query.type = undefined
      this.getEventLog()
    },
    openExport() {
      this.$refs.export.open()
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.query.classify = undefined
      this.query.type = undefined
      this.query.rangeStart = undefined
      this.query.rangeEnd = undefined
      this.dateRange = undefined
      this.getEventLog()
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
    this.getEventLog()
  }
}
</script>

<style lang="less" scoped>

.log-list-filter {
  padding: 8px 0 0 0 !important;
  margin-bottom: 16px;

  .log-search-bar {
    justify-content: flex-end;
    height: 40px;
  }
}

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

    .log-item-classify {
      margin: 0 12px 0 24px;
      width: 98px;
      display: inline-block;
    }

    .log-item-date {
      color: #000;
      width: 200px;
      display: inline-block;
    }
  }
}

/deep/ .ant-list-item-action {
  margin-left: 8px;
}

</style>
