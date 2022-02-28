<template>
  <div class="user-detail-container">
    <a-tabs default-active-key="1" tab-position="left">
      <!-- 系统分析 -->
      <a-tab-pane key="1" tab="系统分析">
        <SystemAnalysis/>
      </a-tab-pane>
      <!-- 安全设置 -->
      <a-tab-pane key="2" tab="安全设置">
        <SecurityConfig/>
      </a-tab-pane>
      <!-- IP 操作 -->
      <a-tab-pane key="3" tab="IP名单配置">
        <IpConfig/>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>

import SystemAnalysis from '@/components/system/SystemAnalysis'

export default {
  name: 'SystemSetting',
  components: {
    SystemAnalysis,
    SecurityConfig: () => import('@/components/system/SecurityConfig'),
    IpConfig: () => import('@/components/system/IpConfig')
  }
}
</script>

<style lang="less" scoped>

.user-detail-container {
  background: #FFF;
  padding: 8px 16px;
  border-radius: 4px;
}
</style>
