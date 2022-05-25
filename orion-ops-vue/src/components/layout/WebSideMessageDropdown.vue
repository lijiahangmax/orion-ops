<template>
  <a-dropdown :trigger="['click']"
              title="站内信"
              overlayClassName="web-side-message-item"
              @visibleChange="visibleChange">
    <!-- 触发器 -->
    <a @click="e => e.preventDefault()">
      <!-- 红点 -->
      <a-badge class="unread-message-dot" :dot="unreadCount > 0">
        <a-icon class="web-side-message-trigger" type="notification"/>
      </a-badge>
    </a>
    <!-- 站内信 -->
    <template #overlay>
      <div class="message-overlay">
        <!-- 消息容器 -->
        <div class="message-item-container">
          <a-spin v-if="rows.length > 0" :spinning="loading">
            <!-- 消息容器 -->
            <div class="message-item-wrapper" v-for="(row, index) of rows" :key="row.id" @click="toMessageDetail(row.id)">
              <div class="message-item">
                <div class="message-title-wrapper">
                  <!-- 标题 -->
                  <span class="message-title" v-text="$enum.valueOf($enum.valueOf($enum.MESSAGE_CLASSIFY, row.classify).type, row.type).label"/>
                  <!-- 时间 -->
                  <span class="message-time" v-text="row.createTimeAgo" :title="row.createTimeString"/>
                </div>
                <!-- 消息 -->
                <span class="massage-body" v-html="row.message"/>
              </div>
              <!-- 消息分隔符 -->
              <a-divider class="message-divider" v-if="index !== rows.length - 1"/>
            </div>
          </a-spin>
          <!-- 无消息 -->
          <a-empty v-if="rows.length === 0 && !loading" class="message-empty" description="暂无未读消息"/>
        </div>
        <!-- 操作 -->
        <a-divider class="message-divider"/>
        <div class="message-handler">
          <span class="span-blue pointer" v-if="rows.length > 0" @click="readAll">已读全部</span>
          <span v-else/>
          <a href="#/user/detail?key=3">查看更多</a>
        </div>
      </div>
    </template>
  </a-dropdown>
</template>

<script>

export default {
  name: 'WebSideMessageDropdown',
  data() {
    return {
      loading: false,
      unreadCount: 0,
      maxId: null,
      rows: [],
      pollId: null
    }
  },
  methods: {
    visibleChange(e) {
      if (!e) {
        return
      }
      this.loading = true
      // 获取站内信
      this.$api.getWebSideMessageList({
        status: this.$enum.READ_STATUS.UNREAD.value,
        limit: 10000
      }).then(({ data }) => {
        this.rows = data.rows || []
        this.unreadCount = this.rows.length
        this.rows.forEach(row => {
          // 格式化时间
          row.createTimeString = this.$utils.dateFormat(new Date(row.createTime))
          // 处理数据
          row.message = this.$utils.replaceStainKeywords(row.message)
        })
        this.loading = false
      }).then(() => {
        this.loading = false
      })
    },
    pollWebSideMessage() {
      this.$api.pollWebSideMessage({
        maxId: this.maxId
      }).then(({ data }) => {
        this.unreadCount = data.unreadCount
        this.maxId = data.maxId
        const newMessages = data.newMessages
        if (newMessages && newMessages.length) {
          // 通知新消息
          for (const newMessage of newMessages) {
            setTimeout(() => {
              const messageType = this.$enum.valueOf(this.$enum.valueOf(this.$enum.MESSAGE_CLASSIFY, newMessage.classify).type, newMessage.type)
              this.$notification[messageType.notify]({
                message: messageType.label,
                description: () => this.$utils.clearStainKeywords(newMessage.message),
                duration: 3
              })
            })
          }
        }
      })
    },
    readAll() {
      this.unreadCount = 0
      this.$api.setWebSideMessageAllRead()
    },
    toMessageDetail(id) {
      this.unreadCount -= 1
      this.$storage.setSession(this.$storage.keys.MESSAGE_ID, id)
      this.$router.push({
        path: '/user/detail',
        query: {
          key: 3
        }
      })
    }
  },
  mounted() {
    this.pollId !== null && clearInterval(this.pollId)
    this.pollWebSideMessage()
    // 轮询
    this.pollId = setInterval(this.pollWebSideMessage, 10000)
  },
  beforeDestroy() {
    this.pollId !== null && clearInterval(this.pollId)
    this.pollId = null
  }
}
</script>

<style lang="less" scoped>

.web-side-message-trigger {
  font-size: 19px;
  color: #181E33;
}

.unread-message-dot /deep/ .ant-badge-dot {
  margin: -2px;
}

/deep/ .ant-dropdown-menu-items {
  padding: 0 8px 0 8px;
  margin: 0;
}

.message-overlay {
  width: 348px;
  background-color: #FFFFFF;
  padding: 4px;
  border-radius: 4px;
  box-shadow: 0 8px 8px 0 #DDDFE0;

  .message-handler {
    display: flex;
    justify-content: space-between;
    padding: 2px 8px;
  }
}

.message-item-container {
  min-height: 248px;
  max-height: 348px;
  overflow-y: auto;

  .message-item {
    padding: 8px;
    margin-bottom: 4px;
    border-radius: 4px;
    transition: .3s;
    cursor: pointer;

    .message-title-wrapper {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 8px;
      color: rgba(0, 0, 0, .65);
      font-size: 14px;
      line-height: 22px;

      .message-title {
        color: #181E33;
      }

      .message-time {
        font-size: 13px;
      }
    }

    .massage-body {
      color: rgba(0, 0, 0, .45);
      font-size: 13px;
      line-height: 22px;
    }
  }

  .message-item:hover {
    background: #F1F3F5;
  }

  .message-empty {
    margin-top: 28px;
  }

}

.message-divider {
  margin: 4px 0 0 0;
}

</style>
