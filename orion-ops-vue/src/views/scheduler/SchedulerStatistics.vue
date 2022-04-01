<template>
  <div class="scheduler-record-container">
    <!-- 调度任务菜单 -->
    <div class="scheduler-task-menu">
      <!-- 任务列表头 -->
      <div class="scheduler-task-header">
        <a-page-header @back="() => {}">
          <template #title>
            <span class="ant-page-header-heading-title pointer" title="刷新" @click="getSchedulerTask">任务列表</span>
          </template>
          <template #backIcon>
            <a-icon type="reload" title="刷新" @click="getSchedulerTask"/>
          </template>
        </a-page-header>
      </div>
      <!-- 任务菜单 -->
      <a-spin :spinning="taskLoading">
        <div class="task-list-wrapper">
          <a-menu mode="inline" v-model="selectedTaskIds">
            <a-menu-item v-for="task in taskList" :key="task.id" :title="task.name" @click="chooseTask(task.id)">
              <a-icon type="carry-out"/>
              {{ task.name }}
            </a-menu-item>
          </a-menu>
        </div>
      </a-spin>
    </div>
    <!-- 调度明细图表 -->
    <div class="scheduler-task-statistic-container">
      <a-spin :spinning="loading">
        <!-- 统计指标容器 -->
        <div class="scheduler-statistic-header-wrapper">
          <div class="clean"/>
          <!-- 统计指标 -->
          <div class="scheduler-statistic-header">
            <a-statistic class="statistic-metrics-item" title="调度次数" :value="statisticMetrics.scheduledCount"/>
            <a-divider class="statistic-metrics-divider" type="vertical"/>
            <a-statistic class="statistic-metrics-item green" title="成功次数" :value="statisticMetrics.successCount"/>
            <a-divider class="statistic-metrics-divider" type="vertical"/>
            <a-statistic class="statistic-metrics-item red" title="失败次数" :value="statisticMetrics.failureCount"/>
            <a-divider class="statistic-metrics-divider" type="vertical"/>
            <a-statistic class="statistic-metrics-item" title="平均耗时" :value="statisticMetrics.avgUsedInterval"/>
          </div>
          <div class="clean"/>
        </div>
        <div class="scheduler-statistic-main-wrapper">
          <!-- 统计图表 -->
          <div class="scheduler-statistic-chart-wrapper">
            <a-divider class="scheduler-statistic-title">近七天调度统计</a-divider>
            <div id="scheduler-statistic-chart"/>
          </div>
        </div>
      </a-spin>
    </div>
  </div>
</template>

<script>
import { Chart } from '@antv/g2'

export default {
  name: 'SchedulerStatistics',
  data() {
    return {
      taskLoading: false,
      taskList: [],
      selectedTaskIds: [0],
      loading: false,
      chart: null,
      statisticMetrics: {
        scheduledCount: 0,
        successCount: 0,
        failureCount: 0,
        avgUsedInterval: 0
      }
    }
  },
  methods: {
    async getSchedulerTask() {
      this.taskLoading = true
      await this.$api.getSchedulerTaskList({
        limit: 10000
      }).then(({ data }) => {
        this.taskLoading = false
        this.taskList = data.rows || []
      }).catch(() => {
        this.taskLoading = false
      })
    },
    chooseTask(id) {
      this.selectedTaskIds = [id]
      this.getStatistic(id)
    },
    getStatistic(taskId) {
      this.loading = true
      this.$api.getSchedulerTaskStatistics({
        taskId
      }).then(({ data }) => {
        this.statisticMetrics.scheduledCount = data.scheduledCount
        this.statisticMetrics.successCount = data.successCount
        this.statisticMetrics.failureCount = data.failureCount
        this.statisticMetrics.avgUsedInterval = data.avgUsedInterval
        this.loading = false
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
          type: '调度次数',
          value: d.scheduledCount
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
        container: 'scheduler-statistic-chart',
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
    }
  },
  async mounted() {
    await this.getSchedulerTask()
    if (this.taskList.length) {
      this.chooseTask(this.taskList[0].id)
    }
  },
  beforeDestroy() {
    this.chart && this.chart.destroy()
  }
}
</script>

<style lang="less" scoped>

.scheduler-record-container {
  display: flex;

  .scheduler-task-menu {
    width: 216px;
    padding: 0 8px 8px 8px;
    margin-right: 16px;
    background-color: #FFF;
    border-radius: 4px;
  }

  .scheduler-task-statistic-container {
    width: calc(100% - 232px);
    background-color: #FFF;
    border-radius: 4px;
    min-height: calc(100vh - 84px);

    .scheduler-statistic-header-wrapper {
      display: flex;
      justify-content: center;
      margin: 24px 0 12px 16px;

      .scheduler-statistic-header {
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

.scheduler-statistic-title {
  margin: 36px 0;
}

.scheduler-statistic-chart-wrapper {
  margin: 0 24px 16px 24px;

  #scheduler-statistic-chart {
    height: calc(100vh - 228px);
  }
}

</style>
