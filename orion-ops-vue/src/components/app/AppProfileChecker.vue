<template>
  <a-popover v-model="visible"
             :destroyTooltipOnHide="true"
             trigger="click"
             overlayClassName="profile-content-list-popover"
             placement="bottom">
    <!-- 标题 -->
    <template #title>
      <div class="profile-title">
        <a-checkbox v-if="profiles.length"
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
      <div class="profile-content">
        <a-spin :spinning="loading">
          <!-- 复选框 -->
          <div class="profile-list-wrapper" v-if="profiles.length">
            <div class="profile-list">
              <a-checkbox-group v-model="checkedList" @change="onChange">
                <a-row v-for="(option, index) of profiles" :key="index" style="margin: 4px 0">
                  <a-checkbox :value="option.id" :disabled="checkDisabled(option.id)">
                    {{ option.name }}
                  </a-checkbox>
                </a-row>
              </a-checkbox-group>
            </div>
          </div>
          <div class="profile-list-empty" v-if="empty">
            <a-empty/>
          </div>
          <!-- 分割线 -->
          <a-divider class="content-divider"/>
          <!-- 底部栏 -->
          <div class="profile-button-tools">
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
  name: 'AppProfileChecker',
  data() {
    return {
      visible: false,
      init: false,
      indeterminate: false,
      checkAll: false,
      loading: false,
      checkedList: [],
      profiles: [],
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
      this.$api.getProfileList()
        .then(({ data }) => {
          this.loading = false
          if (data && data.length) {
            this.profiles = data.map(s => {
              return {
                id: s.id,
                name: s.name
              }
            })
          } else {
            this.empty = true
          }
        })
        .catch(() => {
          this.loading = false
          this.init = false
        })
    },
    hide() {
      this.visible = false
    },
    onChange(checkedList) {
      this.indeterminate = !!checkedList.length && checkedList.length < this.profiles.length - 1
      this.checkAll = checkedList.length === this.profiles.length - 1
    },
    chooseAll(e) {
      Object.assign(this, {
        checkedList: e.target.checked
          ? this.profiles.map(d => d.id).filter(id => !this.checkDisabled(id))
          : [],
        indeterminate: false,
        checkAll: e.target.checked
      })
    },
    checkDisabled(id) {
      const activeProfile = this.$storage.get(this.$storage.keys.ACTIVE_PROFILE)
      if (activeProfile) {
        return JSON.parse(activeProfile).id === id
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

.profile-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-list-wrapper {
  padding: 4px 2px 4px 16px;

  .profile-list {
    max-height: 130px;
    width: 100%;
    overflow-y: auto;
  }
}

.profile-list-empty {
  padding: 4px 0;
}

.content-divider {
  margin: 0;
}

.profile-button-tools {
  padding: 4px;
  display: flex;
  justify-content: flex-end;
}

</style>
