<template>
  <a-drawer
    title="流水线详情"
    placement="right"
    :maskStyle="{opacity: 0, animation: 'none'}"
    :closable="false"
    :visible="visible"
    :width="350"
    @close="onClose">
    <!-- 加载中 -->
    <a-skeleton v-if="loading" active :paragraph="{rows: 6}"/>
    <!-- 描述 -->
    <a-descriptions v-else size="middle">
      <a-descriptions-item label="流水线名称" :span="3">
        {{ record.name }}
      </a-descriptions-item>
      <a-descriptions-item label="流水线描述" :span="3">
        {{ record.description }}
      </a-descriptions-item>
      <a-descriptions-item label="创建时间" :span="3">
        {{ record.createTime | formatDate }}
      </a-descriptions-item>
      <a-descriptions-item label="修改时间" :span="3">
        {{ record.updateTime | formatDate }}
      </a-descriptions-item>
      <!-- 操作流水线 -->
      <a-descriptions-item :span="3">
        <a-timeline style="margin-top: 16px">
          <a-timeline-item v-for="detail of record.details" :key="detail.id">
            <!-- 操作 -->
            <span class="mr4 span-blue">{{ detail.stageType | formatStageType('label') }}</span>
            <!-- 应用名称 -->
            <span>{{ detail.appName }}</span>
          </a-timeline-item>
        </a-timeline>
      </a-descriptions-item>
    </a-descriptions>
  </a-drawer>
</template>

<script>
import { formatDate } from '@/lib/filters'
import { enumValueOf, STAGE_TYPE } from '@/lib/enum'

export default {
  name: 'AppPipelineDetailViewDrawer',
  data() {
    return {
      visible: false,
      loading: false,
      record: {}
    }
  },
  methods: {
    open(id) {
      this.record = {}
      this.visible = true
      this.loading = true
      this.$api.getAppPipelineDetail({
        id
      }).then(({ data }) => {
        this.record = data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    onClose() {
      this.visible = false
    }
  },
  filters: {
    formatDate,
    formatStageType(status, f) {
      return enumValueOf(STAGE_TYPE, status)[f]
    }
  }
}
</script>

<style scoped>

</style>
