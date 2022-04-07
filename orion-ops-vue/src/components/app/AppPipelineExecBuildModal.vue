<template>
  <a-modal v-model="visible"
           v-drag-modal
           :title="title"
           :width="560"
           :maskStyle="{opacity: 0.4, animation: 'none'}"
           :bodyStyle="{padding: '24px 24px 0 24px'}"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="check"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-form-model v-bind="layout">
        <!-- 分支 -->
        <a-form-model-item label="分支" v-if="detail.vcsId" required>
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
        </a-form-model-item>
        <!-- commit -->
        <a-form-model-item label="commit" v-if="detail.vcsId" required>
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
        </a-form-model-item>
        <!-- 构建描述 -->
        <a-form-model-item label="构建描述">
          <a-textarea v-model="submit.description" :maxLength="64" allowClear/>
        </a-form-model-item>
      </a-form-model>
    </a-spin>
  </a-modal>
</template>

<script>

import _filters from '@/lib/filters'

const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 }
}

export default {
  name: 'AppPipelineExecBuildModal',
  data: function() {
    return {
      visible: false,
      loading: false,
      title: null,
      detail: {},
      branchList: [],
      commitList: [],
      submit: {
        branchName: null,
        commitId: null,
        description: null
      },
      layout
    }
  },
  methods: {
    open(detail) {
      this.detail = { ...detail }
      this.title = `${detail.appName} 构建配置`
      this.branchList = []
      this.commitList = []
      this.submit.branchName = detail.branchName
      this.submit.commitId = detail.commitId
      this.submit.description = detail.description
      this.visible = true
      this.loadVcs()
    },
    loadVcs() {
      if (!this.detail.vcsId) {
        return
      }
      this.loading = true
      this.$api.getVcsInfo({
        id: this.detail.vcsId,
        appId: this.detail.appId,
        profileId: this.detail.profileId
      }).then(({ data }) => {
        this.loading = false
        this.branchList = data.branches
        // 分支列表
        const defaultBranch = this.branchList.filter(s => s.isDefault === 1)
        if (this.submit.branchName) {
          // nothing
        } else if (defaultBranch && defaultBranch.length) {
          this.submit.branchName = defaultBranch[0].name
        } else {
          this.submit.branchName = null
        }
        // 提交列表
        this.commitList = data.commits
        if (this.submit.commitId) {
          // nothing
        } else if (data.commits && data.commits.length) {
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
        id: this.detail.vcsId
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
        id: this.detail.vcsId,
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
    check() {
      if (this.detail.vcsId) {
        if (!this.submit.branchName) {
          this.$message.warning('请选择分支')
          return
        }
        if (!this.submit.commitId) {
          this.$message.warning('请选择commit')
          return
        }
      }
      this.$emit('ok', this.detail.id, this.submit)
      this.close()
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

.build-form-item-input {
  width: 346px;
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
    width: 174px;
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
