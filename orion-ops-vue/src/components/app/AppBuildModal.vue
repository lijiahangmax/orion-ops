<template>
  <a-modal v-model="visible"
           :width="550"
           :maskStyle="{opacity: 0.8, animation: 'none'}"
           :dialogStyle="{top: '64px', padding: 0}"
           :bodyStyle="{padding: '8px'}"
           :maskClosable="false"
           :destroyOnClose="true">
    <!-- 页头 -->
    <template #title>
      <span v-if="selectAppPage">选择应用</span>
      <span v-if="!selectAppPage">
        <a-icon class="mx4 pointer span-blue"
                title="重新选择"
                v-if="visibleReselect && appId"
                @click="reselectAppList"
                type="arrow-left"/>
        应用构建
      </span>
    </template>
    <!-- 初始化骨架 -->
    <a-skeleton v-if="initiating" active :paragraph="{rows: 4}"/>
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
      <!-- 构建配置 -->
      <div class="build-container" v-else>
        <div class="build-form">
          <!-- 分支 -->
          <div class="build-form-item" v-if="app && app.repoId">
            <span class="build-form-item-label normal-label required-label">分支</span>
            <a-select class="build-form-item-input"
                      v-model="submit.branchName"
                      placeholder="分支"
                      @change="reloadCommit"
                      allowClear>
              <a-select-option v-for="branch of branchList" :key="branch.name" :value="branch.name">
                {{ branch.name }}
              </a-select-option>
            </a-select>
            <a-icon type="reload" class="reload" title="刷新" @click="reloadBranch"/>
          </div>
          <!-- commit -->
          <div class="build-form-item" v-if="app && app.repoId">
            <span class="build-form-item-label normal-label required-label">commit</span>
            <a-select class="build-form-item-input commit-selector"
                      v-model="submit.commitId"
                      placeholder="提交记录"
                      allowClear>
              <a-select-option v-for="commit of commitList" :key="commit.id" :value="commit.id">
                <div class="commit-item">
                  <div class="commit-item-left">
                    <span class="commit-item-id">{{ commit.id.substring(0, 7) }}</span>
                    <span class="commit-item-message">{{ commit.message }}</span>
                  </div>
                  <span class="commit-item-date">
                  {{ commit.time | formatDate('MM-dd HH:mm' ) }}
                </span>
                </div>
              </a-select-option>
            </a-select>
            <a-icon type="reload" class="reload" title="刷新" @click="reloadCommit"/>
          </div>
          <!-- 描述 -->
          <div class="build-form-item" style="margin: 8px 0;">
            <span class="build-form-item-label normal-label">构建描述</span>
            <a-textarea class="build-form-item-input"
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
      <!-- 构建 -->
      <a-button type="primary"
                :loading="loading"
                :disabled="selectAppPage || loading || appLoading || initiating"
                @click="build">
        构建
      </a-button>
    </template>
  </a-modal>
</template>

<script>
import { formatDate } from '@/lib/filters'
import { CONFIG_STATUS } from '@/lib/enum'

export default {
  name: 'AppBuildModal',
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
      branchList: [],
      commitList: [],
      submit: {
        branchName: null,
        commitId: null,
        description: null
      },
      visible: false,
      loading: false,
      appLoading: false,
      initiating: false
    }
  },
  methods: {
    async openBuild(profileId, id) {
      if (!profileId) {
        this.$message.warning('请先维护应用环境')
        return
      }
      this.profileId = profileId
      this.appList = []
      this.cleanData()
      this.selectAppPage = !id
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
      if (this.app.repoId) {
        this.appLoading = true
        await this.loadRepository()
        this.appLoading = false
      }
    },
    async loadAppList() {
      const { data: { rows } } = await this.$api.getAppList({
        profileId: this.profileId,
        limit: 10000
      })
      this.appList = rows.filter(s => s.isConfig === CONFIG_STATUS.CONFIGURED.value)
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
    cleanData() {
      this.app = {}
      this.appId = null
      this.branchList = []
      this.commitList = []
      this.submit.branchName = null
      this.submit.commitId = null
      this.submit.description = null
    },
    async loadRepository() {
      await this.$api.getRepositoryInfo({
        id: this.app.repoId,
        appId: this.appId,
        profileId: this.profileId
      }).then(({ data }) => {
        this.branchList = data.branches
        // 分支列表
        const defaultBranch = this.branchList.filter(s => s.isDefault === 1)
        if (defaultBranch && defaultBranch.length) {
          this.submit.branchName = defaultBranch[0].name
        } else {
          this.submit.branchName = null
        }
        // 提交列表
        this.commitList = data.commits
        if (data.commits && data.commits.length) {
          this.submit.commitId = this.commitList[0].id
        } else {
          this.submit.commitId = null
        }
      })
    },
    reloadBranch() {
      this.appLoading = true
      this.$api.getRepositoryBranchList({
        id: this.app.repoId
      }).then(({ data }) => {
        this.appLoading = false
        this.branchList = data
      }).catch(() => {
        this.appLoading = false
      })
    },
    reloadCommit() {
      if (!this.submit.branchName) {
        this.commitList = []
        this.submit.commitId = undefined
        return
      }
      if (!this.submit.branchName) {
        this.$message.warning('请先选择分支')
        return
      }
      this.appLoading = true
      this.$api.getRepositoryCommitList({
        id: this.app.repoId,
        branchName: this.submit.branchName
      }).then(({ data }) => {
        this.appLoading = false
        this.commitList = data
        if (data && data.length) {
          this.submit.commitId = this.commitList[0].id
        } else {
          this.submit.commitId = null
        }
      }).catch(() => {
        this.appLoading = false
      })
    },
    async build() {
      if (!this.app) {
        this.$message.warning('请选择构建应用')
        return
      }
      if (this.app.repoId) {
        if (!this.submit.branchName) {
          this.$message.warning('请选择分支')
          return
        }
        if (!this.submit.commitId) {
          this.$message.warning('请选择commit')
          return
        }
      }
      this.loading = true
      this.$api.submitAppBuild({
        appId: this.appId,
        profileId: this.profileId,
        ...this.submit
      }).then(() => {
        this.$message.success('已开始构建')
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

.build-form {
  display: flex;
  flex-wrap: wrap;

  .build-form-item {
    display: flex;
    align-items: center;
    width: 100%;

    .build-form-item-label {
      width: 70px;
      margin: 16px 8px;
      font-size: 15px;
    }

    .build-form-item-input {
      width: 390px;
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

.commit-item {
  display: flex;
  justify-content: space-between;

  .commit-item-left {
    display: flex;
    width: 285px;
  }

  .commit-item-id {
    width: 50px;
    margin-right: 8px;
    color: #000;
  }

  .commit-item-message {
    width: 227px;
    display: block;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow-x: hidden;
  }

  .commit-item-date {
    font-size: 12px;
    margin-right: 4px;
  }
}

::v-deep .commit-selector .ant-select-selection-selected-value {
  width: 100%;
}

</style>
