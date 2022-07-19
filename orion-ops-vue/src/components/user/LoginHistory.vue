<template>
  <div class="login-history-container">
    <h2 class="m0">登陆历史</h2>
    <a-divider class="title-divider"/>
    <span class="history-extra">当前帐号最近登录的30条历史记录</span>
    <!-- 登陆线 -->
    <div class="login-history-wrapper">
      <a-spin :spinning="loading">
        <a-timeline>
          <a-timeline-item v-for="log of loginLogs" :key="log.id">
            <template #dot>
              <!-- 图标 -->
              <div class="login-icon-wrapper">
                <a-icon class="login-icon" type="desktop"/>
              </div>
            </template>
            <!-- 登陆ip -->
            <span class="login-info login-ip">
              {{ log.ip }}
              <!-- 登陆方式 -->
              <span v-if="log.refreshLogin" class="login-mode">(自动登陆)</span>
              <span v-else class="login-mode">(手动登陆)</span>
            </span>
            <!-- 登陆时间 -->
            <span class="login-info login-time">{{ log.createTime | formatDate }} ({{ log.createTimeAgo }})</span>
            <!-- ua -->
            <span class="login-info login-ua">{{ log.userAgent }}</span>
          </a-timeline-item>
        </a-timeline>
      </a-spin>
    </div>
  </div>
</template>

<script>
import { formatDate } from '@/lib/filters'
import { EVENT_CLASSIFY } from '@/lib/enum'

export default {
  name: 'LoginHistory',
  data() {
    return {
      type: EVENT_CLASSIFY.AUTHENTICATION.type.LOGIN.value,
      loading: false,
      loginLogs: []
    }
  },
  methods: {
    getLoginHistory() {
      this.loading = true
      this.$api.getEventLogList({
        result: 1,
        onlyMyself: 1,
        type: this.type,
        page: 1,
        limit: 30
      }).then(({ data }) => {
        this.loading = false
        this.loginLogs = data.rows.map(row => {
          const params = JSON.parse(row.params)
          return {
            createTime: row.createTime,
            createTimeAgo: row.createTimeAgo,
            ip: params.ip,
            userAgent: params.userAgent,
            refreshLogin: params.refreshLogin
          }
        })
      }).catch(() => {
        this.loading = false
      })
    }
  },
  filters: {
    formatDate
  },
  mounted() {
    this.getLoginHistory()
  }
}
</script>

<style lang="less" scoped>
.login-history-container {
  margin: 8px 0;
}

.title-divider {
  margin: 8px 0;
}

.history-extra {
  color: #8C8C8C;
  display: block;
  margin-bottom: 18px;
}

.login-history-wrapper {
  min-height: 100px;
}

.login-icon-wrapper {
  border-radius: 50%;
  width: 48px;
  height: 48px;
  background: #9199A1;
  display: flex;
  align-items: center;
  justify-content: center;

  .login-icon {
    font-size: 24px;
    color: #FFF;
  }
}

/deep/ .ant-timeline {
  margin: 36px 0 0 18px;
}

/deep/ .ant-timeline-item-content {
  top: -20px;
  margin: 0 0 0 44px;
}

.login-info {
  display: block;
  margin-bottom: 2px;
}

.login-ip {
  color: rgba(0, 0, 0, 0.8);
  font-size: 16px;
  font-weight: 500;
}

.login-mode {
  color: #52C41A;
}

</style>
