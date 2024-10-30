<template>
  <div class="login-history-container">
    <h2 class="m0">登录历史</h2>
    <a-divider class="title-divider"/>
    <span class="history-extra">当前帐号最近登录的30条历史记录</span>
    <!-- 登录线 -->
    <div class="login-history-wrapper">
      <a-spin :spinning="loading">
        <a-timeline>
          <a-timeline-item v-for="log of loginLogs" :key="log.id">
            <!-- 图标 -->
            <template #dot>
              <div class="login-icon-wrapper">
                <a-icon class="login-icon" type="desktop"/>
              </div>
            </template>
            <!-- 登录ip -->
            <span class="login-info login-ip">
              <span v-if="log.ip" class="mr8">{{ log.ip }}</span>
              <span>{{ log.ipLocation }}</span>
            </span>
            <!-- 登录时间 -->
            <span class="login-info login-time">
              <span>{{ log.createTime | formatDate }} ({{ log.createTimeAgo }})</span>
              <a-icon v-if="log.refreshLogin" class="login-mode" type="clock-circle" theme="twoTone" title="自动登录"/>
            </span>
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
import { EVENT_TYPE } from '@/lib/enum'

export default {
  name: 'LoginHistory',
  data() {
    return {
      type: EVENT_TYPE.LOGIN.value,
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
        parserIp: 1,
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
            ip: row.ip,
            ipLocation: row.ipLocation,
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

::v-deep .ant-timeline {
  margin: 36px 0 0 18px;
}

::v-deep .ant-timeline-item-content {
  top: -20px;
  margin: 0 0 0 44px;
}

.login-info {
  display: flex;
  margin-bottom: 2px;
  align-items: center;
}

.login-ip {
  color: rgba(0, 0, 0, 0.8);
  font-size: 16px;
  font-weight: 600;
}

.login-mode {
  font-size: 17px;
  margin-left: 8px;
}
</style>
