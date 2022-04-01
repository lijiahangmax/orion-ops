<template>
  <div class="app-release-statistics-record-container">
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
    <!-- 发布明细 -->
    <div class="app-release-statistics-container">
      <a-spin :spinning="loading">
        <!-- 统计指标容器 -->
        <div class="app-release-statistic-header-wrapper">
          <div class="clean"/>
          <!-- 统计指标 -->
          <div class="app-release-statistic-header">
            <a-statistic class="statistic-metrics-item" title="发布次数" :value="statisticMetrics.releaseCount"/>
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
        <div class="app-release-statistic-main-wrapper">
          <!-- 发布视图 -->
          <div class="app-release-statistic-record-view-wrapper" v-if="analysis">
            <a-divider class="app-release-statistic-title">近十次发布视图</a-divider>
            <div class="app-release-statistic-record-view">
              <!-- 发布操作 -->
              <div class="app-release-actions-wrapper">
                <!-- 平均时间 -->
                <div class="app-release-actions-legend-wrapper">
                  <div class="app-release-actions-legend">
                    平均发布时间 : <span style="color: #000">{{ analysis.avgUsedInterval }}</span>
                  </div>
                  <div class="app-release-actions-machine-legend">
                    <span>发布机器</span>
                  </div>
                </div>
                <!-- 发布操作 -->
                <div class="app-release-actions" v-for="(action, index) of analysis.actions" :key="action.id">
                  <div :class="['app-release-actions-name', index % 2 === 0 ? 'app-release-actions-name-theme1' : 'app-release-actions-name-theme2']">
                    {{ action.name }}
                  </div>
                  <div :class="['app-release-actions-avg', index % 2 === 0 ? 'app-release-actions-avg-theme1' : 'app-release-actions-avg-theme2']">
                    <div class="app-release-actions-avg">
                      {{ action.avgUsedInterval }}
                    </div>
                  </div>
                </div>
              </div>
              <!-- 发布日志 -->
              <div class="app-release-machines-container">
                <!-- 发布机器 -->
                <div class="app-release-machines-wrapper" v-for="releaseRecord of analysis.releaseRecordList" :key="releaseRecord.buildId">
                  <!-- 发布信息头 -->
                  <div class="app-release-machine-legend-wrapper">
                    <!-- 发布信息 -->
                    <div class="app-release-record-legend">
                      <div class="app-release-record-status">
                        <span class="app-release-title" :title="releaseRecord.releaseTitle">
                          {{ releaseRecord.releaseTitle }}
                        </span>
                        <!-- 发布状态 -->
                        <a-tag class="mx4" :color="$enum.valueOf($enum.RELEASE_STATUS, releaseRecord.status).color">
                          {{ $enum.valueOf($enum.RELEASE_STATUS, releaseRecord.status).label }}
                        </a-tag>
                      </div>
                      <div class="app-release-record-info">
                        <span>{{ releaseRecord.releaseDate | formatDate('MM-dd HH:mm') }}</span>
                        <span v-if="releaseRecord.usedInterval"> (used: {{ releaseRecord.usedInterval }})</span>
                      </div>
                    </div>
                    <!-- 发布机器信息 -->
                    <div class="app-release-machine-legend">
                      <!-- 发布机器 -->
                      <div :class="['app-release-machine-info', index !== releaseRecord.machines.length - 1 ? 'app-release-machine-info-next' : '']"
                           v-for="(machine, index) of releaseRecord.machines"
                           :key="machine.id">
                        <span class="app-release-machine-info-name" :title=" machine.machineName">{{ machine.machineName }}</span>
                        <div class="app-release-machine-info-status">
                          <a-tag :color="$enum.valueOf($enum.ACTION_STATUS, machine.status).color">
                            {{ $enum.valueOf($enum.ACTION_STATUS, machine.status).label }}
                          </a-tag>
                          <span class="app-release-machine-info-used" v-if="machine.usedInterval">{{ machine.usedInterval }}</span>
                          <span v-else/>
                        </div>
                      </div>
                    </div>
                  </div>
                  <!-- 发布机器操作 -->
                  <div class="app-release-machine-actions-wrapper">
                    <div :class="['app-release-machine-actions', index !== releaseRecord.machines.length - 1 ? 'app-release-machine-actions-next' : '']"
                         v-for="(machine, index) of releaseRecord.machines"
                         :key="machine.id">
                      <!-- 发布操作 -->
                      <div class="app-release-action-log-actions-wrapper">
                        <div v-for="(actionLog, index) of machine.actionLogs"
                             :key="index"
                             :class="['app-release-action-log-action-wrapper', index % 2 === 0 ? 'app-release-action-log-action-wrapper-theme1' : 'app-release-action-log-action-wrapper-theme2']">
                          <!-- 发布操作值 -->
                          <div class="app-release-action-log-action"
                               :style="getActionLogStyle(actionLog)"
                               v-text="getActionLogValue(actionLog)"/>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- 统计表格 -->
          <div class="app-release-statistic-chart-wrapper">
            <a-divider class="app-release-statistic-title">近七天发布统计</a-divider>
            <div id="app-release-statistic-chart"/>
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
  name: 'AppReleaseStatistics',
  data() {
    return {
      appLoading: false,
      appList: [],
      selectedAppIds: [0],
      profileId: null,
      loading: false,
      chart: null,
      statisticMetrics: {
        releaseCount: 0,
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
      this.$api.getAppReleaseStatistics({
        appId,
        profileId: this.profileId
      }).then(({ data }) => {
        this.statisticMetrics.releaseCount = data.releaseCount
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
          type: '发布次数',
          value: d.releaseCount
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
        container: 'app-release-statistic-chart',
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

.app-release-statistics-record-container {
  display: flex;

  .app-list-menu {
    width: 216px;
    padding: 0 8px 8px 8px;
    margin-right: 16px;
    background-color: #FFF;
    border-radius: 4px;
  }

  .app-release-statistics-container {
    width: calc(100% - 232px);
    background-color: #FFF;
    border-radius: 4px;
    min-height: calc(100vh - 84px);

    .app-release-statistic-header-wrapper {
      display: flex;
      justify-content: center;
      margin: 24px 0 12px 16px;

      .app-release-statistic-header {
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

.app-release-statistic-title {
  margin: 36px 0
}

.app-release-statistic-record-view-wrapper {
  margin: 0 24px 24px 24px;

  .app-release-statistic-record-view {
    border-radius: 4px;
    display: inline-block;
    box-shadow: 0 0 4px 1px #DEE2E6;
  }

  .app-release-actions-wrapper {
    display: flex;
    height: 82px;

    .app-release-actions-legend-wrapper {
      width: 320px;

      .app-release-actions-legend {
        padding: 56px 12px 8px 8px;
        display: inline-block;
        width: 180px;
        border-right: 2px solid #F8F9FA;
        text-align: end;
      }

      .app-release-actions-machine-legend {
        display: inline-block;
        padding-left: 42px;
      }
    }

    .app-release-actions {
      display: flex;
      flex-direction: column;
      width: 148px;

      .app-release-actions-name {
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

      .app-release-actions-name-theme1 {
        background: #F1F3F5;
      }

      .app-release-actions-name-theme2 {
        background: #F8F9FA;
      }

      .app-release-actions-avg {
        text-align: center;
        height: 28px;
        padding: 2px;
      }

      .app-release-actions-avg-theme1 {
        background: #E9ECEF;
      }

      .app-release-actions-avg-theme2 {
        background: #F1F4F7;
      }
    }
  }

  .app-release-machines-wrapper {
    display: flex;

    .app-release-machine-legend-wrapper {
      width: 320px;
      border-top: 2px solid #F8F9FA;
      border-radius: 4px;
      display: flex;
      align-items: flex-start;
      flex-direction: row;
      justify-content: center;

      .app-release-record-legend {
        width: 180px;
        height: 100%;
        display: flex;
        padding: 8px 0 8px 8px;
        flex-direction: column;
        justify-content: center;
        align-items: flex-start;
        border-right: 2px solid #F8F9FA;

        .app-release-record-status {
          margin-bottom: 8px;
          display: flex;
          justify-content: space-between;
          align-items: stretch;
          width: 100%;

          .app-release-title {
            width: 108px;
            text-overflow: ellipsis;
            white-space: nowrap;
            overflow-x: hidden;
          }
        }

        .app-release-record-info {
          color: #000000;
          font-size: 12px;
        }
      }

      .app-release-machine-legend {
        display: inline-block;
        width: 140px;
        border-right: 2px solid #F8F9FA;

        .app-release-machine-info {
          display: flex;
          height: 54px;
          flex-direction: column;
          justify-content: space-around;

          .app-release-machine-info-name {
            color: #181E33;
            padding: 0 4px;
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow-x: hidden;
          }

          .app-release-machine-info-status {
            display: flex;
            justify-content: space-between;
            padding: 0 4px 4px 4px;
            align-items: flex-end;
          }

          .app-release-machine-info-used {
            color: #000000;
            font-size: 14px;
          }
        }

        .app-release-machine-info-next {
          border-bottom: 2px solid #F8F9FA;
        }
      }
    }

    .app-release-machine-actions-wrapper {
      display: flex;
      flex-direction: column;

      .app-release-machine-actions {
        height: 54px;

        .app-release-action-log-actions-wrapper {
          display: flex;
          height: 100%;

          .app-release-action-log-action-wrapper {
            width: 148px;
            border-radius: 4px;

            .app-release-action-log-action {
              margin: 2px 1px;
              height: 100%;
              border-radius: 4px;
              font-size: 12px;
              display: flex;
              align-items: center;
              justify-content: space-around;
              opacity: 0.9;
            }
          }

          .app-release-action-log-action-wrapper-theme1 {
            background: #F4F7FA;
          }

          .app-release-action-log-action-wrapper-theme2 {
            background: #F9FCFF;
          }
        }
      }

      .app-release-machine-actions-next {
        border-bottom: 2px solid #F8F9FA;
      }
    }
  }
}

.app-release-statistic-chart-wrapper {
  margin: 0 24px 16px 24px;

  #app-release-statistic-chart {
    height: 500px;
  }
}

</style>
