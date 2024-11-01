<template>
  <div class="user-detail-container gray-box-shadow">
    <a-tabs :defaultActiveKey="key" tabPosition="left" @change="changeTab">
      <!-- 基本信息 -->
      <a-tab-pane :key="1" tab="基本信息">
        <UserBasicForm/>
      </a-tab-pane>
      <!-- 操作日志 -->
      <a-tab-pane :key="2" tab="操作日志">
        <EventLogList/>
      </a-tab-pane>
      <!-- 登录历史 -->
      <a-tab-pane :key="3" tab="登录历史">
        <LoginHistory/>
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
    LoginHistory: () => import('@/components/user/LoginHistory')
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
          document.title = '登录历史'
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
  border-radius: 2px;
}
</style>
