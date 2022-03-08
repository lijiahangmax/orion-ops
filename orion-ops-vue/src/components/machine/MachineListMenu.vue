<template>
  <a-layout-sider :theme="theme" id="machine-list-fixed-left" :collapsible="true" @collapse="changeCollapse">
    <a-spin :spinning="loading">
      <!-- 工具按钮 -->
      <div class="machine-tools">
        <div :class="['machine-tools-item', theme]" title="刷新" @click="getMachineList">
          <a-icon type="reload"/>
        </div>
      </div>
      <!-- 机器列表菜单 -->
      <a-menu :theme="theme" mode="inline" :selectedKeys="selectedMachine">
        <a-menu-item v-for="item of list" :key="item.id" :title="item.host" @dblclick="chooseMachine(item.id)">
          <a-icon type="desktop"/>
          <span class="usn">{{ item.name }}</span>
        </a-menu-item>
      </a-menu>
    </a-spin>
  </a-layout-sider>
</template>

<script>
export default {
  name: 'MachineListMenu',
  props: {
    theme: String,
    selectedMachine: Array,
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
    },
    chooseMachine(id) {
      this.selectedMachine[0] = id
      this.$forceUpdate()
      this.$emit('chooseMachine', id)
    },
    changeCollapse(e) {
      this.$emit('changeCollapse', e)
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

    .machine-tools-item.light {
      background: #118AFA;
      color: #FFFFFF;
    }

    .machine-tools-item.dark {
      color: hsla(0, 0%, 100%, 0.65);
    }

    .machine-tools-item:hover {
      background-color: #1890FF;
      color: #FFFFFF;
    }
  }
}
</style>
