<template>
  <a-layout id="common-layout">
    <a-layout-sider v-model="collapsed" :trigger="null" :style="this.style.sider">
      <!--  <div class="logo"/> -->
      <Menu/>
    </a-layout-sider>
    <a-layout>
      <Header @changeFoldStatus="collapsed = !collapsed"/>
      <a-layout-content :style="this.style.content">
        <router-view/>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script>
  import Menu from './Menu'
  import Header from './Header'

  export default {
    components: {
      Menu,
      Header
    },
    data() {
      return {
        collapsed: false,
        style: {
          sider: {
            overflow: 'auto'
          },
          content: {
            overflow: 'auto',
            margin: '12px',
            padding: '12px',
            background: '#FFF',
            flex: 'none'
          }
        }
      }
    }
  }
</script>

<style scoped>
  #common-layout {
    height: 100vh;
  }

  #common-layout .logo {
    height: 32px;
    background: rgba(255, 255, 255, 0.2);
    margin: 16px;
  }
</style>
