<template>
  <div class="thread-metrics-container">
    <div class="other-title normal-title">
      线程池指标
    </div>
    <div class="thread-metrics-descriptions">
      <a-descriptions>
        <!-- 指标 -->
        <a-descriptions-item v-for="metric of metrics"
                             :key="metric.type"
                             :label="metric.type | formatThreadType('label')"
                             :span="3">
          <div class="thread-pool-metrics-wrapper">
            <span class="metrics-label">活跃线程数: </span>
            <span class="metrics-value-wrapper">
              <span class="metrics-value">{{ metric.activeThreadCount }}</span> 个
            </span>
            <span class="metrics-label">总任务数: </span>
            <span class="metrics-value-wrapper">
              <span class="metrics-value">{{ metric.totalTaskCount }}</span> 个
            </span>
            <span class="metrics-label">已处理任务数: </span>
            <span class="metrics-value-wrapper">
              <span class="metrics-value">{{ metric.completedTaskCount }}</span> 个
            </span>
            <span class="metrics-label">队列任务数: </span>
            <span class="metrics-value-wrapper">
              <span class="metrics-value">{{ metric.queueSize }}</span> 个
            </span>
          </div>
        </a-descriptions-item>
      </a-descriptions>
    </div>
    <!-- 操作 -->
    <div class="thread-metrics-handler-container">
      <!-- 重新加载 -->
      <a-button class="reload-button" :loading="loading" @click="init">
        重新加载
      </a-button>
    </div>
  </div>
</template>

<script>
import { enumValueOf, THREAD_POOL_METRICS_TYPE } from '@/lib/enum'

export default {
  name: 'ThreadMetrics',
  data() {
    return {
      loading: false,
      metrics: []
    }
  },
  methods: {
    init() {
      this.loading = true
      this.$api.getSystemThreadMetrics().then(({ data }) => {
        this.loading = false
        this.metrics = data
      }).catch(() => {
        this.loading = false
      })
    }
  },
  filters: {
    formatThreadType(type, f) {
      return enumValueOf(THREAD_POOL_METRICS_TYPE, type)[f]
    }
  },
  mounted() {
    this.init()
  }
}
</script>

<style lang="less" scoped>

.other-title {
  margin: 16px 0 16px 16px;
}

.thread-metrics-descriptions {
  margin: 18px 0 0 0;
}

/deep/ .ant-descriptions-item-label {
  margin-left: 16px;
  width: 145px;
  text-align: end;
}

.thread-pool-metrics-wrapper {
  margin-left: 8px;
  font-size: 13px;
}

.metrics-value-wrapper {
  margin-right: 8px;
  width: 48px;
  display: inline-block;

  .metrics-value {
    color: #1890FF;
  }
}

.reload-button {
  margin: 8px 0 24px 18px;
}

</style>
