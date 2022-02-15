<template>
  <div class="statistics-container">
    <!-- 顶部统计 -->
    <div class="statistics-top">
      <a-row>
        <a-col :span="6" class="pr8">
          <a-card :bordered="false">
            <a-statistic title="机器数量" suffix="台">
              <template #formatter>
                <a href="#/machine/list" title="跳转">
                  {{ count.machineCount }}
                </a>
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span="6" class="px8">
          <a-card :bordered="false">
            <a-statistic title="环境数量" suffix="个">
              <template #formatter>
                <a href="#/app/profile" title="跳转">
                  {{ count.profileCount }}
                </a>
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span="6" class="px8">
          <a-card :bordered="false">
            <a-statistic title="应用数量" suffix="个">
              <template #formatter>
                <a href="#/app/list" title="跳转">
                  {{ count.appCount }}
                </a>
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span="6" class="pl8">
          <a-card :bordered="false">
            <a-statistic title="日志数量" suffix="个">
              <template #formatter>
                <a href="#/log/list" title="跳转">
                  {{ count.logCount }}
                </a>
              </template>
            </a-statistic>
          </a-card>
        </a-col>
      </a-row>
    </div>
    <!-- 柱状统计 -->
    <div class="statistics-chart-container" v-if="false">
    </div>
  </div>
</template>

<script>
export default {
  name: 'Console',
  data() {
    return {
      count: {
        machineCount: '-',
        profileCount: '-',
        appCount: '-',
        logCount: '-'
      }
    }
  },
  methods: {},
  mounted() {
    // 加载统计信息
    this.$api.getHomeStatistics()
      .then(({ data }) => {
        this.count = data.count
      })
  }
}
</script>

<style lang="less" scoped>

.statistics-top {
  margin-bottom: 18px;
}

</style>
