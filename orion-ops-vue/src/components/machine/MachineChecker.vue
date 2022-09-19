<template>
  <a-popover v-model="visible"
             :destroyTooltipOnHide="true"
             trigger="click"
             overlayClassName="machine-content-list-popover"
             :placement="placement">
    <!-- 标题 -->
    <template #title>
      <div class="machine-title">
        <div v-if="machines.length" class="machine-title-left">
          <a-checkbox :indeterminate="indeterminate"
                      :checked="checkAll"
                      @change="chooseAllOnChange">
            全选
          </a-checkbox>
          <div class="line-input-wrapper">
            <input class="line-input"
                   v-model="filterKey"
                   placeholder="名称/IP过滤"
                   :disabled="loading">
          </div>
        </div>
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
              <a-checkbox class="machine-check-box"
                          v-for="(option, index) of machines"
                          v-show="option.visible"
                          :key="index"
                          :value="option.id"
                          :disabled="option.disabled"
                          :checked="option.checked"
                          @click="checkboxOnChange(option)">
                  <span class="machine-check-box-name">
                    {{ `${option.name} (${option.host})` }}
                  </span>
              </a-checkbox>
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
      empty: false,
      filterKey: undefined
    }
  },
  watch: {
    visible(e) {
      if (e && !this.init) {
        this.initData()
      }
    },
    filterKey() {
      this.filterMachineList()
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
          const machines = data.rows.map(s => {
            return {
              id: s.id,
              name: s.name,
              host: s.host,
              visible: true,
              checked: false,
              disabled: false
            }
          })
          // 设置禁用状态
          if (this.disableValue.length) {
            for (const machine of machines) {
              machine.disabled = this.disableValue.filter(s => s === machine.id).length !== 0
            }
          }
          // 设置默认选择状态
          if (this.defaultValue.length) {
            this.checkedList = [...this.defaultValue]
            for (const machine of machines) {
              machine.checked = this.defaultValue.filter(s => s === machine.id).length !== 0
            }
          }
          this.machines = machines
          // 同步全选状态
          this.syncCheckedAllStatus()
        } else {
          this.empty = true
        }
      }).catch(() => {
        this.loading = false
        this.init = false
      })
    },
    hide() {
      this.visible = false
    },
    checkboxOnChange(option) {
      // 修改单选状态
      option.checked = !option.checked
      if (option.checked) {
        this.checkedList.push(option.id)
      } else {
        this.removeToCheckedList(option.id)
      }
      // 同步全选状态
      this.syncCheckedAllStatus()
    },
    chooseAllOnChange() {
      this.checkAll = !this.checkAll
      this.indeterminate = false
      this.machines.forEach(machine => {
        if (machine.visible && !machine.disabled) {
          if (this.checkAll) {
            // 全选
            if (!machine.checked) {
              machine.checked = true
              this.checkedList.push(machine.id)
            }
          } else {
            // 反选
            if (machine.checked) {
              machine.checked = false
              this.removeToCheckedList(machine.id)
            }
          }
        }
      })
    },
    filterMachineList() {
      let visibleCount = 0
      for (const machine of this.machines) {
        if (!this.filterKey) {
          machine.visible = true
        } else {
          machine.visible = machine.name.includes(this.filterKey) || machine.host.includes(this.filterKey)
        }
        if (machine.visible) {
          visibleCount++
        }
      }
      // 同步全选状态
      this.syncCheckedAllStatus()
      // 空列表判断
      this.empty = visibleCount === 0
    },
    syncCheckedAllStatus() {
      const validMachines = this.machines.filter(s => s.visible).filter(s => !s.disabled)
      const checkValidMachineLen = validMachines.filter(s => s.checked).length
      this.checkAll = validMachines.length === checkValidMachineLen
      this.indeterminate = !(checkValidMachineLen === 0 || this.checkAll)
    },
    removeToCheckedList(id) {
      this.checkedList.forEach((item, index, arr) => {
        if (item === id) {
          arr.splice(index, 1)
        }
      })
    },
    clear() {
      this.checkAll = false
      this.indeterminate = false
      this.checkedList = []
      this.filterKey = undefined
      this.machines.forEach(machine => {
        machine.visible = true
        machine.checked = false
      })
    }
  }
}
</script>

<style lang="less" scoped>

.machine-title {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .machine-title-left {
    display: flex;
  }

  .line-input-wrapper {
    margin-left: 4px;
    width: 208px;
  }
}

.machine-list-wrapper {
  padding: 4px 2px 4px 16px;
  width: 350px;

  .machine-list {
    max-height: 130px;
    width: 100%;
    overflow: auto;

    .machine-check-box {
      display: flex;
      align-items: flex-end;
    }

    .machine-check-box-name {
      display: inline-block;
      width: calc(100% - 25px);
      white-space: nowrap;
    }
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

::v-deep .ant-checkbox-wrapper {
  padding: 2px 0;
  margin: 0 !important;
}
</style>
