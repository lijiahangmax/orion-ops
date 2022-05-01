<template>
  <div class="app-build-statistics-record-container">
    <!-- 应用列表菜单 -->
    <div class="app-list-menu">
      <!-- 应用列表表头 -->
      <div class="app-list-menu-header">
        <a-page-header @back="() => {}">
          <template #title>
            <span class="ant-page-header-heading-title pointer" title="刷新" @click="getAppList">应用列表</span>
          </template>
          <template #backIcon>
            <a-icon type="reload" title="刷新" @click="getAppList"/>
          </template>
        </a-page-header>
      </div>
      <!-- 应用菜单 -->
      <a-spin :spinning="appLoading">
        <div class="app-list-wrapper">
          <a-menu mode="inline" v-model="selectedAppIds">
            <a-menu-item v-for="app in appList" :key="app.id" :title="app.name" @click="chooseApp(app.id)">
              <a-icon type="code-sandbox"/>
              {{ app.name }}
            </a-menu-item>
          </a-menu>
        </div>
      </a-spin>
    </div>
    <!-- 构建明细 -->
    <div class="app-build-statistics-container">
      <a-spin :spinning="loading">
        <!-- 统计指标容器 -->
        <div class="app-build-statistic-header-wrapper">
          <div class="clean"/>
          <!-- 统计指标 -->
          <div class="app-build-statistic-header">
            <a-statistic class="statistic-metrics-item" title="构建次数" :value="statisticMetrics.buildCount"/>
            <a-divider class="statistic-metrics-divider" type="vertical"/>
            <a-statistic class="statistic-metrics-item green" title="成功次数" :value="statisticMetrics.successCount"/>
            <a-divider class="statistic-metrics-divider" type="vertical"/>
            <a-statistic class="statistic-metrics-item red" title="失败次数" :value="statisticMetrics.failureCount"/>
            <a-divider class="statistic-metrics-divider" type="vertical"/>
            <a-statistic class="statistic-metrics-item" title="平均耗时" :value="statisticMetrics.avgUsedInterval"/>
          </div>
          <div class="clean"/>
        </div>
        <!-- 统计信息 -->
        <div class="app-build-statistic-main-wrapper">
          <!-- 构建视图 -->
          <div class="app-build-statistic-record-view-wrapper" v-if="analysis">
            <a-divider class="app-build-statistic-title">近十次构建视图</a-divider>
            <div class="app-build-statistic-record-view">
              <!-- 构建操作 -->
              <div class="app-build-actions-wrapper">
                <!-- 平均时间 -->
                <div class="app-build-actions-legend">
                  平均构建时间 : <span style="color: #000">{{ analysis.avgUsedInterval }}</span>
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
                    </div>
                    <!-- 构建时间 -->
                    <div class="app-build-record-info">
                      <span>{{ buildRecord.buildDate | formatDate('MM-dd HH:mm') }}</span>
                      <span v-if="buildRecord.usedInterval"> (used: {{ buildRecord.usedInterval }})</span>
                    </div>
                  </div>
                  <!-- 构建操作 -->
                  <div class="app-build-action-log-actions-wrapper">
                    <div v-for="(actionLog, index) of buildRecord.actionLogs"
                         :key="index"
                         :class="['app-build-action-log-action-wrapper', index % 2 === 0 ? 'app-build-action-log-action-wrapper-theme1' : 'app-build-action-log-action-wrapper-theme2']">
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
          <!-- 统计表格 -->
          <div class="app-build-statistic-chart-wrapper">
            <a-divider class="app-build-statistic-title">近七天构建统计</a-divider>
            <div id="app-build-statistic-chart"/>
          </div>
        </div>
      </a-spin>
    </div>
  </div>
</template>

<script>
import { Chart } from '@antv/g2'
import _filters from '@/lib/filters'

export default {
  name: 'AppBuildStatistics',
  data() {
    return {
      appLoading: false,
      appList: [],
      selectedAppIds: [0],
      profileId: null,
      loading: false,
      chart: null,
      statisticMetrics: {
        buildCount: 0,
        successCount: 0,
        failureCount: 0,
        avgUsedInterval: 0
      },
      analysis: null
    }
  },
  methods: {
    async getAppList() {
      this.appLoading = true
      await this.$api.getAppList({
        limit: 10000
      }).then(({ data }) => {
        this.appLoading = false
        this.appList = data.rows || []
      }).catch(() => {
        this.appLoading = false
      })
    },
    chooseApp(id) {
      this.selectedAppIds = [id]
      this.getStatistic(id)
    },
    chooseProfile({ id }) {
      this.profileId = id
      this.chooseApp(this.selectedAppIds[0])
    },
    getStatistic(appId) {
      this.loading = true
      this.$api.getAppBuildStatistics({
        appId,
        profileId: this.profileId
      }).then(({ data }) => {
        this.statisticMetrics.buildCount = data.buildCount
        this.statisticMetrics.successCount = data.successCount
        this.statisticMetrics.failureCount = data.failureCount
        this.statisticMetrics.avgUsedInterval = data.avgUsedInterval
        this.analysis = data.analysis
        this.loading = false
        // 渲染表格
        this.renderChart(data.charts)
      }).catch(() => {
        this.loading = false
      })
    },
    renderChart(data) {
      // 处理数据
      const chartsData = []
      for (const d of data) {
        chartsData.push({
          date: d.date,
          type: '构建次数',
          value: d.buildCount
        })
        chartsData.push({
          date: d.date,
          type: '成功次数',
          value: d.successCount
        })
        chartsData.push({
          date: d.date,
          type: '失败次数',
          value: d.failureCount
        })
      }
      this.chart && this.chart.destroy()
      // 渲染图表
      this.chart = new Chart({
        container: 'app-build-statistic-chart',
        autoFit: true
      })
      this.chart.data(chartsData)

      this.chart.tooltip({
        showCrosshairs: true,
        shared: true
      })

      this.chart.line()
        .position('date*value')
        .color('type')
        .shape('circle')
      this.chart.render()
    },
    getActionLogStyle(actionLog) {
      if (actionLog) {
        return this.$enum.valueOf(this.$enum.ACTION_STATUS, actionLog.status).actionStyle
      } else {
        return {
          background: '#FFD8A8'
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
  async mounted() {
    // 读取当前环境
    const activeProfile = this.$storage.get(this.$storage.keys.ACTIVE_PROFILE)
    if (!activeProfile) {
      this.$message.warning('请先维护应用环境')
      return
    }
    this.profileId = JSON.parse(activeProfile).id
    // 加载应用列表
    await this.getAppList()
    if (this.appList.length) {
      this.chooseApp(this.appList[0].id)
    } else {
      this.$message.warning('请先维护应用')
    }
  },
  beforeDestroy() {
    this.chart && this.chart.destroy()
  },
  filters: {
    ..._filters
  }
}
</script>

<style lang="less" scoped>

.app-build-statistics-record-container {
  display: flex;

  .app-list-menu {
    width: 216px;
    padding: 0 8px 8px 8px;
    margin-right: 16px;
    background-color: #FFF;
    border-radius: 4px;
  }

  .app-build-statistics-container {
    width: calc(100% - 232px);
    background-color: #FFF;
    border-radius: 4px;
    min-height: calc(100vh - 84px);

    .app-build-statistic-header-wrapper {
      display: flex;
      justify-content: center;
      margin: 24px 0 12px 16px;

      .app-build-statistic-header {
        display: flex;

        .statistic-metrics-item {
          margin: 0 16px;
        }

        .statistic-metrics-item.green /deep/ .ant-statistic-content {
          color: #58C612;
        }

        .statistic-metrics-item.red /deep/ .ant-statistic-content {
          color: #DD2C00;
        }

        .statistic-metrics-divider {
          height: auto;
        }

        /deep/ .ant-statistic-content {
          text-align: center;
        }
      }
    }
  }
}

.app-build-statistic-title {
  margin: 36px 0
}

.app-build-statistic-record-view-wrapper {
  margin: 0 24px 24px 24px;

  .app-build-statistic-record-view {
    border-radius: 4px;
    display: inline-block;
    box-shadow: 0 0 4px 1px #DEE2E6;
  }

  .build-seq {
    display: inline-block;
    width: 42px;
  }

  .app-build-actions-wrapper {
    display: flex;
    height: 82px;

    .app-build-actions-legend {
      width: 180px;
      text-align: end;
      padding: 56px 12px 8px 8px;
    }

    .app-build-actions {
      display: flex;
      flex-direction: column;
      width: 148px;

      .app-build-actions-name {
        height: 54px;
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
      padding: 8px 8px 4px 8px;
      border-radius: 4px;
      display: flex;
      align-items: flex-start;
      flex-direction: column;
      justify-content: center;
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
          font-size: 12px;
          display: flex;
          align-items: center;
          justify-content: space-around;
          opacity: 0.9;
        }
      }

      .app-build-action-log-action-wrapper-theme1 {
        background: #F4F7FA;
      }

      .app-build-action-log-action-wrapper-theme2 {
        background: #F9FCFF;
      }
    }
  }

}

.app-build-statistic-chart-wrapper {
  margin: 0 24px 16px 24px;

  #app-build-statistic-chart {
    height: 500px;
  }
}

</style>
