<template>
  <a-layout-header class="header-main">
    <!-- 折叠 -->
    <a-icon class="fold-trigger" :type="fold ? 'menu-fold' : 'menu-unfold'" @click="changeFold"/>
    <div class="header-fixed-right">
      <!-- 环境选择 -->
      <HeaderProfileSelect style="margin-right: 20px"/>
      <!-- 用户下拉 -->
      <HeaderUser style="margin-right: 15px"/>
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
      fold: false
    }
  },
  methods: {
    changeFold() {
      this.fold = !this.fold
      this.$emit('changeFoldStatus')
    }
  }
}
</script>

<style scoped>

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

</style>
