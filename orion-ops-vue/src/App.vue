<template>
  <a-config-provider :locale="locale">
    <div id="app">
      <router-view/>
    </div>
  </a-config-provider>
</template>

<script>
  import '../src/css/common.less'
  import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'

  export default {
    name: 'App',
    data: function() {
      return {
        locale: zhCN
      }
    }
  }

</script>
