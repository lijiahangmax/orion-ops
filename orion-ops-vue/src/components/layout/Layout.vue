<template>
  <a-layout id="common-layout">
    <!-- 左侧 -->
    <a-layout-sider id="common-sider" v-model="collapsed" :trigger="null">
      <!--  <div class="logo"/> -->
      <!-- 左侧菜单 -->
      <Menu/>
    </a-layout-sider>
    <a-layout id="common-right">
      <!-- 头部菜单 -->
      <Header id="common-header"
              @changeFoldStatus="collapsed = !collapsed"
              @chooseProfile="chooseProfile"/>
      <!-- 主体部分 -->
      <a-layout-content id="common-content">
        <router-view ref="route"/>
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
      collapsed: false
    }
  },
  methods: {
    chooseProfile(e) {
      this.$refs.route.chooseProfile && this.$refs.route.chooseProfile(e)
    }
  }
}
</script>

<style lang="less" scoped>
#common-layout {
  height: 100vh;

  .logo {
    height: 32px;
    background: rgba(255, 255, 255, 0.2);
    margin: 16px;
  }

  #common-sider {
    overflow: auto
  }
}

#common-right {
  overflow: hidden;

  #common-content {
    padding: 18px;
    overflow: auto;
    flex: auto;
  }

  #common-header {
    z-index: 10;
    padding-right: 0;
    box-shadow: 0 1px 4px rgba(0, 21, 41, .08);
    height: 48px;

    /deep/ .ant-layout-header {
      height: 48px;
      line-height: 48px;
    }
  }
}

</style>
