<template>
  <div class="app-build-statistic-record-view-container">
    <a-spin :spinning="loading">
      <!-- 构建视图 -->
      <div class="app-build-statistic-record-view-wrapper" v-if="initialized && analysis">
        <div class="app-build-statistic-record-view">
          <p class="statistics-description">近十次构建视图</p>
          <!-- 构建视图主体 -->
          <div class="app-build-statistic-main">
            <!-- 构建操作 -->
            <div class="app-build-actions-wrapper">
              <!-- 平均时间 -->
              <div class="app-build-actions-legend">
                平均构建时间: <span style="margin-left: 4px; color: #000">{{ analysis.avgUsedInterval }}</span>
              </div>
              <!-- 构建操作 -->
              <div class="app-build-actions" v-for="(action, index) of analysis.actions" :key="action.id">
                <div :class="['app-build-actions-name', index % 2 === 0 ? 'app-build-actions-name-theme1' : 'app-build-actions-name-theme2']">
                  {{ action.name }}
                </div>
                <div :class="['app-build-actions-avg', index % 2 === 0 ? 'app-build-actions-avg-theme1' : 'app-build-actions-avg-theme2']">
                  <div class="app-build-actions-avg">
                    {{ action.avgUsedInterval }}
                  </div>
                </div>
              </div>
            </div>
            <!-- 构建日志 -->
            <div class="app-build-action-logs-container">
              <div class="app-build-action-logs-wrapper" v-for="buildRecord of analysis.buildRecordList" :key="buildRecord.buildId">
                <!-- 构建信息头 -->
                <div class="app-build-record-legend">
                  <!-- 构建状态 -->
                  <div class="app-build-record-status">
                    <!-- 构建序列 -->
                    <div class="build-seq">
                      <span class="span-blue">#{{ buildRecord.seq }}</span>
                    </div>
                    <!-- 构建状态 -->
                    <a-tag :color="$enum.valueOf($enum.BUILD_STATUS, buildRecord.status).color">
                      {{ $enum.valueOf($enum.BUILD_STATUS, buildRecord.status).label }}
                    </a-tag>
                    <!-- 日志 -->
                    <a-tooltip title="ctrl 点击打开新页面">
                      <a target="_blank"
                         class="build-log-trigger"
                         :href="`#/app/build/log/view/${buildRecord.buildId}`"
                         @click="openLogView($event, buildRecord.buildId)">日志</a>
                    </a-tooltip>
                  </div>
                  <!-- 构建时间 -->
                  <div class="app-build-record-info">
                    <span>{{ buildRecord.buildDate | formatDate('MM-dd HH:mm') }}</span>
                    <span v-if="buildRecord.usedInterval" class="ml4"> (used: {{ buildRecord.usedInterval }})</span>
                  </div>
                </div>
                <!-- 构建操作 -->
                <div class="app-build-action-log-actions-wrapper">
                  <div v-for="(actionLog, index) of buildRecord.actionLogs" :key="index"
                       class="app-build-action-log-action-wrapper">
                    <!-- 构建操作值 -->
                    <div class="app-build-action-log-action"
                         :style="getActionLogStyle(actionLog)"
                         v-text="getActionLogValue(actionLog)"/>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div style="padding: 0 16px" v-else-if="initialized && !analysis">
        无构建记录
      </div>
    </a-spin>
    <!-- 事件 -->
    <div class="app-build-statistic-event-container">
      <!-- 日志模态框 -->
      <AppBuildLogAppenderModal ref="logView"/>
    </div>
  </div>
</template>

<script>
import _filters from '@/lib/filters'
import AppBuildLogAppenderModal from '@/components/log/AppBuildLogAppenderMadal'

export default {
  name: 'AppBuildStatisticsViews',
  components: { AppBuildLogAppenderModal },
  data() {
    return {
      loading: false,
      initialized: false,
      analysis: {}
    }
  },
  methods: {
    async init(appId, profileId) {
      this.loading = true
      this.initialized = false
      const { data } = await this.$api.getAppBuildStatisticsView({
        appId,
        profileId
      })
      this.analysis = data
      this.initialized = true
      this.loading = false
    },
    clean() {
      this.initialized = false
      this.loading = false
      this.analysis = {}
    },
    async refresh(appId, profileId) {
      const { data } = await this.$api.getAppBuildStatisticsView({
        appId,
        profileId
      })
      this.analysis = data
    },
    openLogView(e, id) {
      if (!e.ctrlKey) {
        e.preventDefault()
        // 打开模态框
        this.$refs.logView.open(id)
        return false
      } else {
        // 跳转页面
        return true
      }
    },
    getActionLogStyle(actionLog) {
      if (actionLog) {
        return this.$enum.valueOf(this.$enum.ACTION_STATUS, actionLog.status).actionStyle
      } else {
        return {
          background: '#FFD43B'
        }
      }
    },
    getActionLogValue(actionLog) {
      if (actionLog) {
        return this.$enum.valueOf(this.$enum.ACTION_STATUS, actionLog.status).actionValue(actionLog)
      } else {
        return '未执行'
      }
    }
  },
  beforeDestroy() {
    this.clean()
  },
  filters: {
    ..._filters
  }
}
</script>

<style lang="less" scoped>

.app-build-statistic-record-view-wrapper {
  margin: 0 24px 24px 24px;
  overflow: auto;

  .app-build-statistic-record-view {
    margin: 0 16px 16px 16px;
    border-radius: 4px;
    display: inline-block;
  }

  .app-build-statistic-main {
    display: inline-block;
    box-shadow: 0 0 4px 1px #DEE2E6;
  }

  .build-seq {
    display: inline-block;
    width: 42px;
  }

  .build-log-trigger {
    display: inline-block;
    width: 55px;
    text-align: end;
  }

  .app-build-actions-wrapper {
    display: flex;
    min-height: 82px;

    .app-build-actions-legend {
      width: 180px;
      display: flex;
      align-items: flex-end;
      justify-content: flex-end;
      padding: 4px 8px 4px 0;
    }

    .app-build-actions {
      display: flex;
      flex-direction: column;
      width: 148px;

      .app-build-actions-name {
        height: 100%;
        font-size: 15px;
        color: #181E33;
        font-weight: 600;
        line-height: 18px;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 4px 16px;
        border-bottom: 1px solid #DEE2E6;
      }

      .app-build-actions-name-theme1 {
        background: #F1F3F5;
      }

      .app-build-actions-name-theme2 {
        background: #F8F9FA;
      }

      .app-build-actions-avg {
        text-align: center;
        height: 28px;
        padding: 2px;
      }

      .app-build-actions-avg-theme1 {
        background: #E9ECEF;
      }

      .app-build-actions-avg-theme2 {
        background: #F1F4F7;
      }
    }
  }

  .app-build-action-logs-wrapper {
    display: flex;
    height: 74px;

    .app-build-record-legend {
      width: 180px;
      padding: 8px;
      border-radius: 4px;
      display: flex;
      align-items: flex-start;
      flex-direction: column;
      justify-content: space-between;
      border-top: 2px solid #F8F9FA;

      .app-build-record-status {
        margin-bottom: 8px;
      }

      .app-build-record-info {
        color: #000000;
        font-size: 12px;
      }
    }

    .app-build-action-log-actions-wrapper {
      display: flex;

      .app-build-action-log-action-wrapper {
        width: 148px;
        border-radius: 4px;

        .app-build-action-log-action {
          margin: 2px 1px;
          height: calc(100% - 2px);
          border-radius: 4px;
          font-size: 14px;
          font-weight: 600;
          display: flex;
          align-items: center;
          justify-content: space-around;
          opacity: 0.8;
        }

        .app-build-action-log-action:hover {
          opacity: 1;
        }
      }
    }
  }

}

</style>
