<template>
  <a-layout-sider id="machine-list-fixed-left">
    <a-spin :spinning="loading">
      <!-- 工具按钮 -->
      <div class="machine-tools">
        <div class="machine-tools-item" v-if="!hideBack" title="返回" @click="backHistory">
          <a-icon type="arrow-left"/>
        </div>
        <div class="machine-tools-item" title="刷新" @click="getMachineList">
          <a-icon type="reload"/>
        </div>
      </div>
      <!-- 列表菜单 -->
      <a-menu theme="dark" mode="inline" :defaultSelectedKeys="selectedMachine">
        <a-menu-item v-for="item of list" :key="item.id" :title="item.host" @click="$emit('chooseMachine', item.id)">
          <a-icon type="desktop"/>
          <span class="nav-text">{{ item.name }}</span>
        </a-menu-item>
      </a-menu>
    </a-spin>
  </a-layout-sider>
</template>

<script>
export default {
  name: 'MachineListMenu',
  props: {
    selectedMachine: Array,
    hideBack: Boolean,
    query: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    return {
      loading: false,
      list: []
    }
  },
  methods: {
    backHistory() {
      this.$router.back(-1)
    },
    async getMachineList() {
      this.loading = true
      try {
        const machineListRes = await this.$api.getMachineList({
          ...this.query,
          limit: 10000
        })
        this.list = machineListRes.data.rows.map(i => {
          return {
            id: i.id,
            tag: i.tag,
            name: i.name,
            host: i.host
          }
        })
      } catch (e) {
        // ignore
      }
      this.loading = false
    }
  }
}
</script>

<style lang="less" scoped>
#machine-list-fixed-left {
  overflow: auto;

  .machine-tools {
    height: 32px;
    margin: 8px 8px 4px 8px;
    display: flex;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 4px;

    .machine-tools-item {
      width: 100%;
      height: 100%;
      color: hsla(0, 0%, 100%, 0.65);
      display: flex;
      align-items: center;
      justify-content: center;
      transition: color 0.3s ease-in-out, background-color 0.3s ease-in-out;
      border-radius: 4px;
      cursor: pointer;

      i {
        font-size: 20px;
      }
    }

    .machine-tools-item:hover {
      color: hsla(0, 0%, 100%, .2);
      background-color: #1890ff;
      color: #fff;
    }
  }
}
</style>
