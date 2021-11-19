<template>
  <a-result status="404" title="404" sub-title="页面不存在">
    <template #extra>
      <a-button type="primary" @click="() => $router.push({path: '/'})">
        返回
      </a-button>
    </template>
  </a-result>
</template>

<script>
export default {
  name: 'Page404'
}
</script>

<style scoped>

</style>
