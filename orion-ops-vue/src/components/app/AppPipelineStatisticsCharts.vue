<template>
  <!-- 统计折线图 -->
  <div class="statistic-chart-container">
    <p class="statistics-description">近七天执行统计</p>
    <a-spin :spinning="loading">
      <!-- 折线图 -->
      <div class="statistic-chart-wrapper">
        <div id="statistic-chart"/>
      </div>
    </a-spin>
  </div>
</template>

<script>
import { Chart } from '@antv/g2'

export default {
  name: 'AppPipelineStatisticsCharts',
  data() {
    return {
      loading: false,
      chart: null
    }
  },
  methods: {
    async init(pipelineId) {
      this.loading = true
      const { data } = await this.$api.getAppPipelineTaskStatisticsChart({
        pipelineId
      })
      this.loading = false
      this.renderChart(data)
    },
    clean() {
      this.loading = false
      this.chart && this.chart.destroy()
      this.chart = null
    },
    renderChart(data) {
      // 处理数据
      const chartsData = []
      for (const d of data) {
        chartsData.push({
          date: d.date,
          type: '执行次数',
          value: d.execCount
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
      this.clean()
      // 渲染图表
      this.chart = new Chart({
        container: 'statistic-chart',
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
  beforeDestroy() {
    this.clean()
  }
}
</script>

<style lang="less" scoped>

.statistic-chart-wrapper {
  margin: 0 24px 16px 24px;

  #statistic-chart {
    height: 500px;
  }
}

</style>
