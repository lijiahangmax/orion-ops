<template>
  <a-modal v-model="visible"
           okText="发布"
           :width="550"
           :destroyOnClose="true"
           :okButtonProps="{props: {disabled: loading}}"
           @ok="release"
           @cancel="close">
    <!-- 标题 -->
    <template #title>
      <a-icon class="ml4 pointer span-blue"
              title="重新选择"
              v-if="appId != null" @click="reselectApp"
              type="arrow-left"/>
      <span class="ml16">应用发布</span>
    </template>
    <!-- 应用选择 -->
    <div class="app-list" v-if="selectAppPage">
      <div v-if="loading">
        <a-skeleton active :paragraph="{rows: 6}"/>
      </div>
      <div v-else-if="appList.length">
        <div class="app-item" v-for="app of appList" :key="app.id" @click="chooseApp(app)">
          <div class="app-name">
            <a-icon class="mx8" type="code-sandbox"/>
            {{ app.name }}
          </div>
          <a-tag color="#5C7CFA">
            {{ app.tag }}
          </a-tag>
        </div>
      </div>
      <div v-else-if="!appList.length">
        <a-empty style="margin-top: 10%" description="请先配置应用"/>
      </div>
    </div>
    <!-- 发布配置 -->
    <a-spin :spinning="loading" v-else>
      <div class="release-form">
        <!-- 发布标题 -->
        <div class="release-form-item">
          <span class="release-form-item-label">
            <span class="span-red">* </span>
            发布标题 :
          </span>
          <a-input class="release-form-item-input"
                   v-model="submit.title"
                   placeholder="标题"
                   :maxLength="32"
                   allowClear/>
        </div>
        <!-- 发布版本 -->
        <div class="release-form-item">
          <span class="release-form-item-label">
            <span class="span-red">* </span>
            发布版本 :
          </span>
          <a-select class="release-form-item-input build-selector"
                    v-model="submit.buildId"
                    placeholder="版本"
                    allowClear>
            <a-select-option v-for="build of buildList" :key="build.id" :value="build.id">
              <div class="build-item">
                <div class="build-item-left">
                  <a-tag class="build-item-seq" color="#5C7CFA">
                    #{{ build.seq }}
                  </a-tag>
                  <span class="build-item-message">{{ build.description }}</span>
                </div>
                <span class="build-item-date">
                  {{ build.createTime | formatDate('MM-dd HH:mm') }}
                </span>
              </div>
            </a-select-option>
          </a-select>
          <a-icon type="reload" class="reload" title="刷新" @click="loadBuildList"/>
        </div>
        <!-- 发布机器 -->
        <div class="release-form-item">
          <span class="release-form-item-label">
            <span class="span-red">* </span>
            发布机器 :
          </span>
          <MachineChecker ref="machineChecker"
                          class="release-form-item-input"
                          placement="bottomLeft"
                          :defaultValue="appMachineIdList"
                          :query="{idList: appMachineIdList}">
            <template #trigger>
              <span class="span-blue pointer">已选择 {{ submit.machineIdList.length }} 台机器</span>
            </template>
            <template #footer>
              <a-button type="primary" size="small" @click="chooseMachines">确定</a-button>
            </template>
          </MachineChecker>
        </div>
        <!-- 描述 -->
        <div class="release-form-item" style="margin: 8px 0;">
          <span class="release-form-item-label">发布描述 : </span>
          <a-textarea class="build-form-item-input"
                      v-model="submit.description"
                      style="height: 50px; width: 430px"
                      :maxLength="64"/>
        </div>
      </div>
    </a-spin>
  </a-modal>
</template>

<script>

import MachineChecker from '@/components/machine/MachineChecker'
import _filters from '@/lib/filters'

export default {
  name: 'AppReleaseModal',
  components: {
    MachineChecker
  },
  data: function() {
    return {
      selectAppPage: true,
      appId: null,
      profileId: null,
      app: null,
      appList: [],
      buildList: [],
      appMachineIdList: [-1],
      submit: {
        title: null,
        buildId: null,
        description: null,
        machineIdList: []
      },
      visible: false,
      loading: false
    }
  },
  methods: {
    async openRelease(profileId, appId) {
      if (!profileId) {
        this.$message.warning('请先维护应用环境')
        return
      }
      this.cleanData()
      this.selectAppPage = !appId
      this.visible = true
      this.loading = false
      this.profileId = profileId
      this.appId = null
      this.app = null
      this.appList = []
      await this.loadAppList(profileId)
      if (appId) {
        const selectedApp = this.appList.filter(s => s.id === appId)
        if (selectedApp.length) {
          this.chooseApp(selectedApp[0])
        } else {
          this.$message.warning('未找到该应用')
        }
      }
    },
    chooseApp(app) {
      this.selectAppPage = false
      this.app = app
      this.appId = app.id
      this.cleanData()
      this.loadReleaseMachine()
      this.loadBuildList()
    },
    cleanData() {
      this.buildList = []
      this.appMachineIdList = [-1]
      this.submit.title = null
      this.submit.buildId = null
      this.submit.description = null
      this.submit.machineIdList = []
    },
    loadBuildList() {
      this.$api.getBuildReleaseList({
        appId: this.appId,
        profileId: this.profileId
      }).then(({ data }) => {
        this.buildList = data
        if (!this.submit.buildId && this.buildList && this.buildList.length) {
          this.submit.buildId = this.buildList[0].id
        }
      })
    },
    loadReleaseMachine() {
      this.$api.getAppMachineId({
        id: this.appId,
        profileId: this.profileId
      }).then(({ data }) => {
        if (data && data.length) {
          this.appMachineIdList = data
          this.submit.machineIdList = data
        } else {
          this.$message.warning('请先配置应用发布机器')
        }
      })
    },
    async loadAppList() {
      this.loading = true
      this.app = null
      this.appList = []
      await this.$api.getAppList({
        profileId: this.profileId,
        limit: 10000
      }).then(({ data }) => {
        this.loading = false
        if (data.rows && data.rows.length) {
          this.appList = data.rows.filter(s => s.isConfig === this.$enum.CONFIG_STATUS.CONFIGURED.value)
        }
      }).catch(() => {
        this.loading = false
      })
    },
    reselectApp() {
      this.appId = null
      this.app = null
      this.selectAppPage = true
      if (!this.appList.length) {
        this.loadAppList()
      }
    },
    chooseMachines() {
      const ref = this.$refs.machineChecker
      if (!ref.checkedList.length) {
        this.$message.warning('请选择发布机器机器')
        return
      }
      this.submit.machineIdList = ref.checkedList
      ref.hide()
    },
    async release() {
      if (!this.app) {
        this.$message.warning('请选择发布应用')
        return
      }
      if (!this.submit.title) {
        this.$message.warning('请输入发布标题')
        return
      }
      if (!this.submit.buildId) {
        this.$message.warning('请选择发布版本')
        return
      }
      if (!this.submit.machineIdList.length) {
        this.$message.warning('请选择发布机器')
        return
      }
      this.loading = true
      this.$api.submitAppRelease({
        appId: this.appId,
        profileId: this.profileId,
        ...this.submit
      }).then(() => {
        this.$message.success('已提交发布请求')
        this.$emit('submit')
        this.close()
      }).catch(() => {
        this.loading = false
      })
    },
    close() {
      this.visible = false
      this.loading = false
    }
  },
  filters: {
    ..._filters
  }
}
</script>

<style lang="less" scoped>

/deep/ .ant-modal-body {
  padding: 8px;
}

.app-list {
  margin: 0 4px 0 8px;
  height: 355px;
  overflow-y: auto;

  .app-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 4px;
    padding: 4px 4px 4px 8px;
    background: #F8F9FA;
    border-radius: 4px;
    height: 40px;
    cursor: pointer;

    .app-name {
      width: 300px;
      text-overflow: ellipsis;
      display: block;
      overflow-x: hidden;
      white-space: nowrap;
    }
  }

  .app-item:hover {
    background: #E7F5FF;
  }
}

.release-form {
  display: flex;
  flex-wrap: wrap;

  .release-form-item {
    display: flex;
    align-items: center;
    width: 100%;

    .release-form-item-label {
      width: 80px;
      margin: 16px 8px;
      text-align: end;
      display: block;
      font-size: 15px;
    }

    .release-form-item-input {
      width: 380px;
    }

    .reload {
      font-size: 19px;
      margin-left: 16px;
      cursor: pointer;
      color: #339AF0;
    }

    .reload:hover {
      color: #228BE6;
    }
  }
}

.build-item {
  display: flex;
  justify-content: space-between;

  .build-item-left {
    display: flex;
    align-items: center;
    width: 276px;
  }

  .build-item-seq {
    margin-right: 8px;
  }

  .build-item-message {
    width: 225px;
    display: block;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow-x: hidden;
  }

  .build-item-date {
    font-size: 12px;
    margin-right: 4px;
  }
}

.build-selector /deep/ .ant-select-selection-selected-value {
  width: 100%;
}

</style>
