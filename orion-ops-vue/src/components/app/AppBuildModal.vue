<template>
  <a-modal v-model="visible"
           okText="构建"
           :width="550"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           @ok="build"
           @cancel="close">
    <!-- 标题 -->
    <template #title>
      <a-icon class="ml4 pointer span-blue"
              title="重新选择"
              v-if="appId != null" @click="reselectApp"
              type="arrow-left"/>
      <span class="ml16">应用构建</span>
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
    <!-- 构建配置 -->
    <a-spin :spinning="loading" v-else>
      <div class="build-form">
        <!-- 分支 -->
        <div class="build-form-item" v-if="app && app.vcsId">
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
        <div class="build-form-item" v-if="app && app.vcsId">
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
    </a-spin>
  </a-modal>
</template>

<script>

import _filters from '@/lib/filters'

export default {
  name: 'AppBuildModal',
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
      loading: false
    }
  },
  methods: {
    async openBuild(profileId, appId) {
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
        this.appId = appId
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
      if (this.app.vcsId) {
        this.loadVcs()
      }
    },
    cleanData() {
      this.branchList = []
      this.commitList = []
      this.submit.branchName = null
      this.submit.commitId = null
      this.submit.description = null
    },
    loadVcs() {
      this.loading = true
      this.$api.getVcsInfo({
        id: this.app.vcsId,
        appId: this.appId,
        profileId: this.profileId
      }).then(({ data }) => {
        this.loading = false
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
      }).catch(() => {
        this.loading = false
      })
    },
    reloadBranch() {
      this.loading = true
      this.$api.getVcsBranchList({
        id: this.app.vcsId
      }).then(({ data }) => {
        this.loading = false
        this.branchList = data
      }).catch(() => {
        this.loading = false
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
      this.loading = true
      this.$api.getVcsCommitList({
        id: this.app.vcsId,
        branchName: this.submit.branchName
      }).then(({ data }) => {
        this.loading = false
        this.commitList = data
        if (data && data.length) {
          this.submit.commitId = this.commitList[0].id
        } else {
          this.submit.commitId = null
        }
      }).catch(() => {
        this.loading = false
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
    async build() {
      if (!this.app) {
        this.$message.warning('请选择构建应用')
        return
      }
      if (this.app.vcsId) {
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
        this.close()
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

.commit-selector /deep/ .ant-select-selection-selected-value {
  width: 100%;
}

</style>
