<template>
  <a-spin :spinning="loading">
    <!-- 全部发布指标 -->
    <p class="statistics-description">全部发布指标</p>
    <div class="app-release-statistic-metrics">
      <div class="clean"/>
      <!-- 统计指标 -->
      <div class="app-release-statistic-header">
        <a-statistic class="statistic-metrics-item" title="发布次数" :value="allMetrics.releaseCount"/>
        <a-divider class="statistic-metrics-divider" type="vertical"/>
        <a-statistic class="statistic-metrics-item green" title="成功次数" :value="allMetrics.successCount"/>
        <a-divider class="statistic-metrics-divider" type="vertical"/>
        <a-statistic class="statistic-metrics-item red" title="失败次数" :value="allMetrics.failureCount"/>
        <a-divider class="statistic-metrics-divider" type="vertical"/>
        <a-statistic class="statistic-metrics-item" title="平均耗时" :value="allMetrics.avgUsedInterval"/>
      </div>
      <div class="clean"/>
    </div>
    <!-- 近七天发布指标 -->
    <p class="statistics-description" style="margin-top: 28px">近七天发布指标</p>
    <div class="app-release-statistic-metrics">
      <div class="clean"/>
      <!-- 统计指标 -->
      <div class="app-release-statistic-header">
        <a-statistic class="statistic-metrics-item" title="发布次数" :value="latelyMetrics.releaseCount"/>
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
  name: 'AppReleaseStatisticsMetrics',
  data() {
    return {
      loading: false,
      allMetrics: {
        releaseCount: 0,
        successCount: 0,
        failureCount: 0,
        avgUsedInterval: '0s'
      },
      latelyMetrics: {
        releaseCount: 0,
        successCount: 0,
        failureCount: 0,
        avgUsedInterval: '0s'
      }
    }
  },
  methods: {
    async init(appId, profileId) {
      this.loading = true
      const { data } = await this.$api.getAppReleaseStatisticsMetrics({
        appId,
        profileId
      })
      this.loading = false
      this.allMetrics = data.all
      this.latelyMetrics = data.lately
    },
    clean() {
      this.loading = false
      this.allMetrics = {
        releaseCount: 0,
        successCount: 0,
        failureCount: 0,
        avgUsedInterval: '0s'
      }
      this.latelyMetrics = {
        releaseCount: 0,
        successCount: 0,
        failureCount: 0,
        avgUsedInterval: '0s'
      }
    }
  },
  beforeDestroy() {
    this.clean()
  }
}
</script>

<style lang="less" scoped>

.app-release-statistic-metrics {
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

</style>
