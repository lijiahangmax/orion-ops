<template>
  <div class="app-release-statistics-record-container">
    <!-- 应用列表菜单 -->
    <div class="app-list-menu gray-box-shadow">
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
    <div class="app-release-statistics-container gray-box-shadow">
      <div class="app-release-tabs-wrapper" v-show="profileId && appList.length">
        <a-tabs v-model="activeTab" :animated="false" @change="chooseStatisticsTab">
          <!-- 视图 -->
          <a-tab-pane key="1" tab="视图">
            <AppReleaseStatisticsViews ref="view"/>
          </a-tab-pane>
          <!-- 指标 -->
          <a-tab-pane key="2" tab="指标">
            <AppReleaseStatisticsMetrics ref="metrics"/>
          </a-tab-pane>
          <!-- 图表 -->
          <a-tab-pane key="3" tab="图表">
            <AppReleaseStatisticsCharts ref="chart"/>
          </a-tab-pane>
        </a-tabs>
      </div>
      <p v-if="!appLoading && (!profileId || !appList.length)" style="padding: 16px;">请先维护应用</p>
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
  name: 'AppReleaseStatistics',
  components: {
    AppReleaseStatisticsMetrics: () => import('@/components/app/AppReleaseStatisticsMetrics'),
    AppReleaseStatisticsViews: () => import('@/components/app/AppReleaseStatisticsViews'),
    AppReleaseStatisticsCharts: () => import('@/components/app/AppReleaseStatisticsCharts')
  },
  data() {
    return {
      appLoading: false,
      appList: [],
      selectedAppIds: [0],
      profileId: null,
      activeTab: '1',
      pollId: null
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
      this.$refs.view && this.$refs.view.clean()
      this.$refs.metrics && this.$refs.metrics.clean()
      this.$refs.chart && this.$refs.chart.clean()
      this.selectedAppIds = [id]
      this.chooseStatisticsTab()
    },
    chooseProfile({ id }) {
      this.profileId = id
      this.chooseApp(this.selectedAppIds[0])
    },
    chooseStatisticsTab() {
      setTimeout(() => {
        refKey[this.activeTab].call(this).init(this.selectedAppIds[0], this.profileId)
      })
    },
    refreshView() {
      if (this.activeTab !== '1') {
        return
      }
      this.$refs.view && this.$refs.view.refresh(this.selectedAppIds[0], this.profileId)
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

.app-release-statistics-record-container {
  display: flex;

  .app-list-menu {
    width: 216px;
    padding: 0 8px 8px 8px;
    margin-right: 16px;
    background-color: #FFF;
    border-radius: 2px;
  }

  .app-release-statistics-container {
    width: calc(100% - 232px);
    background-color: #FFF;
    border-radius: 2px;
    min-height: calc(100vh - 84px);

    .app-release-tabs-wrapper {
      padding: 8px;
    }
  }
}

</style>
