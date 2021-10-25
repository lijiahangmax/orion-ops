<template>
  <a-config-provider :locale="locale">
    <div id="app">
      <router-view v-if="disableLayout()"/>
      <Layout v-else/>
    </div>
  </a-config-provider>
</template>

<script>
  import Layout from '@/components/Layout.vue'
  import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'

  export default {
    name: 'App',
    data: function() {
      return {
        locale: zhCN
      }
    },
    components: {
      Layout
    },
    methods: {
      disableLayout: function() {
        return this.$route.meta.disableLayout
      }
    }
  }

</script>
<style lang="less">
  #app {
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  ::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, .1);
    border-radius: 0;
  }

  ::-webkit-scrollbar {
    -webkit-appearance: none;
    width: 7px;
    height: 7px;
  }

  ::-webkit-scrollbar-thumb {
    cursor: pointer;
    border-radius: 5px;
    background: #ADB5BD;
    transition: color .2s ease;
  }

</style>
