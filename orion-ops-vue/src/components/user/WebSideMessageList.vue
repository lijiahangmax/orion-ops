<template>
  <div class="message-list-container">
    <!-- 筛选 -->
    <div class="message-list-filter table-search-columns">
      <a-form-model ref="query" :model="query">
        <a-row>
          <a-col :span="6">
            <a-form-model-item label="关键字" prop="message">
              <a-input v-model="query.message" placeholder="关键字" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <a-form-model-item label="分类" prop="classify">
              <a-select v-model="query.classify" placeholder="消息分类" allowClear>
                <a-select-option :value="classify.value" v-for="classify in MESSAGE_CLASSIFY" :key="classify.value">
                  {{ classify.label }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <a-form-model-item label="状态" prop="status">
              <a-select v-model="query.status" placeholder="全部" allowClear>
                <a-select-option :value="status.value" v-for="status in READ_STATUS" :key="status.value">
                  {{ status.label }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="6">
            <a-form-model-item label="时间" prop="date">
              <a-range-picker v-model="dateRange" @change="selectedDate"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <div class="table-tools-bar p0 message-search-bar">
              <a-popconfirm title="确认要全部已读?"
                            placement="topRight"
                            ok-text="确定"
                            cancel-text="取消"
                            @confirm="readAll">
                <a-icon type="book" class="tools-icon" title="全部已读"/>
              </a-popconfirm>
              <a-icon type="delete" class="tools-icon" title="清理" @click="openClear"/>
              <a-icon type="export" class="tools-icon" title="导出数据" @click="openExport"/>
              <a-icon type="search" class="tools-icon" title="查询" @click="getMessageList()"/>
              <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
            </div>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 列表 -->
    <div class="message-list-rows-container">
      <a-list :loading="loading"
              :pagination="pagination"
              :dataSource="rows">
        <template v-slot:renderItem="item">
          <a-list-item>
            <!-- 消息主体 -->
            <div class="message-item-container">
              <!-- 上半部分 标题 -->
              <div class="message-item-container-top">
                <a-badge class="message-item-dot" :dot="item.status === READ_STATUS.UNREAD.value">
                  <span class="message-item-title"
                        title="详情"
                        @click="openDetail(item)">
                    {{ item.classify | formatMessageType(item.type, 'label') }}
                  </span>
                </a-badge>
              </div>
              <!-- 下半部分 消息 -->
              <div class="message-item-container-bottom">
                <div class="message-item-container-left">
                  <!-- 消息 -->
                  <span class="message-body" v-html="item.message"/>
                </div>
                <div class="message-item-container-right">
                  <!-- 类型 -->
                  <span class="message-item-type span-blue" @click="chooseClassify(item.classify)">
                    {{ item.classify | formatMessageClassify('label') }}
                  </span>
                  <!-- 时间 -->
                  <span class="message-item-date">{{ item.createTime | formatDate }} ({{ item.createTimeAgo }})</span>
                  <!-- 操作 -->
                  <div class="message-action-wrapper">
                    <!-- 详情 -->
                    <span class="span-blue pointer" title="详情" @click="openDetail(item)">详情</span>
                    <a-divider type="vertical"/>
                    <!-- 跳转 -->
                    <a :href="item.classify | formatMessageType(item.type, 'redirect')" title="跳转">跳转</a>
                    <a-divider type="vertical"/>
                    <!-- 删除 -->
                    <a-popconfirm title="确认删除这条消息?"
                                  placement="topRight"
                                  ok-text="确定"
                                  cancel-text="取消"
                                  @confirm="remove(item.id)">
                      <span class="span-blue pointer" title="删除">删除</span>
                    </a-popconfirm>
                  </div>
                </div>
              </div>
            </div>
          </a-list-item>
        </template>
      </a-list>
    </div>
    <!-- 事件 -->
    <div class="message-event">
      <!-- 消息详情 -->
      <WebSideMessageModal ref="messageModal"/>
      <!-- 数据清理模态框 -->
      <WebSideMessageClearModal ref="clear" @clear="getMessageList()"/>
      <!-- 导出模态框-->
      <WebSideMessageExportModal ref="export"/>
    </div>
  </div>
</template>

<script>
import { replaceStainKeywords } from '@/lib/utils'
import { formatDate } from '@/lib/filters'
import { enumValueOf, MESSAGE_CLASSIFY, READ_STATUS } from '@/lib/enum'
import WebSideMessageModal from '@/components/user/WebSideMessageModal'
import WebSideMessageClearModal from '@/components/clear/WebSideMessageClearModal'
import WebSideMessageExportModal from '@/components/export/WebSideMessageExportModal'

export default {
  name: 'WebSideMessageList',
  components: {
    WebSideMessageExportModal,
    WebSideMessageClearModal,
    WebSideMessageModal
  },
  data() {
    return {
      MESSAGE_CLASSIFY,
      READ_STATUS,
      loading: false,
      rows: [],
      query: {
        message: undefined,
        classify: undefined,
        status: undefined,
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
          this.getMessageList(page)
        }
      },
      dateRange: undefined
    }
  },
  methods: {
    getMessageList(page = 1) {
      this.loading = true
      this.$api.getWebSideMessageList({
        ...this.query,
        page,
        limit: this.pagination.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
        this.rows = data.rows || []
        this.rows.forEach((row) => {
          row.message = replaceStainKeywords(row.message)
        })
        this.pagination = pagination
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    selectedDate(moments, dates) {
      this.query.rangeStart = dates[0] + ' 00:00:00'
      this.query.rangeEnd = dates[1] + ' 23:59:59'
    },
    readAll() {
      this.$api.setWebSideMessageAllRead().then(() => {
        this.$message.success('已完成')
      })
      this.rows.forEach(row => {
        row.status = READ_STATUS.READ.value
      })
    },
    remove(id) {
      this.$api.deleteWebSideMessage({
        idList: [id]
      }).then(() => {
        this.$message.success('已删除')
        this.getMessageList()
      })
    },
    openClear() {
      this.$refs.clear.open()
    },
    openExport() {
      this.$refs.export.open()
    },
    openDetail(detail) {
      detail.status = READ_STATUS.READ.value
      this.$api.getWebSideMessageDetail({
        id: detail.id
      }).then(({ data }) => {
        this.$refs.messageModal.open(data)
      })
    },
    chooseClassify(classify) {
      this.query.classify = classify
      this.query.type = undefined
      this.getMessageList()
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.query.classify = undefined
      this.query.type = undefined
      this.query.rangeStart = undefined
      this.query.rangeEnd = undefined
      this.dateRange = undefined
      this.getMessageList()
    }
  },
  filters: {
    formatDate,
    formatMessageClassify(classify, f) {
      return enumValueOf(MESSAGE_CLASSIFY, classify)[f]
    },
    formatMessageType(classify, type, f) {
      const messageType = enumValueOf(MESSAGE_CLASSIFY, classify).type
      return enumValueOf(messageType, type)[f]
    }
  },
  async mounted() {
    // 打开消息
    const messageId = this.$storage.getSession(this.$storage.keys.MESSAGE_ID)
    this.$storage.removeSession(this.$storage.keys.MESSAGE_ID)
    if (messageId) {
      // 打开消息模态框
      await this.$api.getWebSideMessageDetail({
        id: messageId
      }).then(({ data }) => {
        this.$refs.messageModal.open(data)
        // 获取列表
        this.getMessageList()
      })
    } else {
      // 获取列表
      this.getMessageList()
    }
  }
}
</script>

<style lang="less" scoped>

.message-list-filter {
  padding: 8px 0 0 0 !important;
  margin-bottom: 16px;

  .message-search-bar {
    justify-content: flex-end;
    height: 40px;
  }
}

.message-item-container {
  width: 100%;

  .message-item-dot /deep/ .ant-badge-dot {
    margin: 4px -8px;
  }

  .message-item-title {
    color: #181E33;
    display: block;
    cursor: pointer;
    margin-bottom: 8px;
    font-size: 15px;
    line-height: 24px;
  }

  .message-item-container-bottom {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .message-item-container-left {
      width: calc(100% - 468px);

      .message-body {
        word-break: break-all;
        white-space: nowrap;
        text-overflow: ellipsis;
        display: block;
        overflow-x: hidden;
      }
    }

    .message-item-container-right {
      white-space: nowrap;
      width: 468px;

      .message-item-type {
        margin: 0 12px 0 24px;
        width: 86px;
        display: inline-block;
        cursor: pointer;
      }

      .message-item-date {
        color: #000;
        width: 200px;
        margin-right: 8px;
        display: inline-block;
      }

      .message-action-wrapper {
        display: inline-block;
        padding-left: 14px;
      }
    }
  }

}

</style>
