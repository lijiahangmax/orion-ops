<template>
  <div class="pipeline-statistics-record-container">
    <!-- 流水线列表菜单 -->
    <div class="pipeline-list-menu">
      <!-- 流水线列表表头 -->
      <div class="pipeline-list-menu-header">
        <a-page-header @back="() => {}">
          <template #title>
            <span class="ant-page-header-heading-title pointer px0" title="刷新" @click="getPipelineList">流水线列表</span>
          </template>
          <template #backIcon>
            <a-icon type="reload" title="刷新" @click="getPipelineList"/>
          </template>
        </a-page-header>
      </div>
      <!-- 流水线菜单 -->
      <a-spin :spinning="pipelineLoading">
        <div class="pipeline-list-wrapper">
          <a-menu mode="inline" v-model="selectedPipelineIds">
            <a-menu-item v-for="pipeline in pipelineList" :key="pipeline.id" :title="pipeline.name" @click="choosePipeline(pipeline.id)">
              <a-icon type="apartment"/>
              {{ pipeline.name }}
            </a-menu-item>
          </a-menu>
        </div>
      </a-spin>
    </div>
    <!-- 构建明细 -->
    <div class="pipeline-statistics-container">
      <div class="statistic-tabs-wrapper" v-show="profileId && pipelineList.length">
        <a-tabs v-model="activeTab" :animated="false" @change="chooseStatisticsTab">
          <!-- 视图 -->
          <a-tab-pane key="1" tab="视图">
            <AppPipelineStatisticsViews ref="view"/>
          </a-tab-pane>
          <!-- 指标 -->
          <a-tab-pane key="2" tab="指标">
            <AppPipelineStatisticsMetrics ref="metrics"/>
          </a-tab-pane>
          <!-- 图表 -->
          <a-tab-pane key="3" tab="图表">
            <AppPipelineStatisticsCharts ref="chart"/>
          </a-tab-pane>
        </a-tabs>
      </div>
      <p v-if="!pipelineLoading && (!profileId || !pipelineList.length)" style="padding: 16px;">请先维护应用流水线</p>
    </div>
  </div>
</template>

<script>
/**
 * tab 对应的 ref
 */
const refKey = {
  1: function() {
    return this.$refs.view
  },
  2: function() {
    return this.$refs.metrics
  },
  3: function() {
    return this.$refs.chart
  }
}

export default {
  name: 'AppPipelineStatistics',
  components: {
    AppPipelineStatisticsViews: () => import('@/components/app/AppPipelineStatisticsViews'),
    AppPipelineStatisticsMetrics: () => import('@/components/app/AppPipelineStatisticsMetrics'),
    AppPipelineStatisticsCharts: () => import('@/components/app/AppPipelineStatisticsCharts')
  },
  data() {
    return {
      pipelineLoading: false,
      pipelineList: [],
      selectedPipelineIds: [0],
      profileId: null,
      activeTab: '1',
      pollId: null
    }
  },
  methods: {
    async getPipelineList() {
      this.pipelineLoading = true
      await this.$api.getAppPipelineList({
        profileId: this.profileId,
        limit: 10000
      }).then(({ data }) => {
        this.pipelineLoading = false
        this.pipelineList = data.rows || []
      }).catch(() => {
        this.pipelineLoading = false
      })
    },
    choosePipeline(id) {
      this.cleanData()
      this.selectedPipelineIds = [id]
      this.chooseStatisticsTab()
    },
    async chooseProfile({ id }) {
      this.profileId = id
      await this.getPipelineList()
      if (this.pipelineList.length) {
        this.choosePipeline(this.pipelineList[0].id)
      } else {
        this.cleanData()
      }
    },
    cleanData() {
      this.$refs.view && this.$refs.view.clean()
      this.$refs.metrics && this.$refs.metrics.clean()
      this.$refs.chart && this.$refs.chart.clean()
    },
    chooseStatisticsTab() {
      setTimeout(() => {
        refKey[this.activeTab].call(this).init(this.selectedPipelineIds[0])
      })
    },
    refreshView() {
      if (this.activeTab !== '1') {
        return
      }
      this.$refs.view && this.$refs.view.refresh(this.selectedPipelineIds[0])
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
    // 加载流水线列表
    await this.getPipelineList()
    if (this.pipelineList.length) {
      this.choosePipeline(this.pipelineList[0].id)
      // 加载轮询
      this.pollId = setInterval(this.refreshView, 5000)
    }
  },
  beforeDestroy() {
    this.pollId !== null && clearInterval(this.pollId)
    this.pollId = null
  }
}
</script>

<style lang="less" scoped>

.pipeline-statistics-record-container {
  display: flex;

  .pipeline-list-menu {
    width: 216px;
    padding: 0 8px 8px 8px;
    margin-right: 16px;
    background-color: #FFF;
    border-radius: 4px;
  }

  .pipeline-statistics-container {
    width: calc(100% - 232px);
    background-color: #FFF;
    border-radius: 4px;
    min-height: calc(100vh - 84px);

    .statistic-tabs-wrapper {
      padding: 8px;
    }
  }
}

</style>
