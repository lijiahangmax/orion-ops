<template>
  <div class="app-release-statistic-view-container">
    <a-spin :spinning="loading">
      <!-- 发布视图 -->
      <div class="app-release-statistic-record-view-wrapper" v-if="initialized && view">
        <div class="app-release-statistic-record-view">
          <p class="statistics-description">近十次发布视图</p>
          <div class="app-release-statistic-main">
            <!-- 发布操作 -->
            <div class="app-release-actions-wrapper">
              <!-- 平均时间 -->
              <div class="app-release-actions-legend-wrapper">
                <div class="app-release-actions-legend">
                  <span class="avg-used-legend-wrapper">
                    平均发布时间: <span class="avg-used-legend">{{ view.avgUsedInterval }}</span>
                  </span>
                </div>
                <div class="app-release-actions-machine-legend">
                  发布机器
                </div>
              </div>
              <!-- 发布操作 -->
              <div class="app-release-actions" v-for="(action, index) of view.actions" :key="action.id">
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
              <div class="app-release-machines-wrapper" v-for="releaseRecord of view.releaseRecordList" :key="releaseRecord.releaseId">
                <!-- 发布信息头 -->
                <div class="app-release-machine-legend-wrapper">
                  <!-- 发布信息 -->
                  <a target="_blank"
                     style="height: 100%"
                     title="点击查看发布日志"
                     :href="`#/app/release/log/view/${releaseRecord.releaseId}`"
                     @click="openLogView($event,'release', releaseRecord.releaseId)">
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
                  </a>
                  <!-- 发布机器信息 -->
                  <div class="app-release-machine-legend">
                    <a target="_blank"
                       style="height: 100%"
                       title="点击查看机器日志"
                       v-for="(machine, index) of releaseRecord.machines"
                       :key="machine.id"
                       :href="`#/app/release/machine/log/view/${machine.id}`"
                       @click="openLogView($event,'machine', machine.id)">
                      <!-- 发布机器 -->
                      <div :class="['app-release-machine-info', index !== releaseRecord.machines.length - 1 ? 'app-release-machine-info-next' : '']">
                        <span class="app-release-machine-info-name" :title=" machine.machineName">{{ machine.machineName }}</span>
                        <div class="app-release-machine-info-status">
                          <a-tag :color="$enum.valueOf($enum.ACTION_STATUS, machine.status).color">
                            {{ $enum.valueOf($enum.ACTION_STATUS, machine.status).label }}
                          </a-tag>
                          <span class="app-release-machine-info-used" v-if="machine.usedInterval">{{ machine.usedInterval }}</span>
                          <span v-else/>
                        </div>
                      </div>
                    </a>
                  </div>
                </div>
                <!-- 发布机器操作 -->
                <div class="app-release-machine-actions-wrapper">
                  <div :class="['app-release-machine-actions', index !== releaseRecord.machines.length - 1 ? 'app-release-machine-actions-next' : '']"
                       v-for="(machine, index) of releaseRecord.machines"
                       :key="machine.id">
                    <!-- 发布操作 -->
                    <div class="app-release-action-log-actions-wrapper">
                      <div class="app-release-action-log-action-wrapper"
                           v-for="(actionLog, index) of machine.actionLogs" :key="index">
                        <!-- 构建操作值 -->
                        <div class="app-release-action-log-action"
                             v-if="!getCanOpenLog(actionLog)"
                             :style="getActionLogStyle(actionLog)"
                             v-text="getActionLogValue(actionLog)">
                        </div>
                        <!-- 可打开日志 -->
                        <a v-else target="_blank"
                           title="点击查看操作日志"
                           :href="`#/app/action/log/view/${actionLog.id}`"
                           @click="openLogView($event,'action', actionLog.id)">
                          <div class="app-release-action-log-action"
                               :style="getActionLogStyle(actionLog)"
                               v-text="getActionLogValue(actionLog)">
                          </div>
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 无数据 -->
      <div style="padding: 0 16px" v-else-if="initialized && !view">
        无发布记录
      </div>
    </a-spin>
    <!-- 事件 -->
    <div class="app-release-statistic-event-container">
      <!-- 发布日志模态框 -->
      <AppReleaseLogAppenderModal ref="releaseLogView"/>
      <!-- 发布日志模态框 -->
      <AppReleaseMachineLogAppenderModal ref="machineLogView"/>
      <!-- 操作日志模态框 -->
      <AppActionLogAppenderModal ref="actionLogView"/>
    </div>
  </div>
</template>

<script>
import _filters from '@/lib/filters'
import AppActionLogAppenderModal from '@/components/log/AppActionLogAppenderModal'
import AppReleaseMachineLogAppenderModal from '@/components/log/AppReleaseMachineLogAppenderModal'
import AppReleaseLogAppenderModal from '@/components/log/AppReleaseLogAppenderModal'

export default {
  name: 'AppReleaseStatisticsViews',
  components: {
    AppActionLogAppenderModal,
    AppReleaseLogAppenderModal,
    AppReleaseMachineLogAppenderModal
  },
  data() {
    return {
      loading: false,
      initialized: false,
      view: {}
    }
  },
  methods: {
    async init(appId, profileId) {
      this.loading = true
      this.initialized = false
      const { data } = await this.$api.getAppReleaseStatisticsView({
        appId,
        profileId
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
    async refresh(appId, profileId) {
      const { data } = await this.$api.getAppReleaseStatisticsView({
        appId,
        profileId
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
    getCanOpenLog(actionLog) {
      if (actionLog) {
        return this.$enum.valueOf(this.$enum.ACTION_STATUS, actionLog.status).log
      } else {
        return false
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

.app-release-statistic-record-view-wrapper {
  margin: 0 24px 24px 24px;
  overflow: auto;

  .app-release-statistic-record-view {
    margin: 0 16px 16px 16px;
    border-radius: 4px;
    display: inline-block;
  }

  .app-release-statistic-main {
    display: inline-block;
    box-shadow: 0 0 4px 1px #DEE2E6;
  }

  .app-release-actions-wrapper {
    display: flex;
    min-height: 82px;

    .app-release-actions-legend-wrapper {
      width: 320px;
      display: flex;
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

      .app-release-actions-legend {
        width: 180px;
        height: 100%;
        padding: 4px 8px 4px 0;
        display: flex;
        align-items: flex-end;
        justify-content: flex-end;
        border-right: 2px solid #F8F9FA;
      }

      .app-release-actions-machine-legend {
        width: 140px;
        height: 100%;
        padding: 4px 0 4px 8px;
        display: flex;
        align-items: flex-end;
        justify-content: flex-start;
      }
    }

    .app-release-actions {
      display: flex;
      flex-direction: column;
      width: 148px;

      .app-release-actions-name {
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
        padding: 8px 0 8px 8px;
        border-radius: 4px;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
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

      .app-release-record-legend:hover {
        transition: .3s;
        background: #D0EBFF;
      }

      .app-release-machine-legend {
        display: inline-block;
        width: 140px;
        border-right: 2px solid #F8F9FA;

        .app-release-machine-info {
          height: 64px;
          border-radius: 4px;
          display: flex;
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

        .app-release-machine-info:hover {
          transition: .3s;
          background: #dbe4ff;
        }
      }
    }

    .app-release-machine-actions-wrapper {
      display: flex;
      flex-direction: column;

      .app-release-machine-actions {
        height: 64px;

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
              font-size: 14px;
              font-weight: 600;
              display: flex;
              align-items: center;
              justify-content: space-around;
              opacity: 0.8;
              color: rgba(0, 0, 0, .8);
            }

            .app-release-action-log-action:hover {
              opacity: 1;
              transition: .3s;
            }
          }
        }
      }

      .app-release-machine-actions-next {
        border-bottom: 2px solid #F8F9FA;
      }
    }
  }
}

</style>
