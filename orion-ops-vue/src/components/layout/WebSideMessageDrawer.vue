<template>
  <div class="web-side-message-container">
    <!-- 触发器 -->
    <a @click="onOpen">
      <!-- 红点 -->
      <a-badge class="unread-message-dot" :dot="unreadCount > 0">
        <a-icon class="web-side-message-trigger" type="notification"/>
      </a-badge>
    </a>
    <!-- 侧边抽屉 -->
    <a-drawer :title="null"
              placement="right"
              :closable="false"
              :width="400"
              :maskStyle="{opacity: 0, animation: 'none'}"
              :bodyStyle="{padding: 0}"
              :maskClosable="true"
              :visible="visible"
              @close="onClose">
      <!-- 消息头 -->
      <div class="message-header-container">
        <!-- 头部左侧 -->
        <div class="message-header-left">
          <!-- 消息类型 -->
          <a-dropdown :trigger="['click']">
            <!-- 消息类型触发器 -->
            <a class="message-block-item message-type-dropdown-item" @click="e => e.preventDefault()">
              <a-icon :type="messageStatus.icon"/>
              <span>{{ messageStatus.label }}</span>
              <a-icon type="down"/>
            </a>
            <!-- 消息类型下拉框 -->
            <a-menu slot="overlay">
              <a-menu-item v-for="item of messageStatusList" :key="item.label">
                <span class="message-block-item message-type-dropdown-item" @click="changeStatus(item)">
                 <a-icon :type="item.icon"/>
                 <span>{{ item.label }}</span>
                </span>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </div>
        <!-- 头部右侧 -->
        <div class="message-header-right">
          <!-- 消息处理 -->
          <a-dropdown :trigger="['click']">
            <!-- 消息处理触发器 -->
            <a class="message-block-item" @click="e => e.preventDefault()">
              <a-icon class="message-action-trigger" type="ellipsis"/>
            </a>
            <!-- 消息处理下拉框 -->
            <a-menu slot="overlay">
              <a-menu-item key="1">
                <span class="message-block-item message-type-action-item" @click="readAllMessage()">
                 <a-icon type="check"/>
                 <span>标记所有消息为已读</span>
                </span>
              </a-menu-item>
              <a-menu-item key="2">
                <span class="message-block-item message-type-action-item" @click="deleteReadMessage()">
                 <a-icon type="delete"/>
                 <span>删除所有已读消息</span>
                </span>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
          <!-- 关闭消息 -->
          <a class="message-block-item" @click="e => e.preventDefault()">
            <a-icon class="message-action-trigger" type="close"/>
          </a>
        </div>
      </div>
      <!-- 列表 -->
      <div class="">
        身体
      </div>
    </a-drawer>
  </div>
</template>

<script>
import { clearStainKeywords, dateFormat, replaceStainKeywords } from '@/lib/utils'
import { enumValueOf, MESSAGE_CLASSIFY, READ_STATUS } from '@/lib/enum'

const messageStatusList = [{
  status: 1,
  label: '未读消息',
  icon: 'tag'
}, {
  status: undefined,
  label: '全部消息',
  icon: 'tags'
}]

export default {
  name: 'WebSideMessageDrawer',
  data() {
    return {
      visible: true,
      loading: false,
      unreadCount: 0,
      maxId: null,
      rows: [],
      pollId: null,
      messageStatus: messageStatusList[0],
      messageStatusList
    }
  },
  methods: {
    onOpen() {
      this.visible = true
    },
    onClose() {
      this.visible = false
    },
    changeStatus(status) {
      this.messageStatus = status
    },
    readAllMessage() {

    },
    deleteReadMessage() {

    },
    visibleChange(e) {
      if (!e) {
        return
      }
      this.loading = true
      // 获取站内信
      this.$api.getWebSideMessageList({
        status: READ_STATUS.UNREAD.value,
        limit: 10000
      }).then(({ data }) => {
        this.rows = data.rows || []
        this.unreadCount = this.rows.length
        this.rows.forEach(row => {
          // 格式化时间
          row.createTimeString = dateFormat(new Date(row.createTime))
          // 处理数据
          row.message = replaceStainKeywords(row.message)
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
              const messageType = enumValueOf(enumValueOf(MESSAGE_CLASSIFY, newMessage.classify).type, newMessage.type)
              this.$notification[messageType.notify]({
                message: messageType.label,
                description: () => clearStainKeywords(newMessage.message),
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
  filters: {
    formatMessageType(classify, type, f) {
      const messageType = enumValueOf(MESSAGE_CLASSIFY, classify).type
      return enumValueOf(messageType, type)[f]
    }
  },
  mounted() {
    this.pollId !== null && clearInterval(this.pollId)
    this.pollWebSideMessage()
    // 轮询
    this.pollId = setInterval(this.pollWebSideMessage, 15000)
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

.message-header-container {
  height: 48px;
  padding: 0 12px;
  border-bottom: 1px solid #CED4DA;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .message-header-right {
    display: flex;
  }
}

.message-block-item {
  padding-left: 4px;
  display: flex;
  align-items: center;
  color: grey !important;

  span {
    display: inline-block;
    user-select: none;
    font-size: 15.5px;
  }
}

.message-block-item:hover {
  color: #1890FF !important;
}

.message-type-dropdown-item {
  width: 124px;
}

.message-type-action-item i {
  font-size: 17px !important;
}

.message-type-action-item {
  width: 186px;
}

.message-action-trigger {
  margin: 0 4px;
  font-size: 19px;
  font-weight: 600;
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
