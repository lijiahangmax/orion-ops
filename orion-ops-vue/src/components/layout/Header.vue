<template>
  <a-layout-header class="header-main">
    <!-- 头部左侧 -->
    <div class="header-fixed-left">
      <!-- 折叠 -->
      <a-icon class="trigger-icon header-block-container header-block-fold"
              :type="fold ? 'menu-unfold' : 'menu-fold'"
              :title="fold ? '展开' : '折叠'"
              @click="changeFold"/>
      <!-- 左侧配置 -->
      <a-icon class="trigger-icon header-block-container"
              v-for="(prop, index) of leftProps"
              :key="index"
              :title="prop.title"
              :type="prop.icon"
              @click="handlerCall(prop)"/>
    </div>
    <!-- 头部右侧 -->
    <div class="header-fixed-right">
      <!-- 环境选择 -->
      <HeaderProfileSelect id="header-profile-selector"
                           class="header-block-container"
                           v-show="profileSelectorVisible"
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
      leftProps: []
    }
  },
  methods: {
    changeFold() {
      this.fold = !this.fold
      this.$emit('changeFoldStatus')
    },
    handlerCall(prop) {
      prop.call && this[prop.call] && this[prop.call]()
      prop.event && this.$emit('onHeaderEvent', prop.event)
    },
    back() {
      this.$router.back(-1)
    },
    checkVisible(e = this.$route) {
      this.profileSelectorVisible = e.meta.visibleProfile === true
      this.leftProps = e.meta.leftProps || []
    }
  },
  created() {
    this.checkVisible()
  }
}
</script>

<style lang="less" scoped>

.header-main {
  background: #FFF;
  padding-left: 0;
  display: flex;
  justify-content: space-between;

  .header-fixed-left {
    display: flex;

    .header-block-fold {
      margin-left: 2px;
    }

    .trigger-icon {
      font-size: 18px;
      line-height: 48px;
      padding: 2px 16px;
      cursor: pointer;
      transition: color 0.3s;
    }

    .trigger-icon:hover {
      color: #1890FF;
    }
  }

  .header-fixed-right {
    display: flex;
    align-items: center;

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
      padding: 0 8px;
      margin-right: 2px;
      height: 48px;
      display: flex;
      align-items: center;
    }
  }
}

.header-block-container {
  transition: color 0.3s ease-in-out, background-color 0.3s ease-in-out;
  border-radius: 4px;
}

.header-block-container:hover {
  color: hsla(0, 0%, 100%, .2);
  background-color: #E7F5FF;
  color: #148EFF;
}

</style>
