<template>
  <a-spin :spinning="loading">
    <!-- 全部执行指标 -->
    <p class="statistics-description">全部执行指标</p>
    <div class="app-pipeline-statistic-metrics">
      <div class="clean"/>
      <!-- 统计指标 -->
      <div class="app-pipeline-statistic-header">
        <a-statistic class="statistic-metrics-item" title="执行次数" :value="allMetrics.execCount"/>
        <a-divider class="statistic-metrics-divider" type="vertical"/>
        <a-statistic class="statistic-metrics-item green" title="成功次数" :value="allMetrics.successCount"/>
        <a-divider class="statistic-metrics-divider" type="vertical"/>
        <a-statistic class="statistic-metrics-item red" title="失败次数" :value="allMetrics.failureCount"/>
        <a-divider class="statistic-metrics-divider" type="vertical"/>
        <a-statistic class="statistic-metrics-item" title="平均耗时" :value="allMetrics.avgUsedInterval"/>
      </div>
      <div class="clean"/>
    </div>
    <!-- 近七天执行指标 -->
    <p class="statistics-description" style="margin-top: 28px">近七天执行指标</p>
    <div class="app-pipeline-statistic-metrics">
      <div class="clean"/>
      <!-- 统计指标 -->
      <div class="app-pipeline-statistic-header">
        <a-statistic class="statistic-metrics-item" title="执行次数" :value="latelyMetrics.execCount"/>
        <a-divider class="statistic-metrics-divider" type="vertical"/>
        <a-statistic class="statistic-metrics-item green" title="成功次数" :value="latelyMetrics.successCount"/>
        <a-divider class="statistic-metrics-divider" type="vertical"/>
        <a-statistic class="statistic-metrics-item red" title="失败次数" :value="latelyMetrics.failureCount"/>
        <a-divider class="statistic-metrics-divider" type="vertical"/>
        <a-statistic class="statistic-metrics-item" title="平均耗时" :value="latelyMetrics.avgUsedInterval"/>
      </div>
      <div class="clean"/>
    </div>
  </a-spin>
</template>

<script>

export default {
  name: 'AppPipelineStatisticsMetrics',
  data() {
    return {
      loading: false,
      allMetrics: {
        execCount: 0,
        successCount: 0,
        failureCount: 0,
        avgUsedInterval: 0
      },
      latelyMetrics: {
        execCount: 0,
        successCount: 0,
        failureCount: 0,
        avgUsedInterval: 0
      }
    }
  },
  methods: {
    async init(pipelineId) {
      this.loading = true
      const { data } = await this.$api.getAppPipelineTaskStatisticsMetrics({
        pipelineId
      })
      this.loading = false
      this.allMetrics = data.all
      this.latelyMetrics = data.lately
    },
    clean() {
      this.loading = false
      this.allMetrics = {
        execCount: 0,
        successCount: 0,
        failureCount: 0,
        avgUsedInterval: 0
      }
      this.latelyMetrics = {
        execCount: 0,
        successCount: 0,
        failureCount: 0,
        avgUsedInterval: 0
      }
    }
  },
  beforeDestroy() {
    this.clean()
  }
}
</script>

<style lang="less" scoped>

.app-pipeline-statistic-metrics {
  display: flex;
  justify-content: center;
  margin: 24px 0 12px 16px;

  .app-pipeline-statistic-header {
    display: flex;

    .statistic-metrics-item {
      margin: 0 16px;
    }

    ::v-deep .statistic-metrics-item.green .ant-statistic-content {
      color: #58C612;
    }

    ::v-deep .statistic-metrics-item.red .ant-statistic-content {
      color: #DD2C00;
    }

    .statistic-metrics-divider {
      height: auto;
    }

    ::v-deep .ant-statistic-content {
      text-align: center;
    }
  }
}

</style>
