<template>
  <a-layout-header class="header-main">
    <!-- 折叠 -->
    <a-icon class="fold-trigger header-block-container"
            :type="fold ? 'menu-fold' : 'menu-unfold'"
            @click="changeFold"/>
    <div class="header-fixed-right">
      <!-- 环境选择 -->
      <HeaderProfileSelect id="header-profile-selector"
                           class="header-block-container"
                           v-if="profileSelectorVisible"
                           @chooseProfile="(profile) => $emit('chooseProfile', profile)"/>
      <!-- 用户下拉 -->
      <HeaderUser id="header-user" class="header-block-container"/>
    </div>
  </a-layout-header>
</template>

<script>

import HeaderProfileSelect from './HeaderProfileSelect'
import HeaderUser from './HeaderUser'

export default {
  name: 'Header',
  components: {
    HeaderProfileSelect,
    HeaderUser
  },
  data: function() {
    return {
      fold: false,
      profileSelectorVisible: false,
      visiblePath: ['/app/env', '/app/list']
    }
  },
  watch: {
    $route(e) {
      this.checkProfileSelectorVisible(e)
    }
  },
  methods: {
    changeFold() {
      this.fold = !this.fold
      this.$emit('changeFoldStatus')
    },
    checkProfileSelectorVisible(e = this.$route) {
      for (const path of this.visiblePath) {
        if (path === e.path) {
          this.profileSelectorVisible = true
          return
        }
      }
      this.profileSelectorVisible = false
    }
  },
  created() {
    this.checkProfileSelectorVisible()
  }
}
</script>

<style lang="less" scoped>

.header-main {
  background: #FFF;
  padding-left: 0;
  display: flex;
  justify-content: space-between;
}

.fold-trigger {
  font-size: 18px;
  line-height: 48px;
  padding: 2px 24px;
  cursor: pointer;
  transition: color 0.3s;
}

.fold-trigger :hover {
  color: #1890ff;
}

.header-fixed-right {
  display: flex;
  align-items: center;
}

#header-profile-selector {
  padding: 0 10px;
  height: 48px;
  display: flex;
  align-items: center;
  font-size: 16px;
  line-height: 18px;

  /deep/ i {
    padding-left: 4px;
    margin-top: 4px;
  }

}

#header-user {
  padding: 0 12px;
  margin-right: 8px;
  height: 48px;
  display: flex;
  align-items: center;
}

.header-block-container {
  transition: color 0.3s ease-in-out, background-color 0.3s ease-in-out;
  border-radius: 4px;
}

.header-block-container:hover {
  color: hsla(0, 0%, 100%, .2);
  background-color: #E7F5FF;
  color: #148eff;
}

</style>
