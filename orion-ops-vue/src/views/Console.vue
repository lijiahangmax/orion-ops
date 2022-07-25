<template>
  <div class="statistics-container">
    <!-- 顶部统计 -->
    <div class="statistics-top">
      <a-row>
        <template v-for="statisticItem of topStatistic">
          <a :key="statisticItem.field"
             :href="statisticItem.redirect"
             :title="statisticItem.redirectTitle">
            <a-col :span="6" :class="statisticItem.colClass">
              <a-card :bordered="false" :hoverable="true">
                <a-statistic :title="statisticItem.title" :suffix="statisticItem.suffix">
                  <template #formatter>
                    <span class="span-blue">
                      {{ count[statisticItem.field] }}
                    </span>
                  </template>
                </a-statistic>
              </a-card>
            </a-col>
          </a>
        </template>
      </a-row>
    </div>
    <!-- 中部统计 -->
    <div class="statistics-center">
      <!-- 快捷导航 -->
      <div class="quick-router-container">
        <a-card title="快捷导航" :bordered="false" style="width: 100%; height: 100%;">
          <!-- 快捷导航 -->
          <a-row>
            <a-col v-for="(quickRouterItem, index) of quickRouter" :key="index" :span="12">
              <a :href="quickRouterItem.redirect"
                 :target="quickRouterItem.redirectTarget"
                 :title="quickRouterItem.redirectTitle">
                <a-card :class="['quick-router-card', index % 2 === 0 ? 'quick-router-card-left' : 'quick-router-card-right']"
                        :style="{'margin-bottom': ((index >= quickRouter.length - 2) ? '' : '12px')}"
                        :hoverable="true">
                  <div class="quick-router-item-wrapper">
                    <a-icon class="quick-router-item-icon" :type="quickRouterItem.routerIcon" theme="twoTone"/>
                    <span class="quick-router-item-name">{{ quickRouterItem.routerName }}</span>
                  </div>
                </a-card>
              </a>
            </a-col>
          </a-row>
        </a-card>
      </div>
      <!-- 操作日志 -->
      <div class="event-log-container">
        <a-card title="操作日志" :bordered="false" style="width: 100%; height: 100%;">
          <EventLogList class="event-log-list-wrapper"
                        :disableSearch="true"
                        :disableAction="true"
                        :disableClick="true"/>
        </a-card>
      </div>
    </div>
  </div>
</template>

<script>

import EventLogList from '@/components/user/EventLogList'

/**
 * 头部统计
 */
const topStatistic = [
  {
    title: '机器数量',
    suffix: '台',
    colClass: 'pr8',
    redirect: '#/machine/list',
    redirectTitle: '跳转至机器列表',
    field: 'machineCount'
  }, {
    title: '环境数量',
    suffix: '个',
    colClass: 'px8',
    redirect: '#/app/profile',
    redirectTitle: '跳转至环境列表',
    field: 'profileCount'
  }, {
    title: '应用数量',
    suffix: '个',
    colClass: 'px8',
    redirect: '#/app/list',
    redirectTitle: '跳转至引用列表',
    field: 'appCount'
  }, {
    title: '应用流水线数量',
    suffix: '个',
    colClass: 'pl8',
    redirect: '#/app/pipeline/list',
    redirectTitle: '跳转至应用流水线列表',
    field: 'pipelineCount'
  }
]

/**
 * 快捷导航
 */
const quickRouter = [
  {
    routerName: '批量执行',
    routerIcon: 'code',
    redirect: '#/batch/exec/add',
    redirectTitle: '跳转至批量执行',
    redirectTarget: '_self'
  },
  {
    routerName: '日志面板',
    routerIcon: 'book',
    redirect: '#/log/list',
    redirectTitle: '跳转至日志面板',
    redirectTarget: '_self'
  },
  {
    routerName: '机器终端',
    routerIcon: 'cloud',
    redirect: '#/machine/terminal',
    redirectTitle: '跳转至机器终端',
    redirectTarget: '_blank'
  },
  {
    routerName: '文件管理器',
    routerIcon: 'reconciliation',
    redirect: '#/machine/sftp',
    redirectTitle: '跳转至机文件管理器',
    redirectTarget: '_blank'
  },
  {
    routerName: '应用构建任务',
    routerIcon: 'build',
    redirect: '#/app/build/list',
    redirectTitle: '跳转至',
    redirectTarget: '_self'
  },
  {
    routerName: '应用发布任务',
    routerIcon: 'rocket',
    redirect: '#/app/release/list',
    redirectTitle: '跳转至',
    redirectTarget: '_self'
  },
  {
    routerName: '应用流水线任务',
    routerIcon: 'api',
    redirect: '#/app/pipeline/task',
    redirectTitle: '跳转至应用流水线任务',
    redirectTarget: '_self'
  },
  {
    routerName: '应用流水线统计',
    routerIcon: 'fund',
    redirect: '#/app/pipeline/statistics',
    redirectTitle: '跳转至应用流水线统计',
    redirectTarget: '_self'
  },
  {
    routerName: '调度任务',
    routerIcon: 'schedule',
    redirect: '#/scheduler/list',
    redirectTitle: '跳转至',
    redirectTarget: '_self'
  },
  {
    routerName: '调度明细',
    routerIcon: 'dashboard',
    redirect: '#/scheduler/record',
    redirectTitle: '跳转至调度明细',
    redirectTarget: '_self'
  }
]

export default {
  name: 'Console',
  components: { EventLogList },
  data() {
    return {
      profileId: undefined,
      topStatistic,
      quickRouter,
      count: {
        machineCount: '-',
        profileCount: '-',
        appCount: '-',
        pipelineCount: '-'
      }
    }
  },
  methods: {
    chooseProfile({ id }) {
      this.profileId = id
      this.statisticsHome()
    },
    statisticsHome() {
      // 加载统计信息
      this.$api.getHomeStatistics({
        profileId: this.profileId
      }).then(({ data }) => {
        this.count = data.count
      })
    }
  },
  mounted() {
    // 读取当前环境
    const activeProfile = this.$storage.get(this.$storage.keys.ACTIVE_PROFILE)
    if (activeProfile) {
      this.profileId = JSON.parse(activeProfile).id
    }
    this.statisticsHome()
  }
}
</script>

<style lang="less" scoped>

.statistics-top {
  margin-bottom: 18px;
}

.statistics-center {
  display: flex;

  .quick-router-container {
    width: 468px;
    margin-right: 8px;
  }

  .quick-router-card {
    border-radius: 4px;
    cursor: pointer;
    background: #FDFDFD;
  }

  .quick-router-card-left {
    margin-right: 6px;
  }

  .quick-router-card-right {
    margin-left: 6px;
  }

  .quick-router-item-wrapper {
    display: flex;
    align-items: center;

    .quick-router-item-icon {
      font-size: 22px;
      margin-right: 8px;
    }

    .quick-router-item-name {
      font-size: 17px;
    }
  }

  .event-log-container {
    width: calc(100% - 484px);
    margin-left: 8px;

    /deep/ .ant-card-body {
      padding: 8px 8px 12px 24px;
    }
  }

  .event-log-list-wrapper {
    height: 454px;
    padding-right: 4px;
    overflow: auto;
  }
}

</style>
