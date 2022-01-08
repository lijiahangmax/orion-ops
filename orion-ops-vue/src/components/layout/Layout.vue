<template>
  <a-layout id="common-layout">
    <!-- 左侧 -->
    <a-layout-sider id="common-sider" v-model="collapsed" :trigger="null">
      <!--  <div class="logo"/> -->
      <!-- 左侧菜单 -->
      <Menu ref="menu"/>
    </a-layout-sider>
    <a-layout id="common-right">
      <!-- 头部菜单 -->
      <Header id="common-header"
              ref="header"
              @changeFoldStatus="collapsed = !collapsed"
              @chooseProfile="chooseProfile"
              @onHeaderEvent="onHeaderEvent"/>
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
  watch: {
    $route(e) {
      // 设置菜单选中
      this.$refs.menu.chooseMenu(e)
      // 设置头部按钮
      this.$refs.header.checkVisible(e)
    }
  },
  data() {
    return {
      collapsed: false
    }
  },
  methods: {
    chooseProfile(e) {
      this.$refs.route && this.$refs.route.chooseProfile && this.$refs.route.chooseProfile(e)
    },
    onHeaderEvent(e) {
      this.$refs.route && this.$refs.route.onHeaderEvent && this.$refs.route.onHeaderEvent(e)
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
