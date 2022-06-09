<template>
  <div class="app-pipeline-statistic-record-view-container">
    <a-spin :spinning="loading">
      <!-- 流水线视图 -->
      <div class="app-pipeline-statistic-record-view-wrapper" v-if="initialized && view">
        <div class="app-pipeline-statistic-record-view">
          <p class="statistics-description">近十次执行视图</p>
          <!-- 流水线视图主体 -->
          <div class="app-pipeline-statistic-main">
            <!-- 流水线操作 -->
            <div class="app-pipeline-details-wrapper">
              <!-- 平均时间 -->
              <div class="app-pipeline-details-legend">
                <span class="avg-used-legend-wrapper">
                  <span>平均执行时间: </span>
                  <span class="avg-used-legend">{{ view.avgUsedInterval }}</span>
                </span>
              </div>
              <!-- 流水线操作 -->
              <div class="app-pipeline-details" v-for="(detail, index) of view.details" :key="detail.id">
                <div :class="['app-pipeline-details-name', index % 2 === 0 ? 'app-pipeline-details-name-theme1' : 'app-pipeline-details-name-theme2']">
                  <span class="span-blue mr4">{{ detail.stageType | formatStageType('label') }}</span>
                  <span>{{ detail.appName }}</span>
                </div>
                <div :class="['app-pipeline-details-avg', index % 2 === 0 ? 'app-pipeline-details-avg-theme1' : 'app-pipeline-details-avg-theme2']">
                  <div class="app-pipeline-details-avg">
                    {{ detail.avgUsedInterval }}
                  </div>
                </div>
              </div>
            </div>
            <!-- 流水线日志 -->
            <div class="app-pipeline-detail-logs-container">
              <!-- 日志 -->
              <div class="app-pipeline-detail-logs-wrapper"
                   v-for="pipelineTask of view.pipelineTaskList"
                   :key="pipelineTask.id">
                <!-- 流水线信息头 -->
                <div class="app-pipeline-record-legend">
                  <!-- 流水线状态 -->
                  <div class="app-pipeline-record-status">
                    <!-- 流水线序列 -->
                    <div class="pipeline-title">
                      <span class="span-blue">{{ pipelineTask.title }}</span>
                    </div>
                    <!-- 流水线状态 -->
                    <a-tag class="m0" :color="pipelineTask.status | formatPipelineStatus('color')">
                      {{ pipelineTask.status | formatPipelineStatus('label') }}
                    </a-tag>
                  </div>
                  <!-- 流水线时间 -->
                  <div class="app-pipeline-record-info">
                    <span>{{ pipelineTask.execDate | formatDate('MM-dd HH:mm') }}</span>
                    <span v-if="pipelineTask.usedInterval" class="ml4"> (used: {{ pipelineTask.usedInterval }})</span>
                  </div>
                </div>
                <!-- 流水线操作 -->
                <div class="app-pipeline-detail-log-details-wrapper">
                  <div class="app-pipeline-detail-log-detail-wrapper"
                       v-for="(detailLog, index) of pipelineTask.details" :key="index">
                    <!-- 流水线操作值 -->
                    <div class="app-pipeline-detail-log-detail"
                         v-if="!getCanOpenLog(detailLog)"
                         :style="getDetailLogStyle(detailLog)"
                         v-text="getDetailLogValue(detailLog)">
                    </div>
                    <!-- 可打开日志 -->
                    <a v-else target="_blank"
                       :title="`点击查看${enumValueOf(STAGE_TYPE, detailLog.stageType).label}日志`"
                       :href="`#/app/${enumValueOf(STAGE_TYPE, detailLog.stageType).symbol}/log/view/${detailLog.relId}`"
                       @click="openLogView($event, enumValueOf(STAGE_TYPE, detailLog.stageType).symbol, detailLog.relId)">
                      <div class="app-pipeline-detail-log-detail"
                           :style="getDetailLogStyle(detailLog)"
                           v-text="getDetailLogValue(detailLog)">
                      </div>
                    </a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 无数据 -->
      <div style="padding: 0 16px" v-else-if="initialized && !view">
        无执行记录
      </div>
    </a-spin>
    <!-- 事件 -->
    <div class="app-pipeline-statistic-event-container">
      <!-- 构建日志模态框 -->
      <AppBuildLogAppenderModal ref="buildLogView"/>
      <!-- 发布日志模态框 -->
      <AppReleaseLogAppenderModal ref="releaseLogView"/>
    </div>
  </div>
</template>

<script>
import { formatDate } from '@/lib/filters'
import { enumValueOf, PIPELINE_DETAIL_STATUS, PIPELINE_STATUS, STAGE_TYPE } from '@/lib/enum'
import AppBuildLogAppenderModal from '@/components/log/AppBuildLogAppenderModal'
import AppReleaseLogAppenderModal from '@/components/log/AppReleaseLogAppenderModal'

export default {
  name: 'AppPipelineStatisticsViews',
  components: {
    AppBuildLogAppenderModal,
    AppReleaseLogAppenderModal
  },
  data() {
    return {
      STAGE_TYPE,
      loading: false,
      initialized: false,
      view: {}
    }
  },
  methods: {
    enumValueOf,
    async init(pipelineId) {
      this.loading = true
      this.initialized = false
      const { data } = await this.$api.getAppPipelineTaskStatisticsView({
        pipelineId
      })
      this.view = data
      this.initialized = true
      this.loading = false
    },
    clean() {
      this.initialized = false
      this.loading = false
      this.view = {}
    },
    async refresh(pipelineId) {
      const { data } = await this.$api.getAppPipelineTaskStatisticsView({
        pipelineId
      })
      this.view = data
    },
    openLogView(e, type, id) {
      if (!e.ctrlKey) {
        e.preventDefault()
        // 打开模态框
        this.$refs[`${type}LogView`].open(id)
        return false
      } else {
        // 跳转页面
        return true
      }
    },
    getCanOpenLog(detailLog) {
      if (detailLog) {
        return enumValueOf(PIPELINE_DETAIL_STATUS, detailLog.status).log
      } else {
        return false
      }
    },
    getDetailLogStyle(detailLog) {
      if (detailLog) {
        return enumValueOf(PIPELINE_DETAIL_STATUS, detailLog.status).actionStyle
      } else {
        return {
          background: '#FFD43B'
        }
      }
    },
    getDetailLogValue(detailLog) {
      if (detailLog) {
        return enumValueOf(PIPELINE_DETAIL_STATUS, detailLog.status).actionValue(detailLog)
      } else {
        return '未执行'
      }
    }
  },
  beforeDestroy() {
    this.clean()
  },
  filters: {
    formatDate,
    formatStageType(type, f) {
      return enumValueOf(STAGE_TYPE, type)[f]
    },
    formatPipelineStatus(status, f) {
      return enumValueOf(PIPELINE_STATUS, status)[f]
    }
  }
}
</script>

<style lang="less" scoped>

.app-pipeline-statistic-record-view-wrapper {
  margin: 0 24px 24px 24px;
  overflow: auto;

  .app-pipeline-statistic-record-view {
    margin: 0 16px 16px 16px;
    border-radius: 4px;
    display: inline-block;
  }

  .app-pipeline-statistic-main {
    display: inline-block;
    box-shadow: 0 0 4px 1px #DEE2E6;
  }

  .app-pipeline-details-wrapper {
    display: flex;
    min-height: 82px;

    .app-pipeline-details-legend {
      width: 180px;
      display: flex;
      align-items: flex-end;
      justify-content: flex-end;
      padding: 4px 8px 4px 0;
      font-weight: 600;

      .avg-used-legend-wrapper {
        display: flex;
        align-items: center;
      }

      .avg-used-legend {
        margin-left: 4px;
        color: #000;
        font-size: 16px;
      }
    }

    .app-pipeline-details {
      display: flex;
      flex-direction: column;
      width: 148px;

      .app-pipeline-details-name {
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

      .app-pipeline-details-name-theme1 {
        background: #F1F3F5;
      }

      .app-pipeline-details-name-theme2 {
        background: #F8F9FA;
      }

      .app-pipeline-details-avg {
        text-align: center;
        height: 28px;
        padding: 2px;
      }

      .app-pipeline-details-avg-theme1 {
        background: #E9ECEF;
      }

      .app-pipeline-details-avg-theme2 {
        background: #F1F4F7;
      }
    }
  }

  .app-pipeline-detail-logs-wrapper {
    display: flex;
    height: 74px;

    .app-pipeline-record-legend {
      width: 180px;
      height: 100%;
      padding: 8px;
      border-radius: 4px;
      display: flex;
      align-items: flex-start;
      flex-direction: column;
      justify-content: space-between;
      border-top: 2px solid #F8F9FA;

      .app-pipeline-record-status {
        margin-bottom: 8px;
        display: flex;
        justify-content: space-between;
        width: 100%;
      }

      .pipeline-title {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        width: 112px;
        display: inline-block;
      }

      .app-pipeline-record-info {
        color: #000000;
        font-size: 12px;
      }
    }

    .app-pipeline-detail-log-details-wrapper {
      display: flex;

      .app-pipeline-detail-log-detail-wrapper {
        width: 148px;
        border-radius: 4px;

        .app-pipeline-detail-log-detail {
          margin: 2px 1px;
          height: calc(100% - 2px);
          border-radius: 4px;
          font-size: 14px;
          font-weight: 600;
          display: flex;
          align-items: center;
          justify-content: space-around;
          opacity: 0.8;
          color: rgba(0, 0, 0, .8);
        }

        .app-pipeline-detail-log-detail:hover {
          opacity: 1;
          transition: .3s;
        }
      }
    }
  }

}

</style>
