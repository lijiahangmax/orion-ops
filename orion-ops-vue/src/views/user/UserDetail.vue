<template>
  <div class="user-detail-container">
    <a-tabs :defaultActiveKey="key" tabPosition="left" @change="changeTab">
      <!-- 基本信息 -->
      <a-tab-pane :key="1" tab="基本信息">
        <UserBasicForm/>
      </a-tab-pane>
      <!-- 操作日志 -->
      <a-tab-pane :key="2" tab="操作日志">
        <EventLogList/>
      </a-tab-pane>
      <!-- 站内信 -->
      <a-tab-pane :key="3" tab="站内信">
        <WebSideMessageList/>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>
import UserBasicForm from '@/components/user/UserBasicForm'

export default {
  name: 'UserDetail',
  data() {
    return {
      key: 1
    }
  },
  components: {
    UserBasicForm,
    EventLogList: () => import('@/components/user/EventLogList'),
    WebSideMessageList: () => import('@/components/user/WebSideMessageList')
  },
  methods: {
    changeTab(key) {
      switch (key) {
        case 1:
          document.title = '基本信息'
          break
        case 2:
          document.title = '操作日志'
          break
        case 3:
          document.title = '站内信'
          break
        default:
          break
      }
    }
  },
  created() {
    const key = this.$route.query.key
    if (key) {
      this.key = parseInt(this.$route.query.key)
    }
    this.changeTab(this.key)
  }
}
</script>

<style lang="less" scoped>

.user-detail-container {
  background: #FFF;
  padding: 8px 16px;
  border-radius: 4px;
}
</style>
