<template>
  <!-- 日志列表 -->
  <div class="simple-event-log-container">
    <EventLogList :baseQuery="{result: 1, userId: $route.params.id}"/>
  </div>
</template>

<script>
import EventLogList from '@/components/user/EventLogList'

export default {
  name: 'SimpleUserEventLogList',
  components: { EventLogList }
}
</script>

<style lang="less" scoped>
.simple-event-log-container {
  background: #FFF;
  padding: 8px 16px 16px 16px;
  border-radius: 4px;
}

</style>
