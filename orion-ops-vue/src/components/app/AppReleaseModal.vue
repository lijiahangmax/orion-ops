<template>
  <a-modal v-model="visible"
           :width="550"
           :maskStyle="{opacity: 0.8, animation: 'none'}"
           :dialogStyle="{top: '64px', padding: 0}"
           :maskClosable="false"
           :destroyOnClose="true">
    <!-- 标题 -->
    <template #title>
      <span v-if="selectAppPage">选择应用</span>
      <span v-if="!selectAppPage">
        <a-icon class="mx4 pointer span-blue"
                title="重新选择"
                v-if="visibleReselect && appId"
                @click="reselectAppList"
                type="arrow-left"/>
        应用发布
      </span>
    </template>
    <!-- 初始化骨架 -->
    <a-skeleton v-if="initiating" active :paragraph="{rows: 5}"/>
    <!-- 主体 -->
    <a-spin v-else :spinning="loading || appLoading">
      <!-- 应用选择 -->
      <div class="app-list-container" v-if="selectAppPage">
        <!-- 无应用数据 -->
        <a-empty v-if="!appList.length" description="请先配置应用"/>
        <!-- 应用列表 -->
        <div v-else class="app-list">
          <div class="app-item" v-for="app of appList" :key="app.id" @click="chooseApp(app.id)">
            <div class="app-name">
              <a-icon class="mx8" type="code-sandbox"/>
              {{ app.name }}
            </div>
            <a-tag color="#5C7CFA">
              {{ app.tag }}
            </a-tag>
          </div>
        </div>
      </div>
      <!-- 发布配置 -->
      <div class="release-container" v-else>
        <div class="release-form">
          <!-- 发布标题 -->
          <div class="release-form-item">
            <span class="release-form-item-label normal-label required-label">发布标题</span>
            <a-input class="release-form-item-input"
                     v-model="submit.title"
                     placeholder="标题"
                     :maxLength="32"
                     allowClear/>
          </div>
          <!-- 发布版本 -->
          <div class="release-form-item">
            <span class="release-form-item-label normal-label required-label">发布版本</span>
            <a-select class="release-form-item-input build-selector"
                      v-model="submit.buildId"
                      placeholder="版本"
                      allowClear>
              <a-select-option v-for="build of buildList" :key="build.id" :value="build.id">
                <div class="build-item">
                  <div class="build-item-left">
                    <span class="span-blue build-item-seq">#{{ build.seq }}</span>
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
          <!-- 发布类型 -->
          <div class="release-form-item">
            <span class="release-form-item-label normal-label required-label">发布类型</span>
            <a-radio-group v-model="submit.timedRelease" buttonStyle="solid">
              <a-radio-button :value="type.value" v-for="type in $enum.TIMED_TYPE" :key="type.value">
                {{ type.releaseLabel }}
              </a-radio-button>
            </a-radio-group>
          </div>
          <!-- 调度时间 -->
          <div class="release-form-item" v-if="submit.timedRelease === $enum.TIMED_TYPE.TIMED.value">
            <span class="release-form-item-label normal-label required-label">调度时间</span>
            <a-date-picker v-model="submit.timedReleaseTime" :showTime="true" format="YYYY-MM-DD HH:mm:ss"/>
          </div>
          <!-- 发布机器 -->
          <div class="release-form-item">
            <span class="release-form-item-label normal-label required-label">发布机器</span>
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
            <span class="release-form-item-label normal-label">发布描述</span>
            <a-textarea class="release-form-item-input"
                        v-model="submit.description"
                        style="height: 50px; width: 430px"
                        :maxLength="64"
                        allowClear/>
          </div>
        </div>
      </div>
    </a-spin>
    <!-- 页脚 -->
    <template #footer>
      <!-- 关闭 -->
      <a-button @click="close">关闭</a-button>
      <!-- 发布 -->
      <a-button type="primary"
                :loading="loading"
                :disabled="selectAppPage || loading || appLoading || initiating"
                @click="release">
        发布
      </a-button>
    </template>
  </a-modal>
</template>

<script>
import { formatDate } from '@/lib/filters'
import _enum from '@/lib/enum'
import MachineChecker from '@/components/machine/MachineChecker'

export default {
  name: 'AppReleaseModal',
  components: {
    MachineChecker
  },
  props: {
    visibleReselect: Boolean
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
        title: undefined,
        buildId: undefined,
        description: undefined,
        timedRelease: _enum.TIMED_TYPE.NORMAL.value,
        timedReleaseTime: undefined,
        machineIdList: []
      },
      visible: false,
      loading: false,
      appLoading: false,
      initiating: false
    }
  },
  methods: {
    async openRelease(profileId, id) {
      if (!profileId) {
        this.$message.warning('请先维护应用环境')
        return
      }
      this.cleanData()
      this.selectAppPage = !id
      this.profileId = profileId
      this.appId = null
      this.app = null
      this.appList = []
      this.loading = false
      this.appLoading = false
      this.initiating = true
      this.visible = true
      await this.loadAppList()
      if (id) {
        this.chooseApp(id)
      }
      this.initiating = false
    },
    async chooseApp(id) {
      this.cleanData()
      this.appId = id
      this.selectAppPage = false
      const filter = this.appList.filter(s => s.id === id)
      if (!filter.length) {
        this.$message.warning('未找到该应用')
      }
      this.app = filter[0]
      this.submit.title = `发布${this.app.name}`
      this.appLoading = true
      await this.loadReleaseMachine()
      await this.loadBuildList()
      this.appLoading = false
    },
    cleanData() {
      this.app = {}
      this.appId = null
      this.buildList = []
      this.appMachineIdList = [-1]
      this.submit.title = undefined
      this.submit.buildId = undefined
      this.submit.description = undefined
      this.submit.timedRelease = this.$enum.TIMED_TYPE.NORMAL.value
      this.submit.timedReleaseTime = undefined
      this.submit.machineIdList = []
    },
    async loadReleaseMachine() {
      const { data } = await this.$api.getAppMachineId({
        id: this.appId,
        profileId: this.profileId
      })
      if (data && data.length) {
        this.appMachineIdList = data
        this.submit.machineIdList = data
      } else {
        this.$message.warning('请先配置应用发布机器')
      }
    },
    async loadBuildList() {
      const { data } = await this.$api.getBuildReleaseList({
        appId: this.appId,
        profileId: this.profileId
      })
      this.buildList = data
      if (!this.submit.buildId && this.buildList && this.buildList.length) {
        this.submit.buildId = this.buildList[0].id
      }
    },
    async loadAppList() {
      const { data: { rows } } = await this.$api.getAppList({
        profileId: this.profileId,
        limit: 10000
      })
      this.appList = rows.filter(s => s.isConfig === this.$enum.CONFIG_STATUS.CONFIGURED.value)
    },
    async reselectAppList() {
      this.selectAppPage = true
      if (this.appList.length) {
        return
      }
      this.initiating = true
      await this.loadAppList()
      this.initiating = false
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
      if (this.submit.timedRelease === this.$enum.TIMED_TYPE.TIMED.value) {
        if (!this.submit.timedReleaseTime) {
          this.$message.warning('请选择调度时间')
          return
        }
        if (this.submit.timedReleaseTime.unix() * 1000 < Date.now()) {
          this.$message.warning('调度时间需要大于当前时间')
          return
        }
      } else {
        this.submit.timedReleaseTime = undefined
      }
      this.loading = true
      this.$api.submitAppRelease({
        appId: this.appId,
        profileId: this.profileId,
        ...this.submit
      }).then(() => {
        this.$message.success('已创建发布任务')
        this.$emit('submit')
        this.visible = false
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
    formatDate
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
