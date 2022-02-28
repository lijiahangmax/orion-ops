<template>
  <div class="log-list-container">
    <!-- 筛选 -->
    <div class="log-list-filter table-search-columns">
      <a-form-model ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="用户" prop="user">
              <UserAutoComplete ref="userSelector" @change="selectedUser"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="关键字" prop="log">
              <a-input v-model="query.log" placeholder="日志关键字" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="分类" prop="classify">
              <a-input-group compact>
                <a-select v-model="query.classify" placeholder="操作分类" style="width: 50%;" allowClear>
                  <a-select-option :value="classify.value" v-for="classify in $enum.EVENT_CLASSIFY" :key="classify.value">
                    {{ classify.label }}
                  </a-select-option>
                </a-select>
                <a-select v-model="query.type" placeholder="操作类型" style="width: 50%;" allowClear>
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
          <a-col :span="3">
            <div class="table-tools-bar p0 log-search-bar">
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
          <a-list-item key="item.title">
            <!-- 日志主体 -->
            <div class="log-item-container">
              <div class="log-item-container-left">
                <span class="log-info" v-html="item.log"></span>
              </div>
              <div class="log-item-container-right">
                <!-- 操作人 -->
                <span class="log-item-user span-blue pointer"
                      :title="item.username"
                      @click="chooseUser(item.userId)">
                  {{ item.username }}
                </span>
                <!-- 操作类型 -->
                <span class="log-item-type">
                  <span class="span-blue pointer" @click="chooseClassify(item.classify)">
                    {{ item.classify | filterClassify }}
                  </span> /
                  <span class="span-blue pointer" @click="chooseType(item.classify, item.type)">
                    {{ item.type | filterType(item.classify) }}
                  </span>
                </span>
                <!-- 操作时间 -->
                <span class="log-item-date">
                  {{ item.createTime | formatDate }} ({{ item.createTimeAgo }})</span>
              </div>
            </div>
            <!-- 操作 -->
            <template #actions>
              <span class="span-blue" title="查看参数" @click="preview(item.params)">参数</span>
            </template>
          </a-list-item>
        </template>
      </a-list>
    </div>
    <!-- 事件 -->
    <div class="log-list-event">
      <EditorPreview ref="preview" title="参数预览" :editorConfig="{lang: 'json'}"/>
    </div>
  </div>
</template>

<script>
import _filters from '@/lib/filters'
import _enum from '@/lib/enum'
import UserAutoComplete from '@/components/user/UserAutoComplete'
import EditorPreview from '@/components/preview/EditorPreview'

export default {
  name: 'UserEventLogList',
  components: {
    UserAutoComplete,
    EditorPreview
  },
  data() {
    return {
      loading: false,
      rows: [],
      query: {
        result: 1,
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
      if (e) {
        const classify = this.$enum.valueOf(this.$enum.EVENT_CLASSIFY, e)
        if (!classify) {
          return
        }
        this.query.type = undefined
        this.typeArray = { ...classify.type }
      } else {
        this.loadAllType()
      }
    }
  },
  methods: {
    getEventLog(page = 1) {
      this.loading = true
      this.$api.getLogList({
        ...this.query,
        page,
        limit: this.pagination.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
        this.rows = data.rows || []
        this.rows.forEach((row) => {
          row.log = this.$utils.cleanXss(row.log)
            .replaceAll('&lt;sb&gt;', '<span class="span-blue mx4">')
            .replaceAll('&lt;/sb&gt;', '</span>')
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
      this.getEventLog()
    },
    chooseClassify(classify) {
      this.query.classify = classify
      this.query.type = undefined
      this.getEventLog()
    },
    chooseType(classify, type) {
      this.query.classify = classify
      this.$nextTick(() => {
        this.query.type = type
        this.getEventLog()
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
      this.getEventLog()
    },
    loadAllType() {
      this.typeArray = {}
      for (const classifyKey in this.$enum.EVENT_CLASSIFY) {
        this.typeArray = { ...this.typeArray, ...this.$enum.EVENT_CLASSIFY[classifyKey].type }
      }
    }
  },
  filters: {
    ..._filters,
    filterClassify(origin) {
      return _enum.valueOf(_enum.EVENT_CLASSIFY, origin).label
    },
    filterType(origin, classify) {
      const _classify = _enum.valueOf(_enum.EVENT_CLASSIFY, classify)
      if (!_classify) {
        return null
      }
      return _enum.valueOf(_classify.type, origin).label
    }
  },
  mounted() {
    this.loadAllType()
    this.getEventLog()
  }
}
</script>

<style lang="less" scoped>

.log-list-container {
  background: #FFF;
  border-radius: 4px;
  padding: 16px;

  .log-list-filter {
    margin-bottom: 16px;
    padding: 0;

    .log-search-bar {
      justify-content: flex-end;
      height: 40px;
    }
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

    .log-item-user {
      margin: 0 12px 0 24px;
      width: 120px;
      display: inline-block;
    }

    .log-item-type {
      margin: 0 12px 0 12px;
      width: 180px;
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
