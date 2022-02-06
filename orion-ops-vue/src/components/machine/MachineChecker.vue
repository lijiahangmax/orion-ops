<template>
  <a-popover v-model="visible"
             :destroyTooltipOnHide="true"
             trigger="click"
             overlayClassName="machine-content-list-popover"
             :placement="placement">
    <!-- 标题 -->
    <template #title>
      <div class="machine-title">
        <a-checkbox v-if="machines.length"
                    :indeterminate="indeterminate"
                    :checked="checkAll"
                    @change="chooseAll">
          全选
        </a-checkbox>
        <div v-else/>
        <a @click="hide">关闭</a>
      </div>
    </template>
    <!-- 内容 -->
    <template #content>
      <div class="machine-content">
        <a-spin :spinning="loading">
          <!-- 复选框 -->
          <div class="machine-list-wrapper" v-if="machines.length">
            <div class="machine-list">
              <a-checkbox-group v-model="checkedList" @change="onChange">
                <a-row v-for="(option, index) of machines" :key="index" style="margin: 4px 0">
                  <a-checkbox :value="option.id" :disabled="checkDisabled(option.id)">
                    {{ `${option.name} (${option.host})` }}
                  </a-checkbox>
                </a-row>
              </a-checkbox-group>
            </div>
          </div>
          <div class="machine-list-empty" v-if="empty">
            <a-empty/>
          </div>
          <!-- 分割线 -->
          <a-divider class="content-divider"/>
          <!-- 底部栏 -->
          <div class="machine-button-tools">
            <slot name="footer"/>
          </div>
        </a-spin>
      </div>
    </template>
    <!-- 触发器 -->
    <slot name="trigger"/>
  </a-popover>
</template>
<script>

export default {
  name: 'MachineChecker',
  props: {
    placement: {
      type: String,
      default: 'bottomLeft'
    },
    defaultValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    disableValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    query: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    return {
      visible: false,
      init: false,
      indeterminate: false,
      checkAll: false,
      loading: false,
      checkedList: [],
      machines: [],
      empty: false
    }
  },
  watch: {
    visible(e) {
      if (e && !this.init) {
        this.initData()
      }
    }
  },
  methods: {
    initData() {
      this.loading = true
      this.init = true
      this.$api.getMachineList({
        ...this.query,
        limit: 10000
      }).then(({ data }) => {
        this.loading = false
        if (data && data.rows && data.rows.length) {
          this.machines = data.rows.map(s => {
            return {
              id: s.id,
              name: s.name,
              host: s.host
            }
          })
        } else {
          this.empty = true
        }
        // 设置默认选择状态
        if (this.defaultValue.length) {
          this.checkedList = this.defaultValue
          if (this.machines.length === this.checkedList.length) {
            this.checkAll = true
            this.indeterminate = false
          } else {
            this.indeterminate = true
          }
        }
      }).catch(() => {
        this.loading = false
        this.init = false
      })
    },
    hide() {
      this.visible = false
    },
    onChange(checkedList) {
      this.indeterminate = !!checkedList.length && checkedList.length < this.machines.length - this.disableValue.length
      this.checkAll = checkedList.length === this.machines.length - this.disableValue.length
    },
    chooseAll(e) {
      Object.assign(this, {
        checkedList: e.target.checked
          ? this.machines.map(d => d.id).filter(id => !this.checkDisabled(id))
          : [],
        indeterminate: false,
        checkAll: e.target.checked
      })
    },
    checkDisabled(id) {
      for (const disable of this.disableValue) {
        if (disable === id) {
          return true
        }
      }
      return false
    },
    clear() {
      this.checkedList = []
      this.checkAll = false
      this.indeterminate = false
    }
  }
}
</script>

<style lang="less" scoped>

.machine-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.machine-list-wrapper {
  padding: 4px 2px 4px 16px;
  min-width: 350px;

  .machine-list {
    max-height: 130px;
    width: 100%;
    overflow-y: auto;
  }
}

.machine-list-empty {
  padding: 4px 0;
}

.content-divider {
  margin: 0;
}

.machine-button-tools {
  padding: 4px;
  display: flex;
  justify-content: flex-end;
}

</style>
