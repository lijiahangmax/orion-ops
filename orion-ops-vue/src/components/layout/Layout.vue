<template>
  <a-layout id="common-layout" v-if="validToken">
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
        <a-spin :spinning="globalLoading" :tip="globalLoadingTip">
          <router-view ref="route"
                       :key="$route.fullPath"
                       @reloadProfile="reloadProfile"
                       @openLoading="openLoading"
                       @closeLoading="closeLoading"/>
        </a-spin>
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
      collapsed: false,
      validToken: false,
      globalLoading: false,
      globalLoadingTip: null
    }
  },
  methods: {
    chooseProfile(e) {
      this.$refs.route && this.$refs.route.chooseProfile && this.$refs.route.chooseProfile(e)
    },
    onHeaderEvent(e) {
      this.$refs.route && this.$refs.route.onHeaderEvent && this.$refs.route.onHeaderEvent(e)
    },
    reloadProfile() {
      this.$refs.header.reloadProfile()
    },
    openLoading(tip = null) {
      this.globalLoading = true
      this.globalLoadingTip = tip
    },
    closeLoading() {
      this.globalLoading = false
      this.globalLoadingTip = ''
    }
  },
  async beforeCreate() {
    if (this.$getUserId()) {
      await this.$api.validToken().then(() => {
        this.validToken = true
      }).catch(() => {
        this.validToken = false
      })
    } else {
      this.validToken = false
    }
    if (!this.validToken) {
      this.$storage.clear()
      this.$storage.clearSession()
      this.$router.push('/login')
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
    overflow: auto;
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
  }
}

::-webkit-scrollbar {
  display: none;
}

</style>
