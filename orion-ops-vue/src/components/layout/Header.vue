<template>
  <a-layout-header class="header-main">
    <a-icon
      class="fold-trigger"
      :type="fold ? 'menu-fold' : 'menu-unfold'"
      @click="changeFold()"
    />
    <div>
      <HeaderProfileSelect style="margin-right: 20px"/>
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
        fold: false,
        size: 200,
        end: 'end'
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

  .fold-trigger {
    font-size: 18px;
    line-height: 64px;
    padding: 0 24px;
    cursor: pointer;
    transition: color 0.3s;
  }

  .fold-trigger :hover {
    color: #1890ff;
  }

  .header-main {
    background: #FFF;
    padding-left: 0;
    padding-right: 20px;
    display: flex;
    justify-content: space-between;
  }

</style>
